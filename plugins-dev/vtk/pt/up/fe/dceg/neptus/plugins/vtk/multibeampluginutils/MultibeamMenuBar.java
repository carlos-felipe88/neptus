/*
 * Copyright (c) 2004-2013 Universidade do Porto - Faculdade de Engenharia
 * Laboratório de Sistemas e Tecnologia Subaquática (LSTS)
 * All rights reserved.
 * Rua Dr. Roberto Frias s/n, sala I203, 4200-465 Porto, Portugal
 *
 * This file is part of Neptus, Command and Control Framework.
 *
 * Commercial Licence Usage
 * Licencees holding valid commercial Neptus licences may use this file
 * in accordance with the commercial licence agreement provided with the
 * Software or, alternatively, in accordance with the terms contained in a
 * written agreement between you and Universidade do Porto. For licensing
 * terms, conditions, and further information contact lsts@fe.up.pt.
 *
 * European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENSE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://www.lsts.pt/neptus/licence.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: hfq
 * Aug 14, 2013
 */
package pt.up.fe.dceg.neptus.plugins.vtk.multibeampluginutils;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import pt.up.fe.dceg.neptus.i18n.I18n;
import pt.up.fe.dceg.neptus.plugins.vtk.Vtk;

/**
 * @author hfq
 *
 */
public class MultibeamMenuBar {
    private Vtk vtkMultibeamInit;
    
    private JMenuBar menuBar;
    
    public MultibeamMenuBar(Vtk vtkMultibeamInit) {
        this.vtkMultibeamInit = vtkMultibeamInit;
    }
    
    public JMenuBar createMultibeamMenuBar() {
        setMenuBar(new JMenuBar());

        JMenu file = new JMenu(I18n.text("File"));
        addMenuItemsToFile(file);
        
        JMenu edit = new JMenu(I18n.text("Edit"));
        JMenu view = new JMenu(I18n.text("View"));
        JMenu help = new JMenu(I18n.text("Help"));
        JMenu tools = new JMenu(I18n.text("Tools"));       
        
        getMenuBar().add(file);
        getMenuBar().add(edit);
        getMenuBar().add(view);
        getMenuBar().add(help);
        getMenuBar().add(tools);
        
        return getMenuBar();
    }


    /**
     * @param file
     */
    private void addMenuItemsToFile(JMenu file) {
        
        
    }

    /**
     * @return the menuBar
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * @param menuBar the menuBar to set
     */
    private void setMenuBar(JMenuBar menuBar) {
        this.menuBar = menuBar;
    }

}
