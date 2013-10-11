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
 * Oct 9, 2013
 */
package pt.up.fe.dceg.neptus.plugins.vtk.multibeampluginutils;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import pt.up.fe.dceg.neptus.i18n.I18n;
import pt.up.fe.dceg.neptus.plugins.vtk.Vtk;
import pt.up.fe.dceg.neptus.util.GuiUtils;
import pt.up.fe.dceg.neptus.util.ImageUtils;

/**
 * @author hfq
 * 
 */
public class ToolBar extends JPanel {
    private static final long serialVersionUID = -7233932650068469685L;

    private static final short ICON_SIZE = 20;

    private static final ImageIcon ICON_POINTS = ImageUtils.getScaledIcon(
            ImageUtils.getImage("pt/up/fe/dceg/neptus/plugins/vtk/assets/points.png"), ICON_SIZE, ICON_SIZE);

    private static final ImageIcon ICON_WIREFRAME = ImageUtils.getScaledIcon(
            ImageUtils.getImage("pt/up/fe/dceg/neptus/plugins/vtk/assets/wire.png"), ICON_SIZE, ICON_SIZE);

    private static final ImageIcon ICON_SOLID = ImageUtils.getScaledIcon(
            ImageUtils.getImage("pt/up/fe/dceg/neptus/plugins/vtk/assets/textures.png"), ICON_SIZE, ICON_SIZE);

    private static final ImageIcon ICON_Z = ImageUtils.getScaledIcon(
            ImageUtils.getImage("pt/up/fe/dceg/neptus/plugins/vtk/assets/zexag.png"), ICON_SIZE, ICON_SIZE);
    
    
    private Vtk vtkMultibeamInit;

    private JPanel panel;
    private static JToolBar toolbar;

    private JToggleButton rawPointsToggle;          // works with pointcloud
    private JToggleButton wireframeToggle;          // works with mesh
    private JToggleButton solidToggle;              // works with mesh

    private JToggleButton zExaggerationToggle;      
    private JToggleButton countoursToggle;

    private JToggleButton meshingToggle;
    private JToggleButton smoothingMeshToggle;

    private JToggleButton downsamplePointToggle;

    public ToolBar() {

    }

    public ToolBar(Vtk vtkMultibeamInit) {
        this.vtkMultibeamInit = vtkMultibeamInit;
        //this.setOpaque(false); 
    }

    public void createToolbar() {
        
     
        setToolbar(new JToolBar(JToolBar.VERTICAL)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D graphic2d = (Graphics2D) g;
                Color color1 = getBackground();
                Color color2 = Color.GRAY;
                GradientPaint gradPaint = new GradientPaint(0, 0, color2, getWidth(), getHeight(), color1);
                graphic2d.setPaint(gradPaint);
                graphic2d.fillRect(0, 0, getWidth(), getHeight());
            } 
        });
    
        getToolbar().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder()));
        //getToolbar().setOpaque(false);

        rawPointsToggle = new JToggleButton();
        //rawPointsToggle.setOpaque(false);
        rawPointsToggle.setHorizontalAlignment(JToggleButton.CENTER);
        rawPointsToggle.setToolTipText(I18n.text("Points based representation."));
        rawPointsToggle.setIcon(ICON_POINTS);

        wireframeToggle = new JToggleButton();
        //wireframeToggle.setOpaque(false);
        wireframeToggle.setHorizontalAlignment(JToggleButton.CENTER);
        wireframeToggle.setToolTipText(I18n.text("Wireframe based representation."));
        wireframeToggle.setIcon(ICON_WIREFRAME);
        
        solidToggle = new JToggleButton();
        //solidToggle.setOpaque(false);
        solidToggle.setHorizontalAlignment(JToggleButton.CENTER);
        solidToggle.setToolTipText(I18n.text("Solid based representation."));
        solidToggle.setIcon(ICON_SOLID);
        
        zExaggerationToggle = new JToggleButton();
        //zExaggerationToggle.setOpaque(false);
        zExaggerationToggle.setHorizontalAlignment(JToggleButton.CENTER);
        zExaggerationToggle.setToolTipText(I18n.text("Exaggerate Z."));
        zExaggerationToggle.setIcon(ICON_Z);

        getToolbar().addSeparator();
        getToolbar().add(rawPointsToggle);
        getToolbar().add(wireframeToggle);
        getToolbar().add(solidToggle);
        
        getToolbar().addSeparator();
        getToolbar().add(zExaggerationToggle);
        
        
        this.add(getToolbar());       
    }

    /**
     * @return the toolbar
     */
    public JToolBar getToolbar() {
        return toolbar;
    }

    /**
     * @param toolbar the toolbar to set
     */
    private void setToolbar(JToolBar toolbar) {
        this.toolbar = toolbar;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ToolBar classToolbar = new ToolBar();
        classToolbar.createToolbar();
        classToolbar.add(toolbar); 
        GuiUtils.testFrame(classToolbar, "Test Multibeam: " + classToolbar.getClass().getSimpleName(), ICON_SIZE + 25, ICON_SIZE * 3 + 500);

        // GuiUtils.testFrame(lcp, "Test" + lcp.getClass().getSimpleName(), LedsUtils.PANEL_WIDTH,
        // LedsUtils.PANEL_HEIGHT);
    }



}
