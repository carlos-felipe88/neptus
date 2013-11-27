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
 * Apr 19, 2013
 */
package pt.lsts.neptus.plugins.vtk.pointcloud;

import pt.lsts.neptus.plugins.vtk.pointtypes.PointXYZ;
import vtk.vtkLookupTable;
import vtk.vtkPolyData;
import vtk.vtkShortArray;
import vtk.vtkUnsignedCharArray;

/**
 * @author hfq
 * Handles Pointcloud colors x, y, z and intensities
 * FIXME - fix generation of intensities colors.
 */
public class PointCloudHandlers<T extends PointXYZ> {    
    private vtkUnsignedCharArray colorsX;
    private vtkUnsignedCharArray colorsY;
    private vtkUnsignedCharArray colorsZ;
    private vtkUnsignedCharArray colorsI;
    private vtkShortArray intensities;
    
    private vtkLookupTable lutX;
    private vtkLookupTable lutY;
    private vtkLookupTable lutZ;
    private vtkLookupTable lutIntensities;
    
    public PointCloudHandlers() {
        setColorsX(new vtkUnsignedCharArray());
        setColorsY(new vtkUnsignedCharArray());
        setColorsZ(new vtkUnsignedCharArray());
        setColorsI(new vtkUnsignedCharArray());
        setLutX(new vtkLookupTable());
        setLutY(new vtkLookupTable());
        setLutZ(new vtkLookupTable());
        setLutIntensities(new vtkLookupTable());
    }
    
    /**
     * Generates color scalars and Lookuptables
     * @param polyData
     * @param bounds
     */
    public void generatePointCloudColorHandlers(vtkPolyData polyData, double[] bounds) {
        getLutX().SetRange(bounds[0], bounds[1]);
        getLutX().SetScaleToLinear();
        getLutX().Build();
        getLutY().SetRange(bounds[2], bounds[3]);
        getLutY().SetScaleToLinear();
        getLutY().Build();
        getLutZ().SetRange(bounds[4], bounds[5]);
        getLutZ().SetScaleToLinear();
        getLutZ().Build();
               
        colorsX.SetNumberOfComponents(3);
        colorsY.SetNumberOfComponents(3);
        colorsZ.SetNumberOfComponents(3);      
        colorsX.SetName("colorsX");
        colorsY.SetName("colorsY");
        colorsZ.SetName("colorsZ");
        
        for (int i = 0; i < polyData.GetNumberOfPoints(); ++i) {
            double[] point = new double[3];
            polyData.GetPoint(i, point);
            
            double[] xDColor = new double[3];
            double[] yDColor = new double[3];
            double[] zDColor = new double[3];
                           
            getLutX().GetColor(point[0], xDColor);
            getLutY().GetColor(point[1], yDColor);
            getLutZ().GetColor(point[2], zDColor);
            
            char[] colorx = new char[3];
            char[] colory = new char[3];
            char[] colorz = new char[3];
            
            for (int j = 0; j < 3; ++j) {
                colorx[j] = (char) (255.0 * xDColor[j]);
                colory[j] = (char) (255.0 * yDColor[j]);
                colorz[j] = (char) (255.0 * zDColor[j]);
            }
                       
            colorsX.InsertNextTuple3(colorx[0], colorx[1], colorx[2]);
            colorsY.InsertNextTuple3(colory[0], colory[1], colory[2]);
            colorsZ.InsertNextTuple3(colorz[0], colorz[1], colorz[2]);
        }
    }
    
    /**
     * Generates color scalars and Lookuptables
     * @param polyData
     * @param bounds
     * @param intensities 
     */
    public void generatePointCloudColorHandlers(vtkPolyData polyData, double[] bounds, vtkShortArray intensities) {      
        //colorLookupTable.SetValueRange(getBounds()[4], getBounds()[5]);        
        //colorLookupTable.SetHueRange(0, 1);
        //colorLookupTable.SetSaturationRange(1, 1);
        //colorLookupTable.SetValueRange(1, 1);
        //colorLookupTable.SetTableRange(getBounds()[4], getBounds()[5]);
            
        getLutX().SetRange(bounds[0], bounds[1]);
        getLutX().SetScaleToLinear();
        getLutX().Build();
        getLutY().SetRange(bounds[2], bounds[3]);
        getLutY().SetScaleToLinear();
        getLutY().Build();
        //getLutZ().SetValueRange(-bounds[5], -bounds[4]);
        getLutZ().SetRange(bounds[4], bounds[5]);
        getLutZ().SetScaleToLinear();
        getLutZ().Build();
        
        colorsX.SetNumberOfComponents(3);
        colorsY.SetNumberOfComponents(3);
        colorsZ.SetNumberOfComponents(3);      
        colorsX.SetName("colorsX");
        colorsY.SetName("colorsY");
        colorsZ.SetName("colorsZ");
        
        
        if (intensities.GetDataSize() != 0) {
            NeptusLog.pub().info("got into built color handler for intens");
            //NeptusLog.pub().info("Data max: " + intensities.GetValueRange()[0]);
            getLutIntensities().SetRange(0, 32000);
            //intensities.SetNumberOfComponents(1);
            //intensities.CreateDefaultLookupTable();
            //setLutIntensities(intensities.GetLookupTable());
            //getLutIntensities().SetRange(0, 32000);
            
            getLutIntensities().SetScaleToLinear();
            getLutIntensities().Build();
            
            colorsI.SetNumberOfComponents(3);
            colorsI.SetName("colorsI");
        }
        
        NeptusLog.pub().info("gonna create colors");
        
        for (int i = 0; i < polyData.GetNumberOfPoints(); ++i) {
            double[] point = new double[3];
            polyData.GetPoint(i, point);
            
            double[] xDColor = new double[3];
            double[] yDColor = new double[3];
            double[] zDColor = new double[3];
            double[] iDColor = new double[3];
                           
            getLutX().GetColor(point[0], xDColor);
            getLutY().GetColor(point[1], yDColor);
            getLutZ().GetColor(point[2], zDColor);
            //getLutIntensities().GetColor(intensities.GetValue(i), iDColor);
            getLutIntensities().GetColor(0.0, iDColor);
            //NeptusLog.pub().info("intens value: " + intensities.GetValue(i));
            
            char[] colorx = new char[3];
            char[] colory = new char[3];
            char[] colorz = new char[3];
            char[] colori = new char[3];
            
            for (int j = 0; j < 3; ++j) {
                colorx[j] = (char) (255.0 * xDColor[j]);
                colory[j] = (char) (255.0 * yDColor[j]);
                colorz[j] = (char) (255.0 * zDColor[j]);
                colori[j] = (char) (255.0 * iDColor[j]);
            }                      
            //NeptusLog.pub().info("colors int: " + colori[0] + " " + colori[1] + " " + colori[2]);
            
            colorsX.InsertNextTuple3(colorx[0], colorx[1], colorx[2]);
            colorsY.InsertNextTuple3(colory[0], colory[1], colory[2]);
            colorsZ.InsertNextTuple3(colorz[0], colorz[1], colorz[2]);
            //colorsI.InsertNextTuple3(colori[0], colori[1], colori[2]);
            colorsI.InsertNextTuple3(0, 0, 0);
        }
        NeptusLog.pub().info("Inserted colors on tuples");
    }   

    /**
     * @return the colorsX
     */
    public vtkUnsignedCharArray getColorsX() {
        return colorsX;
    }

    /**
     * @param colorsX the colorsX to set
     */
    private void setColorsX(vtkUnsignedCharArray colorsX) {
        this.colorsX = colorsX;
    }

    /**
     * @return the colorsY
     */
    public vtkUnsignedCharArray getColorsY() {
        return colorsY;
    }

    /**
     * @param colorsY the colorsY to set
     */
    private void setColorsY(vtkUnsignedCharArray colorsY) {
        this.colorsY = colorsY;
    }

    /**
     * @return the colorsZ
     */
    public vtkUnsignedCharArray getColorsZ() {
        return colorsZ;
    }
    
    /**
     * @param colorsZ the colorsZ to set
     */
    private void setColorsZ(vtkUnsignedCharArray colorsZ) {
        this.colorsZ = colorsZ;
    }    
    
    /**
     * @return the lutX
     */
    public vtkLookupTable getLutX() {
        return lutX;
    }

    /**
     * @param lutX the lutX to set
     */
    private void setLutX(vtkLookupTable lutX) {
        this.lutX = lutX;
    }

    /**
     * @return the lutY
     */
    public vtkLookupTable getLutY() {
        return lutY;
    }

    /**
     * @param lutY the lutY to set
     */
    private void setLutY(vtkLookupTable lutY) {
        this.lutY = lutY;
    }

    /**
     * @return the lutZ
     */
    public vtkLookupTable getLutZ() {
        return lutZ;
    }

    /**
     * @param lutZ the lutZ to set
     */
    private void setLutZ(vtkLookupTable lutZ) {
        this.lutZ = lutZ;
    }

    /**
     * @return the intensities
     */
    public vtkShortArray getIntensities() {
        return intensities;
    }

//    /**
//     * @param intensities the intensities to set
//     */
//    private void setIntensities(vtkShortArray intensities) {
//        this.intensities = intensities;
//    }

    /**
     * @return the lutIntensities
     */
    public vtkLookupTable getLutIntensities() {
        return lutIntensities;
    }

    /**
     * @param lutIntensities the lutIntensities to set
     */
    public void setLutIntensities(vtkLookupTable lutIntensities) {
        this.lutIntensities = lutIntensities;
    }

    /**
     * @return the colorsI
     */
    public vtkUnsignedCharArray getColorsI() {
        return colorsI;
    }

    /**
     * @param colorsI the colorsI to set
     */
    public void setColorsI(vtkUnsignedCharArray colorsI) {
        this.colorsI = colorsI;
    }
}
