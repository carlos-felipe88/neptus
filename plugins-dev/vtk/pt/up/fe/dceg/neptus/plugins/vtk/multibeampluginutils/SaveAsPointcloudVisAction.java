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
 * Aug 26, 2013
 */
package pt.up.fe.dceg.neptus.plugins.vtk.multibeampluginutils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import pt.up.fe.dceg.neptus.NeptusLog;
import pt.up.fe.dceg.neptus.i18n.I18n;
import pt.up.fe.dceg.neptus.plugins.vtk.Vtk;
import pt.up.fe.dceg.neptus.plugins.vtk.io.Writer3D;
import pt.up.fe.dceg.neptus.plugins.vtk.utils.File3DUtils;
import pt.up.fe.dceg.neptus.util.GuiUtils;
import pt.up.fe.dceg.neptus.util.ImageUtils;
import vtk.vtkPolyData;

/**
 * @author hfq
 * 
 */
public class SaveAsPointcloudVisAction extends VisualizationAction {
    private static final long serialVersionUID = 57188206410572486L;

    protected Vtk vtkMultibeamInit;
    private Component parent;
    private vtkPolyData polyData;

    /**
     * FIXME - change keyStroke to ctrl+s (same as save)
     * 
     * FIXME - Needs to check if the user wants to save the pointcloud or the mesh (if meshing was already performed)
     * 
     * @param vtkMultibeamInit
     * @param parent
     */
    public SaveAsPointcloudVisAction(Vtk vtkMultibeamInit, Component parent, vtkPolyData polyData) {
        super(I18n.text("Save Pointcloud As..."), new ImageIcon(ImageUtils.getImage("images/menus/saveas.png")), I18n
                .text("Save a to mesh File"), KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK, true));
        this.vtkMultibeamInit = vtkMultibeamInit;
        this.parent = parent;
        this.polyData = polyData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(vtkMultibeamInit.getLog().getFile("Data.lsf").getParentFile());

        // not supported 3ds  + "*.3ds,"
        FileFilter fileFilter = GuiUtils.getCustomFileFilter(I18n.text("3D Files ") + "*.vtk" + ", *.stl"
                + ", *.ply" + ", *.obj" + ", *.wrl" + " *.x3d", new String[] { "X3D", "VTK", "STL", "PLY", "OBJ", "WRL" });
        chooser.setFileFilter(fileFilter);

        int ans = chooser.showDialog(parent, I18n.text("Save As..."));
        if (ans == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                ans = JOptionPane.showConfirmDialog(parent,
                        I18n.text("Are you sure you want to overwrite existing file?"), I18n.text("Save File As.."),
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (ans != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            File dst = chooser.getSelectedFile();
            String ext = File3DUtils.getExtension(dst);
            NeptusLog.pub().info("Extension: " + ext);
            File3DUtils.FileType type = null;
            type = File3DUtils.getFileType(ext);
            NeptusLog.pub().info("Filetype: " + type.toString());
            switch (type) {
//                case XYZ:
//                    Writer3D.ex
//                    break;
                case STL:
                    NeptusLog.pub().info("Saving STL File");
                    Writer3D.exportToSTLFileFormat(chooser.getSelectedFile(),
                            polyData);
                    break;
                case OBJ:
                    NeptusLog.pub().info("Saving OBJ File");
                    Writer3D.exportToOBJFileFormat(chooser.getSelectedFile(),
                            polyData,
                            vtkMultibeamInit.canvas.GetRenderWindow());
                    break;
                case PLY:
                    NeptusLog.pub().info("Saving PLY File");
                    Writer3D.exportToPLYFileFormat(chooser.getSelectedFile(),
                            polyData);
                    break;
//                case ThreeDS:
//                    break;
                case VTK:
                    NeptusLog.pub().info("Saving VTK File");
                    Writer3D.exportToVTKFileFormat(chooser.getSelectedFile(), polyData);
                    break;
                case WRL:
                    NeptusLog.pub().info("Saving WRL File");
                    Writer3D.exportToVRMLFileFormat(chooser.getSelectedFile(), polyData, vtkMultibeamInit.canvas.GetRenderWindow());
                    break;
                case X3D:
                    NeptusLog.pub().info("Saving X3D File");
                    Writer3D.exportToX3DFileFormart(chooser.getSelectedFile(),
                            polyData,
                            vtkMultibeamInit.canvas.GetRenderWindow());
                    break;
                default:
                    NeptusLog.pub().info("Default type... no way!!!");
                    break;
            }
//            if (ext.equals(File3DUtils.xyzStr) || ext.equals(File3DUtils.wrlStr) || ext.equals(File3DUtils.stlStr)
//                    || ext.equals(File3DUtils.plyStr) || ext.equals(File3DUtils.objStr)
//                    || ext.equals(File3DUtils.treedsStr) || ext.equals(File3DUtils.vtkStr)) {
//
//            }
        }
    }
}
