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
 * Jun 6, 2013
 */
package pt.lsts.neptus.plugins.vtk.visualization;

import java.awt.event.MouseEvent;

import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.plugins.vtk.pointtypes.PointXYZ;
import vtk.vtkCommand;
import vtk.vtkPointPicker;
import vtk.vtkRenderWindowInteractor;
import vtk.vtkRenderer;

/**
 * @author hfq * FIXME - Have to improve, and add interation to visualizer check weather there are really 2 points
 *         choosen add to visualizer coords of choosen points add to visualizer calculated distance between 2 points
 * 
 *         this interation can be used to do area profiling, by choosing 2 points and showing a chart that shows
 *         bathymetry along the the straight line defined by the 2 points.
 * 
 */
public class PointPickingEvent extends vtkCommand {
    private int idx1, idx2;

    private float x1, y1, z1;
    private float x2, y2, z2;

    private boolean pickFirst = false;

    Canvas canvas;

    /**
     * @param canvas
     */
    public PointPickingEvent(Canvas canvas) {
        this.canvas = canvas;

    }

    /**
     * @param interactor
     */
    public void performSinglePick(vtkRenderWindowInteractor interactor) {
        int mouseX, mouseY;
        vtkPointPicker picker = new vtkPointPicker();

        mouseX = interactor.GetEventPosition()[0];
        mouseY = interactor.GetEventPosition()[1];

        interactor.StartPickCallback();

        vtkRenderer ren = interactor.FindPokedRenderer(interactor.GetEventPosition()[0],
                interactor.GetEventPosition()[1]);
        picker.Pick(mouseX, mouseY, 0.0, ren);

        if (!pickFirst) {
            idx1 = picker.GetPointId();
            if (picker.GetDataSet() != null) {
                double[] p = new double[3];
                picker.GetDataSet().GetPoint(idx1, p);
                x1 = (float) p[0];
                y1 = (float) p[1];
                z1 = (float) p[2];
            }
            pickFirst = true;
        }
        else {
            idx2 = picker.GetPointId();
            if (picker.GetDataSet() != null) {
                double[] p = new double[3];
                picker.GetDataSet().GetPoint(idx2, p);
                x2 = (float) p[0];
                y2 = (float) p[1];
                z2 = (float) p[2];
            }
            pickFirst = false;
        }
    }

    /**
     * @param e
     * @param eventId
     */
    public void execute(MouseEvent e, int eventId) {
        // NeptusLog.pub().info("expectacular");
        performSinglePick(canvas.getRenderWindowInteractor());
        NeptusLog.pub().info("1 - Point id: " + idx1);
        NeptusLog.pub().info(
                "1 Point picked - x: " + getPoint1().getX() + " y: " + getPoint1().getY() + " z: " + getPoint1().getZ());

        if (getPoint2() != null) {
            NeptusLog.pub().info("2 Point id: " + idx2);
            NeptusLog.pub().info(
                    "2 Point picked - x2: " + getPoint2().getX() + " y2: " + getPoint2().getY() + " z2: "
                            + getPoint2().getZ());
        }

    }

    /**
     * get first choosen point
     * @return
     */
    public PointXYZ getPoint1() {
        PointXYZ p = new PointXYZ();
        p.setX(x1);
        p.setY(y1);
        p.setZ(z1);

        return (p);
    }

    /**
     * get second choosen point
     * @return
     */
    public PointXYZ getPoint2() {
        PointXYZ p = new PointXYZ();
        p.setX(x2);
        p.setY(y2);
        p.setZ(z2);

        return (p);
    }

    /**
     * get first points choosen index
     * @return idx1
     */
    public int getPointIndex1() {
        return (idx1);
    }
    
    /**
     * get secont point choosen index
     * @return idx2
     */
    public int getPointIndex2() {
        return (idx2);
    }

}
