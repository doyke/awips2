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
package com.raytheon.uf.edex.core.dataplugin;

import java.util.ArrayList;
import java.util.List;

import com.raytheon.uf.common.dataplugin.IPluginRegistryChanged;
import com.raytheon.uf.common.dataplugin.PluginProperties;
import com.raytheon.uf.common.util.registry.GenericRegistry;
import com.raytheon.uf.common.util.registry.RegistryException;

/**
 * Registry of EDEX data plugins
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Aug 6, 2009            mschenke     Initial creation
 * 
 * </pre>
 * 
 * @author mschenke
 * @version 1.0
 */

public class PluginRegistry extends GenericRegistry<String, PluginProperties> {

    private static PluginRegistry instance = new PluginRegistry();

    private List<IPluginRegistryChanged> listeners = new ArrayList<IPluginRegistryChanged>();

    private PluginRegistry() {
        super();
    }

    public static PluginRegistry getInstance() {
        return instance;
    }

    @Override
    public Object register(String pluginName, PluginProperties pluginProperties)
            throws RegistryException {
        if (!registry.containsKey(pluginName)) {
            super.register(pluginName, pluginProperties);
            for (IPluginRegistryChanged iprc : listeners) {
                iprc.pluginAdded(pluginName);
            }
            return this;
        } else {
            throw new RegistryException("Duplicate pluginName " + pluginName
                    + " registered in Spring XML configuration.");
        }
    }

    public Object addListener(IPluginRegistryChanged listener) {
        listeners.add(listener);
        return this;
    }

}
