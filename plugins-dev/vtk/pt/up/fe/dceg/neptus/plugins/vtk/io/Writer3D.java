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
 * Aug 27, 2013
 */
package pt.up.fe.dceg.neptus.plugins.vtk.io;

import java.io.File;

import vtk.vtkDataSetWriter;
import vtk.vtkOBJExporter;
import vtk.vtkPLYWriter;
import vtk.vtkPolyData;
import vtk.vtkRenderWindow;
import vtk.vtkSTLWriter;
import vtk.vtkVRMLExporter;
import vtk.vtkX3DExporter;

/**
 * @author hfq
 * Export vtk polydata to some of the most known 3D file types
 * 3D file types supported:
 * *.vtk - Native VTK file type, can also be loaded onto ParaView
 * *.obj
 * *.ply
 * *.stl
 * *.wrl
 * *.x3d
 * 
 * .3ds ?!?! - doesn't have an exporter on vtk lib (it can be imported)
 */
public class Writer3D {
    
    private static vtkDataSetWriter exporterToVtk;
    private static vtkOBJExporter exporterToOBJ;
    private static vtkPLYWriter exporterToPLY;
    private static vtkSTLWriter exporterToSTL;
    private static vtkVRMLExporter exporterToVRML;
    private static vtkX3DExporter exporterToX3D;
    
    public Writer3D(){
        
    }
    
    /**
     * @param filename
     * @param poly
     */
    public static void exportToVTKFileFormat(File file, vtkPolyData poly) {
        exporterToVtk = new vtkDataSetWriter();
        exporterToVtk.SetFileName(file.getAbsolutePath());
        exporterToVtk.SetInput(poly);
        exporterToVtk.Update();
        exporterToVtk.Write();
    }
    
    /**
     * not supported?!?
     * @param filename
     * @param poly
     */
    public static void exportToOBJFileFormat(File file, vtkPolyData poly, vtkRenderWindow renWin) {
        exporterToOBJ = new vtkOBJExporter();
        exporterToOBJ.SetFilePrefix(file.getParent() + "/cells");
        exporterToOBJ.SetInput(renWin);
        exporterToOBJ.Update();
        exporterToOBJ.Write();
        
        // remove /cells.obj
        // remover /cells.mtl
    }
    
    /**
     * @param filename
     * @param poly
     */
    public static void exportToPLYFileFormat(File file, vtkPolyData poly) {
        exporterToPLY = new vtkPLYWriter();
        exporterToPLY.SetFileName(file.getAbsolutePath());
        exporterToPLY.SetInput(poly);
        exporterToPLY.SetFileTypeToBinary();
        //exporterToPLY.SetLookupTable()
        exporterToPLY.Update();
        exporterToPLY.Write();
    }
    
    /**
     * @param filename
     * @param poly
     */
    public static void exportToSTLFileFormat(File file, vtkPolyData poly) {
        exporterToSTL = new vtkSTLWriter();
        exporterToSTL.SetFileName(file.getAbsolutePath());
        exporterToSTL.SetInput(poly);
        exporterToSTL.Update();
        exporterToSTL.Write();
    }
    
    /**
     * 
     * @param file
     * @param poly
     * @param renWin
     */
    public static void exportToVRMLFileFormat(File file, vtkPolyData poly, vtkRenderWindow renWin) {
        exporterToVRML = new vtkVRMLExporter();
        //exporterToVRML.SetStartWrite(id0, id1)
        exporterToVRML.SetFileName(file.getAbsolutePath());
        //exporterToVRML.SetInput(poly);
        exporterToVRML.SetInput(renWin);
        exporterToVRML.SetSpeed(5.5);
        exporterToVRML.Update();
        exporterToVRML.Write();
    }
    
    /**
     * 
     * @param file
     * @param poly
     * @param renWin
     */
    public static void exportToX3DFileFormart(File file, vtkPolyData poly, vtkRenderWindow renWin) {
        exporterToX3D = new vtkX3DExporter();
        exporterToX3D.SetFileName(file.getAbsolutePath());
        exporterToX3D.SetSpeed(5.5);
        exporterToX3D.SetInput(renWin);
        exporterToX3D.Update();
        exporterToX3D.Write();
    }
}
