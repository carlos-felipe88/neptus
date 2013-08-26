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

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import pt.up.fe.dceg.neptus.i18n.I18n;
import pt.up.fe.dceg.neptus.plugins.vtk.Vtk;
import pt.up.fe.dceg.neptus.util.ImageUtils;

/**
 * @author hfq
 * 
 */
public class MultibeamMenuBar {
    private Vtk vtkMultibeamInit;

    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, viewMenu, toolsMenu, helpMenu;

    // fileMenu
    private AbstractAction saveFile, saveFileAs;
    // EditMenu
    private AbstractAction configDepthColorBounds, zValueExaggeration, downsampleLeafSize;
    // ViewMenu
    private AbstractAction resetViewportCamera, incrementPointSize, decrementPointSize, colorGradX, colorGradY,
            colorGradZ, viewPointCloud, viewMesh, pointBasedRep, wireframeRep, surfaceRep, displayLookUpTable,
            displayScaleGrid, displayInfoPointcloud;
    // ToolsMenu
    private AbstractAction exaggerateZ, performMeshing, performSmoothing;
    // HelpMenu
    private AbstractAction help;

    public MultibeamMenuBar(Vtk vtkMultibeamInit) {
        this.vtkMultibeamInit = vtkMultibeamInit;
    }

    public JMenuBar createMultibeamMenuBar() {
        setMenuBar(new JMenuBar());

        fileMenu = new JMenu(I18n.text("File"));
        addMenuItemsToFileMenu();
        editMenu = new JMenu(I18n.text("Edit"));
        addMenuItemsToEditMenu();
        viewMenu = new JMenu(I18n.text("View"));
        addMenuItemsToViewMenu();
        toolsMenu = new JMenu(I18n.text("Tools"));
        addMenuItemsToToolsMenu();
        helpMenu = new JMenu(I18n.text("Help"));
        addMenuItemsToHelpMenu();

        getMenuBar().setBackground(Color.GRAY);
        getMenuBar().add(fileMenu);
        getMenuBar().add(editMenu);
        getMenuBar().add(viewMenu);
        getMenuBar().add(toolsMenu);
        getMenuBar().add(helpMenu);

        return getMenuBar();
    }

    private void addMenuItemsToFileMenu() {
        saveFile = new AbstractAction(I18n.text("Save File")) {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        saveFileAs = new AbstractAction(I18n.text("Save File as...")) {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        fileMenu.add(saveFile);
        fileMenu.add(saveFileAs);
    }

    private void addMenuItemsToEditMenu() {

    }

    private void addMenuItemsToViewMenu() {
        resetViewportCamera = new MultibeamVisAction(I18n.text("Reset Viewport"), ImageUtils.createImageIcon("/images/menus/camera.png")) {
            private static final long serialVersionUID = 1982593403207394131L;

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    vtkMultibeamInit.canvas.lock();
                    vtkMultibeamInit.canvas.GetRenderer().GetActiveCamera().SetViewUp(0.0, 0.0, -1.0);
                    vtkMultibeamInit.canvas.GetRenderer().ResetCamera();
                    vtkMultibeamInit.canvas.Render();
                    vtkMultibeamInit.canvas.unlock();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
                
        };

        viewMenu.add(resetViewportCamera);
    }

    private void addMenuItemsToToolsMenu() {

    }

    private void addMenuItemsToHelpMenu() {
        help = new HelpMultibeamVisAction(vtkMultibeamInit);
        helpMenu.add(help);
    }

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
