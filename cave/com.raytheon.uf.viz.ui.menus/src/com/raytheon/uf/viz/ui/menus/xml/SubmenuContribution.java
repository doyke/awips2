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
package com.raytheon.uf.viz.ui.menus.xml;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;

import com.raytheon.uf.common.menus.xml.CommonAbstractMenuContribution;
import com.raytheon.uf.common.menus.xml.CommonSubmenuContribution;
import com.raytheon.uf.common.menus.xml.VariableSubstitution;
import com.raytheon.uf.viz.core.exception.VizException;
import com.raytheon.uf.viz.ui.menus.widgets.SubmenuContributionItem;
import com.raytheon.uf.viz.ui.menus.widgets.tearoff.TearOffMenuListener;

/**
 * Describes a submenu contribution
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Mar 26, 2009            chammack     Initial creation
 * 
 * </pre>
 * 
 * @author chammack
 * @version 1.0
 */
public class SubmenuContribution extends
        AbstractMenuContributionItem<CommonSubmenuContribution> implements
        IVizMenuManager {

    private SubmenuContributionItem submenuCont = null;

    private IMenuListener mListener = null;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.raytheon.viz.ui.menus.xml.IContribItemProvider#getContributionItems
     * (com.raytheon.viz.ui.menus.xml.VariableSubstitution[])
     */
    @Override
    public IContributionItem[] getContributionItems(
            CommonAbstractMenuContribution items, VariableSubstitution[] subs,
            Set<String> removals) throws VizException {
        CommonSubmenuContribution item = (CommonSubmenuContribution) items;

        if (removals.contains(item.id))
            return new IContributionItem[0];

        submenuCont = new SubmenuContributionItem(subs, item.menuText,
                item.contributions, new HashSet<String>(), mListener);
        // adding tear off listener, seems out of place, but must be done
        if (mListener == null
                && com.raytheon.uf.viz.core.Activator.getDefault()
                        .getPreferenceStore().getBoolean("tearoffmenus")) {
            mListener = new TearOffMenuListener(submenuCont);
            submenuCont.addMenuListener(mListener);
        }
        return new IContributionItem[] { submenuCont };
    }

    @Override
    public void addMenuListener(IMenuListener listener) {
        mListener = listener;
    }

    @Override
    public void removeMenuListener(IMenuListener listener) {
        submenuCont.removeMenuListener(listener);
    }
}
