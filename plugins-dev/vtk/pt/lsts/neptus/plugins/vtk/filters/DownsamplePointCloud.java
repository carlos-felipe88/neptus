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
 * May 7, 2013
 */
package pt.lsts.neptus.plugins.vtk.filters;

import java.util.Date;

import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.plugins.vtk.pointcloud.PointCloud;
import pt.lsts.neptus.plugins.vtk.pointtypes.PointXYZ;
import vtk.vtkCleanPolyData;
import vtk.vtkPoints;

/**
 * @author hfq FIXME
 * this method isn't the best one to downsample... it simply cleans data within a tolerance
 */
public class DownsamplePointCloud {
    protected int numberOfPoints = 0;
    public PointCloud<PointXYZ> pointCloud;
    private PointCloud<PointXYZ> outputDownsampledCloud;
    private double tolerance;
    public Boolean isDownsampleDone = false;

    public DownsamplePointCloud(PointCloud<PointXYZ> pointCloud, double tolerance) {
        long lDateTime = new Date().getTime();
        NeptusLog.pub().info("Init downsampling - Time in milliseconds: " + lDateTime);

        this.pointCloud = pointCloud;
        this.numberOfPoints = pointCloud.getNumberOfPoints();
        this.tolerance = tolerance;
        outputDownsampledCloud = new PointCloud<>();
        NeptusLog.pub().info("constructor class donwnsample");
        downsample();

        long lDateTime2 = new Date().getTime();
        NeptusLog.pub().info("Final downsampling - Time in milliseconds: " + lDateTime2);
    }

    private void downsample() {
        try {
            vtkCleanPolyData cleanPolyData = new vtkCleanPolyData();
            // cleanPolyData.SetInput(pointCloud.getPoly());
            cleanPolyData.SetInputConnection(pointCloud.getPoly().GetProducerPort());
            cleanPolyData.SetTolerance(tolerance);
            cleanPolyData.Update();

            NeptusLog.pub().info(
                    "Number of Points from cleanPolyData: "
                            + String.valueOf(cleanPolyData.GetOutput().GetPoints().GetNumberOfPoints()));

            // outputDownsampledCloud.setPoly(cleanPolyData.GetOutput());
            // outputDownsampledCloud.setPoints(outputDownsampledCloud.getPoly().GetPoints());

            outputDownsampledCloud.setCloudName("downsampledCloud");
            NeptusLog.pub().info("cloud name: " + outputDownsampledCloud.getCloudName());
            outputDownsampledCloud.setPoints(cleanPolyData.GetOutput().GetPoints());
            outputDownsampledCloud.setNumberOfPoints(outputDownsampledCloud.getPoints().GetNumberOfPoints());
            NeptusLog.pub().info("Number of points: " + outputDownsampledCloud.getNumberOfPoints());

            vtkPoints points = outputDownsampledCloud.getPoints();

            for (int i = 0; i < outputDownsampledCloud.getNumberOfPoints(); ++i) {
                outputDownsampledCloud.getVerts().InsertNextCell(1);
                outputDownsampledCloud.getVerts().InsertCellPoint(
                        pointCloud.getPoints().InsertNextPoint(points.GetPoint(i)));
            }

            // outputDownsampledCloud.setVerts()

            // outputDownsampledCloud.createLODActorFromPoints();

            // vtkPolyDataMapper outputDataMapper = new vtkPolyDataMapper();
            // outputDataMapper.SetInputConnection(cleanPolyData.GetOutputPort());
            // outputDownsampledCloud.setNumberOfPoints(cleanPolyData.

            isDownsampleDone = true;
        }
        catch (Exception e) {
            NeptusLog.pub().info("Exception on cloud downsampling");
            e.printStackTrace();
        }
    }

    /**
     * @return the outputDownsampledCloud
     */
    public PointCloud<PointXYZ> getOutputDownsampledCloud() {
        return outputDownsampledCloud;
    }

    /**
     * @param outputDownsampledCloud the outputDownsampledCloud to set
     */
    public void setOutputDownsampledCloud(PointCloud<PointXYZ> outputDownsampledCloud) {
        this.outputDownsampledCloud = outputDownsampledCloud;
    }
}
