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

package gov.noaa.nws.ncep.edex.uengine.tasks.profile;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.raytheon.uf.common.dataplugin.PluginDataObject;
import com.raytheon.uf.common.dataplugin.PluginException;
import com.raytheon.uf.common.dataplugin.persist.IPersistable;
import com.raytheon.uf.common.datastorage.IDataStore;
import com.raytheon.uf.common.datastorage.Request;
import com.raytheon.uf.common.datastorage.records.FloatDataRecord;
import com.raytheon.uf.common.datastorage.records.IDataRecord;
import com.raytheon.uf.edex.database.plugin.PluginDao;
import com.raytheon.uf.edex.database.plugin.PluginFactory;

/**
 * PointIn task derived from original uEngine PointIn task. Reads a file in from
 * the data store.
 * 
 * <pre>
 * SOFTWARE HISTORY
 * Date             PR#             Engineer            Description
 * -----------      ----------      ------------        --------------------------
 * Mar 29, 2007                     njensen             Initial Creation
 * 03/28/2012                       Chin Chen   Add new APIs to support query multiple Points at one shoot and using
 * 										dataStore.retrieveGroups()
 * 
 * </PRE>
 * 
 */
public class PointIn {// extends ScriptTask {

    private final PluginDataObject dataRecord;

    private PluginDao dao;

    private final int indX;

    private final int indY;

    /**
     * Constructor
     * 
     * @param aPlugin
     *            the plugin
     * @param aDataRecord
     *            the data record to read in
     */
    public PointIn(String aPlugin, PluginDataObject aDataRecord, int xInd,
            int yInd) {
        dataRecord = aDataRecord;
        indX = xInd;
        indY = yInd;
        try {
            dao = PluginFactory.getInstance().getPluginDao(aPlugin);
            // dataRecord.getPluginName());
        } catch (PluginException e) {
            System.out.println("Unable to get " + dataRecord.getPluginName()
                    + " dao");
        }
    }

    public PointIn(String aPlugin, PluginDataObject aDataRecord) {
        dataRecord = aDataRecord;
        indX = 0;
        indY = 0;
        try {
            dao = PluginFactory.getInstance().getPluginDao(aPlugin);
            // dataRecord.getPluginName());
        } catch (PluginException e) {
            System.out.println("Unable to get " + dataRecord.getPluginName()
                    + " dao");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.raytheon.edex.uengine.js.tasks.ScriptTask#execute()
     * 
     * @Override public Object execute() throws PluginException { IDataRecord
     * record = getHDF5DataPointNew(dataRecord, indX, indY ); FloatDataRecord
     * fdr = (FloatDataRecord)record; return fdr.getFloatData()[0]; }
     */

    public float getPointData() throws PluginException {
        return ((FloatDataRecord) getHDF5DataPoint(dataRecord, indX, indY))
                .getFloatData()[0];
    }

    // public Object[] retrieveGroup() throws PluginException {
    // return dao.getHDF5Data(dataRecord, -1);
    // }

    /*
     * public IDataRecord getHDF5DataPoint(PluginDataObject object, int xInd,
     * int yInd) throws PluginException {
     * 
     * Request pointRequest = Request.buildPointRequest(new Point(xInd, yInd) );
     * IDataRecord[] record = null; record = new IDataRecord[1];
     * 
     * if (object instanceof IPersistable) { // connect to the data store and
     * retrieve the data //chin remove this line NcgribDao dao = new
     * NcgribDao(); IDataStore dataStore = dao.getDataStore((IPersistable)
     * object); try { record[0] = dataStore.retrieve(object.getDataURI(),
     * "Data", pointRequest);
     * 
     * } catch (Exception e) { throw new
     * PluginException("Error getting HDF5 data", e); } } return record[0]; }
     */
    public IDataRecord getHDF5DataPoint(PluginDataObject object, int xInd,
            int yInd) throws PluginException {

        Request pointRequest = Request.buildPointRequest(new Point(xInd, yInd));
        IDataRecord[] dr = null;
        // record = new IDataRecord[1];

        if (object instanceof IPersistable) {
            // chin remove this line NcgribDao dao = new NcgribDao();
            IDataStore dataStore = dao.getDataStore((IPersistable) object);
            try {
                String[] groups = new String[1];
                groups[0] = object.getDataURI();
                dr = dataStore.retrieveGroups(groups, pointRequest);
                for (IDataRecord element : dr) {
                    float[] data = (float[]) element.getDataObject();

                }

            } catch (Exception e) {
                throw new PluginException("Error getting HDF5 data", e);
            }
        }
        return dr[0];
    }

    /*
     * //from efficientRetirevePoint() public float[]
     * getHDF5GroupDataPoint(Object[] objects) throws PluginException { float[]
     * rval = new float[objects.length]; Request pointRequest =
     * Request.buildPointRequest(new Point(indX, indY) ); IDataRecord[] dr =
     * null; //record = new IDataRecord[1];
     * 
     * if (objects[0] instanceof IPersistable) { IDataStore dataStore =
     * dao.getDataStore((IPersistable) objects[0]); try { String[] groups = new
     * String[objects.length]; for(int i=0; i<objects.length; i++){ groups[i] =
     * ((PluginDataObject)objects[i]).getDataURI(); } dr=
     * dataStore.retrieveGroups(groups, pointRequest); for (int k = 0, index=0;
     * k < dr.length; k++, index++) { float[] data = (float[])
     * dr[k].getDataObject(); rval[index] = data[0]; }
     * 
     * } catch (Exception e) { throw new
     * PluginException("Error getting HDF5 data", e); } } return rval; }
     */
    /**
     * 
     * This API is to query grid data for multiple Points and multiple
     * parameters. Parameters can be same parameter but at different pressure
     * level. They will be treated as different parameters.
     * 
     * @param objects
     *            :parameters to be query
     * @param points
     *            : query locations, they are index in a 2 dimensional grid (can
     *            not use lat/lon directly). Use PointUtil.determineIndex to
     *            convert from lat/lon to Point.
     * 
     */
    public List<float[]> getHDF5GroupDataPoints(Object[] objects,
            List<Point> points) throws PluginException {
        int pointsRequested = points.size();
        List<float[]> rval = new ArrayList<float[]>(pointsRequested);
        Request pointRequest = (Request.buildPointRequest(points
                .toArray(new Point[pointsRequested])));

        List<IPersistable> objList = new ArrayList<IPersistable>(objects.length);
        for (Object obj : objects) {
            // shouldn't need to check every object, better to be safe
            if (obj instanceof IPersistable) {
                objList.add((IPersistable) obj);
            }
        }

        // arbitrary list of IPersistable could be in any number of data stores
        Map<IDataStore, List<IPersistable>> dataStoreMap = dao
                .getDataStoreMap(objList);
        int rvalIndex = 0;
        int totalRec = 0;

        try {
            // list for data records retrieved
            List<IDataRecord> dataRecords = new ArrayList<IDataRecord>(
                    objects.length);

            for (Map.Entry<IDataStore, List<IPersistable>> entry : dataStoreMap
                    .entrySet()) {
                /* connect to the data store and retrieve the data */
                IDataStore dataStore = entry.getKey();

                List<IPersistable> persistList = entry.getValue();
                String[] groups = new String[persistList.size()];
                int i = 0;
                for (IPersistable persist : persistList) {
                    groups[i++] = ((PluginDataObject) persist).getDataURI();
                }

                // retrieve data from this data store
                IDataRecord[] records = dataStore.retrieveGroups(groups,
                        pointRequest);
                for (IDataRecord rec : records) {
                    dataRecords.add(rec);
                }
            }

            if (dataRecords.size() > 0) {
                for (int i = 0; i < pointsRequested; i++) {
                    rval.add(new float[dataRecords.size()]);
                }

                int recordIndex = 0;
                for (IDataRecord record : dataRecords) {
                    float[] data = (float[]) record.getDataObject();
                    // note; data.length should be the same as points.size()
                    // if(k==0)
                    // System.out.println("data[] szie="+data.length+
                    // " parameter group size="+dr.length);
                    totalRec += data.length;
                    for (int pointIndex = 0; pointIndex < data.length; pointIndex++) {
                        float[] pData = rval.get(pointIndex);
                        pData[recordIndex] = data[pointIndex];
                    }
                }
                System.out.println("total points = " + points.size()
                        + " totalRec = " + totalRec);
            }
        } catch (Exception e) {
            throw new PluginException("Error getting HDF5 data", e);
        }
        return rval;
    }
}