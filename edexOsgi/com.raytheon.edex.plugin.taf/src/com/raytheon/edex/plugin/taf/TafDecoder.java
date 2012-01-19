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

package com.raytheon.edex.plugin.taf;

import com.raytheon.edex.exception.DecoderException;
import com.raytheon.edex.plugin.AbstractDecoder;
import com.raytheon.edex.plugin.taf.common.TafRecord;
import com.raytheon.edex.plugin.taf.decoder.TAFParser;
import com.raytheon.edex.plugin.taf.decoder.TAFParts;
import com.raytheon.uf.common.dataplugin.PluginDataObject;
import com.raytheon.uf.edex.wmo.message.WMOHeader;

/**
 * 
 * Decoder implementation for taf plugin
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * 
 * Date       	Ticket#		Engineer	Description
 * ------------	----------	-----------	--------------------------
 * 2/14/07		139			bphillip	Initial creation	
 * 6/21/07		180			bphillip	Updated to use new plugin pattern
 * 20080425           1001  jkorman     Extracted decoder code into TAFParser.
 * 
 * </pre>
 * 
 * @author bphillip
 * @version 1
 */

public class TafDecoder extends AbstractDecoder {
    // Name of the plugin controlling this decoder.
    public static final String PLUGIN_NAME = "TAF";

    private String traceId = "";

    /**
     * Constructor.
     * 
     * @param productType
     * @throws DecoderException
     */
    public TafDecoder() {

    }

    public static class TAFDecoderInput {
        public WMOHeader wmoHeader;

        public TAFParts tafParts;
    }

    /**
     * 
     * @param input
     * @return
     * @throws DecoderException
     */
    public PluginDataObject[] decode(TAFDecoderInput input)
            throws DecoderException {

        TafRecord record = null;

        TAFParser parser = null;
        try {
            parser = new TAFParser(input.tafParts, input.wmoHeader);

            record = parser.getDecodedRecord();
            if (record != null) {
                record.setTraceId(traceId);
                record.setPluginName("taf");
                record.constructDataURI();
            }
        } catch (DecoderException de) {
            logger.info(traceId + " -" + de.getMessage());
            record = null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(traceId, e);
            record = null;
        }

        if (record == null)
            return new PluginDataObject[0];

        return new PluginDataObject[] { record };
    }
}
