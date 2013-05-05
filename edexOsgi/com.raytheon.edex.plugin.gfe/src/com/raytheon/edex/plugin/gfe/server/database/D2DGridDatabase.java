/**
 * This software was developed and / or modified by Raytheon Company,
 * pursuant to Contract DG133W-05-CQ-1067 with the US Government.
 * 
 * U.S. EXPORT CONTROLLED TECHNICAL DATA
 * This software product contains export-restricted data whose
 * export/transfer/disclosure is restricted by U.S. law. Dissemination
 * to non-U.S. persons whether in the United States or abroad requires
 * an export license or other authorization.
 * 
 * Contractor Name:        Raytheon Company
 * Contractor Address:     6825 Pine Street, Suite 340
 *                         Mail Stop B8
 *                         Omaha, NE 68106
 *                         402.291.0100
 * 
 * See the AWIPS II Master Rights File ("Master Rights File.pdf") for
 * further licensing information.
 **/

package com.raytheon.edex.plugin.gfe.server.database;

import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;

import com.raytheon.edex.plugin.gfe.config.IFPServerConfig;
import com.raytheon.edex.plugin.gfe.db.dao.GFED2DDao;
import com.raytheon.edex.plugin.gfe.paraminfo.GridParamInfo;
import com.raytheon.edex.plugin.gfe.paraminfo.GridParamInfoLookup;
import com.raytheon.edex.plugin.gfe.paraminfo.ParameterInfo;
import com.raytheon.edex.plugin.gfe.server.GridParmManager;
import com.raytheon.uf.common.comm.CommunicationException;
import com.raytheon.uf.common.dataplugin.PluginException;
import com.raytheon.uf.common.dataplugin.gfe.GridDataHistory;
import com.raytheon.uf.common.dataplugin.gfe.RemapGrid;
import com.raytheon.uf.common.dataplugin.gfe.db.objects.DatabaseID;
import com.raytheon.uf.common.dataplugin.gfe.db.objects.DatabaseID.DataType;
import com.raytheon.uf.common.dataplugin.gfe.db.objects.GFERecord.GridType;
import com.raytheon.uf.common.dataplugin.gfe.db.objects.GridLocation;
import com.raytheon.uf.common.dataplugin.gfe.db.objects.GridParmInfo;
import com.raytheon.uf.common.dataplugin.gfe.db.objects.ParmID;
import com.raytheon.uf.common.dataplugin.gfe.db.objects.TimeConstraints;
import com.raytheon.uf.common.dataplugin.gfe.exception.GfeException;
import com.raytheon.uf.common.dataplugin.gfe.grid.Grid2DFloat;
import com.raytheon.uf.common.dataplugin.gfe.server.message.ServerResponse;
import com.raytheon.uf.common.dataplugin.gfe.slice.IGridSlice;
import com.raytheon.uf.common.dataplugin.gfe.slice.ScalarGridSlice;
import com.raytheon.uf.common.dataplugin.gfe.slice.VectorGridSlice;
import com.raytheon.uf.common.dataplugin.grid.GridPathProvider;
import com.raytheon.uf.common.dataplugin.grid.GridRecord;
import com.raytheon.uf.common.dataplugin.level.Level;
import com.raytheon.uf.common.dataplugin.level.mapping.LevelMapping;
import com.raytheon.uf.common.dataplugin.level.mapping.LevelMappingFactory;
import com.raytheon.uf.common.datastorage.DataStoreFactory;
import com.raytheon.uf.common.datastorage.IDataStore;
import com.raytheon.uf.common.datastorage.Request;
import com.raytheon.uf.common.datastorage.records.FloatDataRecord;
import com.raytheon.uf.common.datastorage.records.IDataRecord;
import com.raytheon.uf.common.gridcoverage.GridCoverage;
import com.raytheon.uf.common.message.WsId;
import com.raytheon.uf.common.parameter.mapping.ParameterMapper;
import com.raytheon.uf.common.status.IPerformanceStatusHandler;
import com.raytheon.uf.common.status.IUFStatusHandler;
import com.raytheon.uf.common.status.PerformanceStatus;
import com.raytheon.uf.common.status.UFStatus;
import com.raytheon.uf.common.status.UFStatus.Priority;
import com.raytheon.uf.common.time.TimeRange;
import com.raytheon.uf.common.time.util.TimeUtil;
import com.raytheon.uf.common.util.mapping.MultipleMappingException;
import com.raytheon.uf.edex.database.DataAccessLayerException;

/**
 * Singleton that assists with grid data
 * 
 * <pre>
 * SOFTWARE HISTORY
 * Date         Ticket#     Engineer    Description
 * ------------ ----------  ----------- --------------------------
 * 05/16/08     875         bphillip    Initial Creation
 * 06/17/08     #940        bphillip    Implemented GFE Locking
 * 07/23/09     2342        ryu         Check for null gridConfig in getGridParmInfo
 * 03/02/12     DR14651     ryu         change time independency of staticTopo, staticCoriolis, staticSpacing
 * 05/04/12     #574        dgilling    Implement missing methods from GridDatabase.
 * 09/12/12     #1117       dgilling    Fix getParmList() so it returns all parms defined
 *                                      in the GribParamInfo file.
 * 10/10/12     #1260       randerso    Changed to only retrieve slab containing overlapping
 *                                      data instead of full grid. Added logging to support
 *                                      GFE performance testing
 * 03/19/2013   #1774       randerso    Fix accumulative grid time ranges
 * 04/04/2013   #1774       randerso    Moved wind component checking to GfeIngestNotificaionFilter
 * 04/04/2013   #1787       randerso    Move the D2D to GFE translation logic out of GFED2DDao
 * 04/17/2013   #1913       randerso    Added GFE level mapping to replace GridTranslator
 * 
 * </pre>
 * 
 * @author bphillip
 * @version 1.0
 */
public class D2DGridDatabase extends VGridDatabase {
    private static final transient IUFStatusHandler statusHandler = UFStatus
            .getHandler(D2DGridDatabase.class);

    /**
     * Retrieve the gfe DatabaseID for a given d2d model run
     * 
     * @param d2dModelName
     * @param modelTime
     * @param config
     * @return
     */
    public static DatabaseID getDbId(String d2dModelName, Date modelTime,
            IFPServerConfig config) {
        String gfeModelName = config.gfeModelNameMapping(d2dModelName);
        if (gfeModelName == null || gfeModelName.isEmpty()) {
            return null;
        }
        return new DatabaseID(getSiteID(config), DataType.GRID, "D2D",
                gfeModelName, modelTime);
    }

    /**
     * Retrieves DatabaseIDs for all model runs of a given d2dModelName
     * 
     * @param d2dModelName
     *            desired d2d model name
     * @param gfeModel
     *            gfe model name to use
     * @param siteID
     *            siteID to use in databaseId
     * @param maxRecords
     *            max number of model runs to return
     * @return
     * @throws DataAccessLayerException
     */
    public static List<DatabaseID> getD2DDatabaseIdsFromDb(
            IFPServerConfig config, String d2dModelName)
            throws DataAccessLayerException {
        return getD2DDatabaseIdsFromDb(config, d2dModelName, -1);
    }

    /**
     * Retrieves DatabaseIDs for the n most recent model runs of a given
     * d2dModelName
     * 
     * @param d2dModelName
     *            desired d2d model name
     * @param maxRecords
     *            max number of model runs to return
     * @return
     * @throws DataAccessLayerException
     */
    public static List<DatabaseID> getD2DDatabaseIdsFromDb(
            IFPServerConfig config, String d2dModelName, int maxRecords)
            throws DataAccessLayerException {

        try {
            GFED2DDao dao = new GFED2DDao();
            List<Date> result = dao.getModelRunTimes(d2dModelName, maxRecords);

            List<DatabaseID> dbInventory = new ArrayList<DatabaseID>();
            for (Date date : result) {
                DatabaseID dbId = null;
                dbId = getDbId(d2dModelName, date, config);
                try {
                    GridDatabase db = GridParmManager.getDb(dbId);
                    if ((db != null) && !dbInventory.contains(dbId)) {
                        dbInventory.add(dbId);
                    }
                } catch (GfeException e) {
                    statusHandler.handle(Priority.PROBLEM,
                            e.getLocalizedMessage(), e);
                }
            }
            return dbInventory;
        } catch (PluginException e) {
            statusHandler.handle(Priority.PROBLEM, e.getLocalizedMessage(), e);
            return Collections.emptyList();
        }
    }

    // regex to match parmnnhr
    private static final Pattern parmHrPattern = Pattern
            .compile("(\\D+)\\d+hr");

    private static final String GFE_LEVEL_MAPPING_FILE = "grid/gfeLevelMappingFile.xml";

    private final IPerformanceStatusHandler perfLog = PerformanceStatus
            .getHandler("GFE:");

    public static class D2DParm {
        private ParmID parmId;

        private GridParmInfo gpi;

        private Map<Integer, TimeRange> fcstHrToTimeRange;

        private Map<TimeRange, Integer> timeRangeToFcstHr;

        private String[] components;

        private Level level;

        public D2DParm(ParmID parmId, GridParmInfo gpi,
                Map<Integer, TimeRange> fcstHrToTimeRange, String... components) {
            this.parmId = parmId;
            this.gpi = gpi;
            this.fcstHrToTimeRange = fcstHrToTimeRange;
            this.components = components;

            this.timeRangeToFcstHr = new HashMap<TimeRange, Integer>(
                    fcstHrToTimeRange.size(), 1.0f);

            for (Entry<Integer, TimeRange> entry : fcstHrToTimeRange.entrySet()) {
                this.timeRangeToFcstHr.put(entry.getValue(), entry.getKey());
            }

            this.level = getD2DLevel(parmId.getParmLevel());
        }

        public ParmID getParmId() {
            return parmId;
        }

        public GridParmInfo getGpi() {
            return gpi;
        }

        public Map<Integer, TimeRange> getFcstHrToTimeRange() {
            return fcstHrToTimeRange;
        }

        public Map<TimeRange, Integer> getTimeRangeToFcstHr() {
            return timeRangeToFcstHr;
        }

        public String[] getComponents() {
            return components;
        }

        public Level getLevel() {
            return level;
        }

        @Override
        public String toString() {
            return this.parmId.toString();
        }
    }

    private String d2dModelName;

    private Date refTime;

    private GridParamInfo modelInfo;

    private List<TimeRange> availableTimes;

    /** The remap object used for resampling grids */
    private final Map<Integer, RemapGrid> remap = new HashMap<Integer, RemapGrid>();

    /** The destination GridLocation (The local GFE grid coverage) */
    private GridLocation outputGloc;

    private Map<ParmID, D2DParm> gfeParms = new HashMap<ParmID, D2DParm>();

    private Map<String, D2DParm> d2dParms = new HashMap<String, D2DParm>();

    /**
     * Constructs a new D2DGridDatabase
     * 
     * @param dbId
     *            The database ID of this database
     */
    public D2DGridDatabase(IFPServerConfig config, String d2dModelName,
            Date refTime) throws GfeException {
        super(config);

        this.d2dModelName = d2dModelName;
        this.refTime = refTime;
        this.modelInfo = GridParamInfoLookup.getInstance().getGridParamInfo(
                d2dModelName);
        if (modelInfo == null) {
            throw new GfeException("No model info for: " + d2dModelName);
        }
        this.availableTimes = modelInfo.getAvailableTimes(refTime);

        // Get the database id for this database.
        this.dbId = getDbId(this.d2dModelName, this.refTime, this.config);
        this.valid = this.dbId.isValid();

        // get the output gloc'
        if (this.valid) {
            outputGloc = this.config.dbDomain();
            if (!outputGloc.isValid()) {
                this.valid = false;
            }
        }

        // create the parms
        if (this.valid) {
            loadParms();
        }
    }

    private RemapGrid getOrCreateRemap(GridCoverage awipsGrid)
            throws GfeException {
        RemapGrid remap = this.remap.get(awipsGrid.getId());
        if (remap == null) {
            String gfeModelName = dbId.getModelName();
            String d2dModelName = this.config.d2dModelNameMapping(gfeModelName);
            GridLocation inputLoc = new GridLocation(d2dModelName, awipsGrid);
            inputLoc.setSiteId(d2dModelName);
            Rectangle subdomain = NetCDFUtils.getSubGridDims(inputLoc,
                    this.outputGloc);

            // fix up coordinates for 0,0 in upper left in A2
            subdomain.y = inputLoc.gridSize().y - subdomain.y
                    - subdomain.height;

            if (subdomain.isEmpty()) {
                throw new GfeException(this.dbId
                        + ": GFE domain does not overlap dataset domain.");
            } else {
                GridLocation subGloc = new GridLocation(dbId.toString(),
                        inputLoc, subdomain);
                remap = new RemapGrid(subGloc, this.outputGloc);
                this.remap.put(awipsGrid.getId(), remap);
            }

        }
        return remap;
    }

    @Override
    public void updateDbs() {
        // no op
    }

    /**
     * Attempts to load the supplied ParmAtts using the given level.
     * 
     * Certain weather elements are handled differently, if they are considered
     * accumulative in nature, PoP, tp, cp are common examples.
     * 
     * This method loads only Scalar parms
     * 
     * @param atts
     * @param level
     */
    private void loadParm(ParameterInfo atts, String level) {
        List<String> accumParms = this.config.accumulativeD2DElements(this.dbId
                .getModelName());
        boolean accParm = false;
        if (accumParms.contains(atts.getShort_name())) {
            accParm = true;
        }

        ParmID pid = new ParmID(atts.getShort_name(), this.dbId, level);
        if (!pid.isValid()) {
            return;
        }

        TimeConstraints tc = getTimeConstraints(availableTimes);
        if (accParm) {
            tc = new TimeConstraints(tc.getRepeatInterval(),
                    tc.getRepeatInterval(), tc.getStartTime());
        }

        float minV = atts.getMinVal();
        float maxV = atts.getMaxVal();
        int precision = calcPrecision(minV, maxV);
        GridParmInfo gpi = new GridParmInfo(pid, this.outputGloc,
                GridType.SCALAR, atts.getUnits(), atts.getLong_name(), minV,
                maxV, precision, false, tc, accParm);
        if (!gpi.isValid()) {
            return;
        }

        // get possible inventory times
        HashMap<Integer, TimeRange> possibleInventorySlots;
        List<Integer> forecastTimes = this.modelInfo.getTimes();
        possibleInventorySlots = new HashMap<Integer, TimeRange>(
                forecastTimes.size(), 1.0f);
        if (accParm) {
            long millisDur = tc.getDuration() * TimeUtil.MILLIS_PER_SECOND;
            // adjust accumulative parms to have forecast hour
            // at end of time range
            for (int i = 0; i < forecastTimes.size(); i++) {
                possibleInventorySlots.put(forecastTimes.get(i), new TimeRange(
                        new Date(availableTimes.get(i).getStart().getTime()
                                - millisDur), millisDur));
            }
        } else if ((GridPathProvider.STATIC_PARAMETERS.contains(atts
                .getShort_name())) && !availableTimes.isEmpty()) {
            TimeRange ntr = availableTimes.get(0).combineWith(
                    availableTimes.get(availableTimes.size() - 1));
            // make static parms have a single time range that spans
            // all availableTimes
            for (int i = 0; i < forecastTimes.size(); i++) {
                possibleInventorySlots.put(forecastTimes.get(i), ntr);
            }
        } else {
            for (int i = 0; i < forecastTimes.size(); i++) {
                possibleInventorySlots.put(forecastTimes.get(i),
                        availableTimes.get(i));
            }
        }

        String gfeParmName = atts.getShort_name();
        String d2dParmName = getD2DParmName(gfeParmName);

        D2DParm d2dParm = new D2DParm(pid, gpi, possibleInventorySlots,
                d2dParmName);
        this.gfeParms.put(pid, d2dParm);
        this.d2dParms.put(compositeName(gfeParmName, level), d2dParm);
    }

    /**
     * Loads a vector parm using the supplied u and v components at the
     * specified level.
     * 
     * Vectors are never rate-parms.
     * 
     */
    private void loadParm(ParameterInfo uatts, ParameterInfo vatts, String level) {
        ParmID pid = new ParmID("wind", dbId, level);
        if (!pid.isValid()) {
            return;
        }

        TimeConstraints tc = getTimeConstraints(availableTimes);

        float maxV = uatts.getMaxVal();
        float minV = 0.0f;
        int precision = calcPrecision(minV, maxV);
        GridParmInfo gpi = new GridParmInfo(pid, outputGloc, GridType.VECTOR,
                uatts.getUnits(), "wind", minV, maxV, precision, false, tc,
                false);

        if (!gpi.isValid()) {
            return;
        }

        // get possible inventory times
        List<Integer> forecastTimes = this.modelInfo.getTimes();
        Map<Integer, TimeRange> possibleInventorySlots = new HashMap<Integer, TimeRange>(
                forecastTimes.size(), 1.0f);
        for (int i = 0; i < forecastTimes.size(); i++) {
            possibleInventorySlots.put(forecastTimes.get(i),
                    availableTimes.get(i));
        }

        String uGfeParmName = uatts.getShort_name();
        String uD2dParmName = getD2DParmName(uGfeParmName);

        String vGfeParmName = vatts.getShort_name();
        String vD2dParmName = getD2DParmName(vGfeParmName);

        D2DParm d2dParm = new D2DParm(pid, gpi, possibleInventorySlots,
                uD2dParmName, vD2dParmName);
        this.gfeParms.put(pid, d2dParm);
        this.d2dParms.put(compositeName(uGfeParmName, level), d2dParm);
        this.d2dParms.put(compositeName(vGfeParmName, level), d2dParm);
    }

    /**
     * Loads all of the parms it can.
     */
    private void loadParms() {
        // NOTE: this is returned as a copy so we are not modifying the copy in
        // modelInfo
        Collection<String> parmNames = modelInfo.getParmNames();

        // first see if we can make wind...
        if ((parmNames.contains("uw")) && (parmNames.contains("vw"))) {
            // replace uw and vw with wind.
            ParameterInfo uatts = this.modelInfo.getParameterInfo("uw");
            ParameterInfo vatts = this.modelInfo.getParameterInfo("vw");

            parmNames.remove("uw");
            parmNames.remove("vw");

            for (String level : uatts.getLevels()) {
                loadParm(uatts, vatts, level);
            }

        } else if ((parmNames.contains("ws")) && (parmNames.contains("wd"))) {
            // replace ws and wd with wind.
            ParameterInfo satts = this.modelInfo.getParameterInfo("ws");
            ParameterInfo datts = this.modelInfo.getParameterInfo("wd");

            parmNames.remove("ws");
            parmNames.remove("wd");

            for (String level : satts.getLevels()) {
                loadParm(satts, datts, level);
            }
        }

        // Now do all the scalars
        for (String parmName : parmNames) {
            ParameterInfo atts = this.modelInfo.getParameterInfo(parmName);

            for (String level : atts.getLevels()) {
                loadParm(atts, level);
            }
        }
    }

    @Override
    public ServerResponse<List<TimeRange>> getGridInventory(ParmID id) {
        ServerResponse<List<TimeRange>> sr = new ServerResponse<List<TimeRange>>();
        List<TimeRange> inventory;
        D2DParm parm = this.gfeParms.get(id);
        if (parm != null) {
            // get database inventory
            List<Integer> dbInv = null;
            try {
                GFED2DDao dao = new GFED2DDao();

                // get database inventory where all components are available
                for (String component : parm.getComponents()) {
                    List<Integer> compInv = dao.queryFcstHourByParmId(
                            d2dModelName, refTime, component, parm.getLevel());

                    if (dbInv == null) {
                        dbInv = compInv;
                    } else {
                        dbInv.retainAll(compInv);
                    }
                }
            } catch (Exception e) {
                statusHandler.handle(Priority.PROBLEM,
                        "Error retrieving inventory for " + id, e);
                dbInv = Collections.emptyList();
            }

            SortedSet<TimeRange> invSet = new TreeSet<TimeRange>();
            for (Integer forecastTime : dbInv) {
                TimeRange tr = parm.getFcstHrToTimeRange().get(forecastTime);
                if (tr != null) {
                    invSet.add(tr);
                } else {
                    statusHandler.warn("No time range found for "
                            + parm.getParmId() + " at forecast time "
                            + forecastTime);
                }
            }
            inventory = new ArrayList<TimeRange>(invSet);
        } else {
            sr.addMessage("Unknown PID: " + id);
            inventory = Collections.emptyList();
        }
        sr.setPayload(inventory);
        return sr;
    }

    public boolean isParmInfoDefined(ParmID id) {
        String mappedModel = config.d2dModelNameMapping(id.getDbId()
                .getModelName());
        return GridParamInfoLookup.getInstance().getParameterInfo(mappedModel,
                id.getParmName().toLowerCase()) != null;
    }

    @Override
    public ServerResponse<GridParmInfo> getGridParmInfo(ParmID id) {

        ServerResponse<GridParmInfo> sr = new ServerResponse<GridParmInfo>();
        GridParmInfo gpi = null;
        String mappedModel = config.d2dModelNameMapping(id.getDbId()
                .getModelName());

        if (id.getParmName().equalsIgnoreCase("wind")) {
            List<TimeRange> modelTimes = GridParamInfoLookup
                    .getInstance()
                    .getParameterTimes(mappedModel, id.getDbId().getModelDate());
            TimeConstraints tc = getTimeConstraints(modelTimes);

            // first try getting u-component attributes
            ParameterInfo atts = GridParamInfoLookup.getInstance()
                    .getParameterInfo(mappedModel, "uw");

            // if not found try wind speed
            if (atts == null) {
                atts = GridParamInfoLookup.getInstance().getParameterInfo(
                        mappedModel, "ws");
            }
            float minV = 0;
            float maxV = atts.getValid_range()[1];
            int precision = calcPrecision(minV, maxV);
            gpi = new GridParmInfo(id, this.outputGloc, GridType.VECTOR,
                    atts.getUnits(), "wind", minV, maxV, precision, false, tc,
                    false);
            sr.setPayload(gpi);
            return sr;

        }

        ParameterInfo atts = GridParamInfoLookup.getInstance()
                .getParameterInfo(mappedModel, id.getParmName());

        if (atts == null) {
            if (gpi == null) {
                TimeConstraints tc = new TimeConstraints(
                        TimeUtil.SECONDS_PER_HOUR, TimeUtil.SECONDS_PER_HOUR, 0);
                gpi = new GridParmInfo(id, this.outputGloc, GridType.SCALAR,
                        "", "", ParameterInfo.MIN_VALUE,
                        ParameterInfo.MAX_VALUE, 0, false, tc, false);
            }

        } else {
            boolean accParm = false;
            List<String> accumParms = config.accumulativeD2DElements(dbId
                    .getModelName());
            if (accumParms != null) {
                if (accumParms.contains(atts.getShort_name())) {
                    accParm = true;
                }
            }

            boolean rateParm = false;
            // List<TimeRange> times = this.getGridInventory(id).getPayload();
            List<TimeRange> times = GridParamInfoLookup
                    .getInstance()
                    .getParameterTimes(mappedModel, id.getDbId().getModelDate());
            TimeConstraints tc = getTimeConstraints(times);
            if (accParm) {
                tc = new TimeConstraints(tc.getRepeatInterval(),
                        tc.getRepeatInterval(), tc.getStartTime());
                rateParm = true;
            }

            float minV = -30;
            float maxV = 10000;

            if (atts.getValid_range() != null) {
                minV = atts.getValid_range()[0];
                maxV = atts.getValid_range()[1];
            } else {
                // This is the CDF convention. But we can't use
                // it or the GFE will attempt to create billions and
                // billions of contours.
                // min = MINFLOAT;
                // max = MAXFLOAT;
                minV = 0;
                maxV = 10000;
                if (!GridPathProvider.STATIC_PARAMETERS.contains(id
                        .getParmName())) {
                    statusHandler.handle(Priority.VERBOSE,
                            "[valid_range] or [valid_min] or [valid_max] "
                                    + "not found for " + id.toString());
                }
            }

            int precision = calcPrecision(minV, maxV);
            gpi = new GridParmInfo(id, this.outputGloc, GridType.SCALAR,
                    atts.getUnits(), atts.getLong_name(), minV, maxV,
                    precision, false, tc, rateParm);
        }

        sr.setPayload(gpi);
        return sr;
    }

    @Override
    public ServerResponse<Map<TimeRange, List<GridDataHistory>>> getGridHistory(
            ParmID id, List<TimeRange> trs) {

        Map<TimeRange, List<GridDataHistory>> history = new HashMap<TimeRange, List<GridDataHistory>>();
        ServerResponse<Map<TimeRange, List<GridDataHistory>>> sr = new ServerResponse<Map<TimeRange, List<GridDataHistory>>>();

        List<TimeRange> inventory = getGridInventory(id).getPayload();

        for (TimeRange time : trs) {
            if (inventory.contains(time)) {
                List<GridDataHistory> hist = new ArrayList<GridDataHistory>();
                hist.add(new GridDataHistory(
                        GridDataHistory.OriginType.INITIALIZED, id, time, null,
                        (WsId) null));
                history.put(time, hist);
            } else {
                sr.addMessage("Time Range is not in inventory: " + time);
                history.clear();
                return sr;
            }
        }
        sr.setPayload(history);
        return sr;
    }

    @Override
    public ServerResponse<List<ParmID>> getParmList() {
        ServerResponse<List<ParmID>> sr = new ServerResponse<List<ParmID>>();

        List<ParmID> parmIds = new ArrayList<ParmID>(this.gfeParms.keySet());
        sr.setPayload(parmIds);
        return sr;
    }

    @Override
    public String getProjectionId() {
        return this.outputGloc.getProjection().getProjectionID();
    }

    @Override
    public ServerResponse<List<IGridSlice>> getGridData(ParmID id,
            List<TimeRange> timeRanges) {

        List<IGridSlice> data = new ArrayList<IGridSlice>(timeRanges.size());
        ServerResponse<List<IGridSlice>> sr = new ServerResponse<List<IGridSlice>>();
        for (TimeRange tr : timeRanges) {
            GridParmInfo gpi = getGridParmInfo(id).getPayload();
            try {
                data.add(getGridSlice(id, gpi, tr, false));
            } catch (GfeException e) {
                sr.addMessage("Error getting grid slice for ParmID: " + id
                        + " TimeRange: " + tr);
                statusHandler.handle(Priority.PROBLEM,
                        "Error getting grid slice for ParmID: " + id
                                + " TimeRange: " + tr, e);
            }
        }
        sr.setPayload(data);
        return sr;
    }

    public ServerResponse<List<IGridSlice>> getGridData(ParmID id,
            List<TimeRange> timeRanges, boolean convertUnit) {

        List<IGridSlice> data = new ArrayList<IGridSlice>(timeRanges.size());
        ServerResponse<List<IGridSlice>> sr = new ServerResponse<List<IGridSlice>>();
        for (TimeRange tr : timeRanges) {
            GridParmInfo gpi = getGridParmInfo(id).getPayload();
            try {
                data.add(getGridSlice(id, gpi, tr, convertUnit));
            } catch (GfeException e) {
                sr.addMessage("Error getting grid slice for ParmID: " + id
                        + " TimeRange: " + tr);
                statusHandler.handle(Priority.PROBLEM,
                        "Error getting grid slice for ParmID: " + id
                                + " TimeRange: " + tr, e);
            }
        }

        sr.setPayload(data);
        return sr;
    }

    /**
     * Gets a grid slice based on Parm information provided
     * 
     * @param parmId
     *            The parmID for the grid slice
     * @param gpi
     *            The associated grid parm info
     * @param time
     *            The time range of the data
     * @return The grid slice containing the data
     * @throws GfeException
     *             If the grid slice cannot be constructed
     */
    private IGridSlice getGridSlice(ParmID parmId, GridParmInfo gpi,
            TimeRange time, boolean convertUnit) throws GfeException {
        IGridSlice gs = null;
        GridDataHistory[] gdh = { new GridDataHistory(
                GridDataHistory.OriginType.INITIALIZED, parmId, time, null,
                (WsId) null) };

        switch (gpi.getGridType()) {
        case SCALAR:
            Grid2DFloat data = null;
            if (this.remap == null) {
                // GFE domain does not overlap D2D grid, return default grid
                data = new Grid2DFloat(gpi.getGridLoc().getNx(), gpi
                        .getGridLoc().getNy(), gpi.getMinValue());
            } else {
                data = getGrid(parmId, time, gpi, convertUnit);
            }
            gs = new ScalarGridSlice(time, gpi, gdh, data);
            break;
        case VECTOR:
            Grid2DFloat mag = new Grid2DFloat(gpi.getGridLoc().getNx(), gpi
                    .getGridLoc().getNy());
            Grid2DFloat dir = new Grid2DFloat(gpi.getGridLoc().getNx(), gpi
                    .getGridLoc().getNy());

            if (this.remap == null) {
                // GFE domain does not overlap D2D grid, return default grid
                mag.setAllValues(gpi.getMinValue());
                dir.setAllValues(0.0f);
            } else {
                getWindGrid(parmId, time, gpi, mag, dir);
            }
            gs = new VectorGridSlice(time, gpi, gdh, mag, dir);
            break;
        default:
            statusHandler.handle(Priority.PROBLEM,
                    "Unsupported parm type for: " + gpi);
            break;

        }

        return gs;
    }

    /**
     * Returns a Grid2DFloat for the specified parm information
     * 
     * @param parmId
     *            The parmID of the data
     * @param timeRange
     *            The time range of the data
     * @param gpi
     *            The grid parm information associated with the data
     * @return The raw data
     * @throws GfeException
     *             If the grid data cannot be retrieved
     */
    private Grid2DFloat getGrid(ParmID parmId, TimeRange timeRange,
            GridParmInfo gpi, boolean convertUnit) throws GfeException {

        Grid2DFloat bdata = null;
        GridRecord d2dRecord = null;

        long t0 = System.currentTimeMillis();
        GFED2DDao dao = null;
        try {
            dao = new GFED2DDao();
        } catch (PluginException e1) {
            throw new GfeException("Unable to get GFE dao!", e1);
        }

        try {
            // Gets the metadata from the grib metadata database
            D2DParm parm = this.gfeParms.get(parmId);
            Integer fcstHr = null;
            if (!GridPathProvider.STATIC_PARAMETERS.contains(parmId
                    .getParmName())) {
                fcstHr = parm.getTimeRangeToFcstHr().get(timeRange);
                if (fcstHr == null) {
                    throw new GfeException("Invalid time range " + timeRange
                            + " for " + parmId);
                }
            }
            d2dRecord = dao.getGrid(d2dModelName, refTime,
                    parm.getComponents()[0], parm.getLevel(), fcstHr, gpi);
        } catch (DataAccessLayerException e) {
            throw new GfeException(
                    "Error retrieving D2D Grid record from database", e);
        }
        long t1 = System.currentTimeMillis();

        if (d2dRecord == null) {
            throw new GfeException("No data available for " + parmId
                    + " for time range " + timeRange);
        }

        // Gets the raw data from the D2D grib HDF5 file
        bdata = getRawGridData(d2dRecord);
        long t2 = System.currentTimeMillis();

        float fillV = Float.MAX_VALUE;
        ParameterInfo atts = GridParamInfoLookup.getInstance()
                .getParameterInfo(
                        config.d2dModelNameMapping(parmId.getDbId()
                                .getModelName()), parmId.getParmName());
        if (atts != null) {
            fillV = atts.getFillValue();
        }

        // Resample the data to fit desired region
        Grid2DFloat retVal;
        try {
            RemapGrid remap = getOrCreateRemap(d2dRecord.getLocation());
            retVal = remap.remap(bdata, fillV, gpi.getMaxValue(),
                    gpi.getMinValue(), gpi.getMinValue());
            if (convertUnit && (d2dRecord != null)) {
                convertUnits(d2dRecord, retVal, gpi.getUnitObject());
            }
        } catch (Exception e) {
            throw new GfeException("Unable to get Grid", e);
        }
        long t3 = System.currentTimeMillis();

        perfLog.logDuration("D2DGridDatabase.getGrid metaData: ", (t1 - t0));
        perfLog.logDuration("D2DGridDatabase.getGrid hdf5: ", (t2 - t1));
        perfLog.logDuration("D2DGridDatabase.getGrid remap: ", (t3 - t2));
        perfLog.logDuration("D2DGridDatabase.getGrid total: ", (t3 - t0));

        return retVal;

    }

    /**
     * Converts the data from the native unit found in the grib file to the
     * desired unit found in the grid parm info
     * 
     * @param d2dRecord
     *            The grib metadata containing the original unit information
     * @param data
     *            The float data to convert
     * @param gpi
     *            The grid parm info containing the target unit information
     * @throws GfeException
     *             If the source and target units are incompatible
     */
    private void convertUnits(GridRecord d2dRecord, Grid2DFloat data,
            Unit<?> targetUnit) throws GfeException {

        Unit<?> sourceUnit = d2dRecord.getParameter().getUnit();
        if (sourceUnit.equals(targetUnit)) {
            return;
        }

        if (!sourceUnit.isCompatible(targetUnit)) {
            return;
        }

        if (!sourceUnit.equals(targetUnit)) {
            UnitConverter converter = sourceUnit.getConverterTo(targetUnit);

            FloatBuffer dataArray = data.getBuffer();
            for (int i = 0; i < dataArray.capacity(); i++) {
                dataArray.put((float) converter.convert(dataArray.get(i)));
            }
        }
    }

    /**
     * Constructs a magnitude and direction grids for wind data
     * 
     * @param parmId
     *            The parmID of the data
     * @param timeRange
     *            The time range of the data
     * @param gpi
     *            The grid parm info for the data
     * @param mag
     *            The resulting wind magnitude grid
     * @param dir
     *            The resulting wind direction grid
     * @throws GfeException
     *             The the wind grids cannot be constructed
     */
    private void getWindGrid(ParmID parmId, TimeRange timeRange,
            GridParmInfo gpi, Grid2DFloat mag, Grid2DFloat dir)
            throws GfeException {

        GFED2DDao dao = null;
        try {
            dao = new GFED2DDao();
        } catch (PluginException e1) {
            throw new GfeException("Unable to get GFE dao!!", e1);
        }

        D2DParm windParm = this.gfeParms.get(parmId);
        Integer fcstHr = windParm.getTimeRangeToFcstHr().get(timeRange);
        if (fcstHr == null) {
            throw new GfeException("Invalid time range " + timeRange + " for "
                    + parmId);
        }

        // TODO clean up the hard coded d2d parm names
        if (windParm.getComponents()[0].equals("uW")) {
            try {
                GridRecord uRecord = null;
                GridRecord vRecord = null;

                // Get the metadata from the grib metadata database

                uRecord = dao.getGrid(d2dModelName, refTime, "uW",
                        windParm.getLevel(), fcstHr, gpi);
                vRecord = dao.getGrid(d2dModelName, refTime, "vW",
                        windParm.getLevel(), fcstHr, gpi);

                // Gets the raw grid data from the D2D grib HDF5 files
                Grid2DFloat uData = getRawGridData(uRecord);
                Grid2DFloat vData = getRawGridData(vRecord);

                // Resample the data to fit the desired region
                float fillV = Float.MAX_VALUE;
                ParameterInfo pa = modelInfo.getParameterInfo("uw");
                if (pa != null) {
                    fillV = pa.getFillValue();
                }

                try {
                    RemapGrid remap = getOrCreateRemap(uRecord.getLocation());
                    remap.remapUV(uData, vData, fillV, gpi.getMaxValue(),
                            gpi.getMinValue(), gpi.getMinValue(), true, true,
                            mag, dir);
                } catch (Exception e) {
                    throw new GfeException("Unable to remap UV wind grids", e);
                }
                return;
            } catch (DataAccessLayerException e) {
                throw new GfeException(
                        "Unable to retrieve wind grids from D2D database", e);
            }

        } else {
            try {
                GridRecord sRecord = null;
                GridRecord dRecord = null;

                // Get the metadata from the grib metadata database
                sRecord = dao.getGrid(d2dModelName, refTime, "WS",
                        windParm.getLevel(), fcstHr, gpi);
                dRecord = dao.getGrid(d2dModelName, refTime, "WD",
                        windParm.getLevel(), fcstHr, gpi);

                // Gets the raw grid data from the D2D grib HDF5 files
                Grid2DFloat sData = getRawGridData(sRecord);
                Grid2DFloat dData = getRawGridData(dRecord);

                // Resample the data to fit the desired region
                float fillV = Float.MAX_VALUE;
                ParameterInfo pa = modelInfo.getParameterInfo("ws");
                if (pa != null) {
                    fillV = pa.getFillValue();
                }

                try {
                    RemapGrid remap = getOrCreateRemap(sRecord.getLocation());
                    remap.remap(sData, dData, fillV, gpi.getMaxValue(),
                            gpi.getMinValue(), gpi.getMinValue(), mag, dir);
                } catch (Exception e) {
                    throw new GfeException("Unable to remap wind grids", e);
                }
                return;
            } catch (DataAccessLayerException e) {
                throw new GfeException(
                        "Unable to retrieve wind grids from D2D database", e);
            }
        }
    }

    /**
     * Gets the raw data from the D2D HDF5 repository
     * 
     * @param d2dRecord
     *            The grid metadata
     * @return The raw data
     * @throws GfeException
     */
    private Grid2DFloat getRawGridData(GridRecord d2dRecord)
            throws GfeException {
        try {
            GFED2DDao dao = new GFED2DDao();
            // TODO should we add subgrid support to GridDao or PluginDao

            // reimplementing this call here with subgrid support
            // dao.getHDF5Data(d2dRecord, -1);

            IDataStore dataStore = dao.getDataStore(d2dRecord);

            GridLocation gloc = getOrCreateRemap(d2dRecord.getLocation())
                    .getSourceGloc();

            String abbrev = d2dRecord.getParameter().getAbbreviation();
            String group, dataset;
            if (GridPathProvider.STATIC_PARAMETERS.contains(abbrev)) {
                group = "/" + d2dRecord.getLocation().getId();
                dataset = abbrev;
            } else {
                group = d2dRecord.getDataURI();
                dataset = DataStoreFactory.DEF_DATASET_NAME;
            }

            IDataRecord record = dataStore.retrieve(group, dataset, Request
                    .buildSlab(
                            new int[] { (int) Math.floor(gloc.getOrigin().x),
                                    (int) Math.floor(gloc.getOrigin().y), },
                            new int[] {
                                    (int) Math.ceil(gloc.getOrigin().x
                                            + gloc.getExtent().x),
                                    (int) Math.ceil(gloc.getOrigin().y
                                            + gloc.getExtent().y), }));

            FloatDataRecord hdf5Record = (FloatDataRecord) record;
            return new Grid2DFloat((int) hdf5Record.getSizes()[0],
                    (int) hdf5Record.getSizes()[1], hdf5Record.getFloatData());

        } catch (Exception e) {
            throw new GfeException("Error retrieving hdf5 record. "
                    + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Gets time constraints based on an ordered list of times
     * 
     * @param times
     *            The times
     * @return The time constraints
     */
    private TimeConstraints getTimeConstraints(List<TimeRange> times) {

        if (times.size() <= 1) {
            return new TimeConstraints(TimeUtil.SECONDS_PER_HOUR,
                    TimeUtil.SECONDS_PER_HOUR, 0);
        }

        long repeat = (times.get(1).getStart().getTime() - times.get(0)
                .getStart().getTime())
                / TimeUtil.MILLIS_PER_SECOND;
        long start = (times.get(0).getStart().getTime() / TimeUtil.MILLIS_PER_SECOND)
                % TimeUtil.SECONDS_PER_DAY;

        for (int i = 1; i < times.size() - 1; i++) {
            if (((times.get(i + 1).getStart().getTime() - times.get(i)
                    .getStart().getTime()) / TimeUtil.MILLIS_PER_SECOND) != repeat) {
                return new TimeConstraints(TimeUtil.SECONDS_PER_HOUR,
                        TimeUtil.SECONDS_PER_HOUR, 0);
            }
        }
        return new TimeConstraints(TimeUtil.SECONDS_PER_HOUR, (int) repeat,
                (int) start);
    }

    private int calcPrecision(float minV, float maxV) {
        if (maxV - minV > 250.0) {
            return 0;
        } else if (maxV - minV > 25.0) {
            return 1;
        } else if (maxV - minV > 2.5) {
            return 2;
        } else if (maxV - minV > 0.25) {
            return 3;
        } else if (maxV - minV > 0.025) {
            return 4;
        } else {
            return 5;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.raytheon.edex.plugin.gfe.server.database.VGridDatabase#getValidTimes
     * ()
     */
    @Override
    public SortedSet<Date> getValidTimes() throws GfeException,
            DataAccessLayerException {
        GFED2DDao dao = null;
        try {
            dao = new GFED2DDao();
        } catch (PluginException e) {
            throw new GfeException("Unable to get GFE dao!!", e);
        }

        List<Integer> fcstTimes = dao.getForecastTimes(d2dModelName, refTime);
        SortedSet<Date> validTimes = new TreeSet<Date>();
        for (Integer fcstTime : fcstTimes) {
            validTimes.add(new Date(refTime.getTime() + fcstTime
                    * TimeUtil.MILLIS_PER_SECOND));
        }
        return validTimes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.raytheon.edex.plugin.gfe.server.database.GridDatabase#deleteDb()
     */
    @Override
    public void deleteDb() {
        // no-op
    }

    public D2DParm getD2DParm(String d2dParmName, Level d2dLevel) {
        String gfeParmName = getGfeParmName(d2dParmName);

        String gfeLevel = getGFELevel(d2dLevel);
        if (gfeLevel == null) {
            statusHandler.warn("No GFE level found for D2D model: "
                    + d2dModelName + " D2D parm: " + d2dParmName
                    + " D2D level:" + d2dLevel + " GFE model: "
                    + dbId.getModelName() + " GFE parm: " + gfeParmName
                    + ". Check gfeLevelMapping and parameterInfo files.");
            return null;
        }

        D2DParm parm = d2dParms.get(compositeName(gfeParmName, gfeLevel));
        if (parm == null) {
            // try to find one with duration (XXXnnhr)
            Matcher matcher = parmHrPattern.matcher(d2dParmName);
            if (matcher.find()) {
                String abbrev = matcher.group(1);
                gfeParmName = getGfeParmName(abbrev);
                parm = d2dParms.get(compositeName(gfeParmName, gfeLevel));
            }
        }

        if (parm == null) {
            statusHandler.warn("No gridParameterInfo found for "
                    + compositeName(gfeParmName, gfeLevel) + ":"
                    + dbId.getModelId() + ". Check parameterInfo file.");
        }

        return parm;
    }

    public String getGfeParmName(String d2dParmName) {
        String gfeParmName = null;
        try {
            gfeParmName = ParameterMapper.getInstance().lookupAlias(
                    d2dParmName, "gfeParamName");
        } catch (MultipleMappingException e) {
            statusHandler.handle(Priority.WARN, e.getLocalizedMessage(), e);
            gfeParmName = e.getArbitraryMapping();
        }
        return gfeParmName;
    }

    public String getD2DParmName(String gfeParmName) {
        String d2dParmName = null;
        try {
            d2dParmName = ParameterMapper.getInstance().lookupBaseName(
                    gfeParmName, "gfeParamName");
        } catch (MultipleMappingException e) {
            statusHandler.handle(Priority.WARN, e.getLocalizedMessage(), e);
            d2dParmName = e.getArbitraryMapping();
        }
        return d2dParmName;
    }

    public TimeRange getTimeRange(ParmID parmID, Integer fcstHour) {
        D2DParm parm = this.gfeParms.get(parmID);
        if (parm == null) {
            statusHandler.warn("No D2D parameter found for " + parmID);
            return null;
        }
        TimeRange tr = parm.getFcstHrToTimeRange().get(fcstHour);
        return tr;
    }

    private String compositeName(String parmName, String level) {
        return parmName + "_" + level;
    }

    private static Level getD2DLevel(String gfeLevel) {
        List<Level> levels;
        try {
            levels = LevelMappingFactory.getInstance(GFE_LEVEL_MAPPING_FILE)
                    .getLevelMappingForKey(gfeLevel).getLevels();
        } catch (CommunicationException e) {
            levels = Collections.emptyList();
        }

        Level level = null;
        if (levels.isEmpty()) {
            statusHandler.warn("No D2D level found for: " + gfeLevel);
        } else {
            level = levels.get(0);
        }
        return level;
    }

    private static String getGFELevel(Level d2dLevel) {
        LevelMapping levelMapping;
        try {
            levelMapping = LevelMappingFactory.getInstance(
                    GFE_LEVEL_MAPPING_FILE).getLevelMappingForLevel(d2dLevel);
        } catch (CommunicationException e) {
            levelMapping = null;
        }

        String gfeLevel = null;
        if (levelMapping != null) {
            gfeLevel = levelMapping.getKey();
        }
        return gfeLevel;
    }
}
