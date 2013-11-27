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
 * Apr 18, 2013
 */
package pt.lsts.neptus.plugins.vtk.visualization;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Set;

import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.plugins.vtk.pointcloud.PointCloud;
import pt.lsts.neptus.plugins.vtk.pointtypes.PointXYZ;
import pt.lsts.neptus.plugins.vtk.utils.Utils;
import vtk.vtkAbstractPropPicker;
import vtk.vtkActorCollection;
import vtk.vtkAssemblyPath;
import vtk.vtkLODActor;
import vtk.vtkRenderWindowInteractor;
import vtk.vtkRenderer;

/**
 * @author hfq FIXME add keys to change mode (trackball, joystick..)
 */
public class KeyboardEvent implements KeyListener {
    private NeptusInteractorStyle neptusInteractorStyle;

    // private vtkCanvas canvas;
    private Canvas canvas;

    private vtkRenderer renderer;
    private vtkRenderWindowInteractor interactor;

    private LinkedHashMap<String, PointCloud<PointXYZ>> linkedHashMapCloud = new LinkedHashMap<>();

    private Set<String> setOfClouds;

    private PointCloud<PointXYZ> pointCloud;

    // private vtkLODActor marker = new vtkLODActor();
    private boolean markerEnabled = false;

    private enum ColorMappingRelation {
        xMap,
        yMap,
        zMap,
        iMap
    }

    public ColorMappingRelation colorMapRel;

    private Caption captionInfo;
    private Boolean captionEnabled = false;

    private static final boolean VTKIS_ANIMEOFF = false;
    private static final boolean VTKIS_ANIMEON = true;
    //@SuppressWarnings("unused")
    protected static boolean AnimeState = VTKIS_ANIMEOFF;

    /**
     * @param canvas
     * @param linkedHashMapCloud
     * @param neptusInteractorStyle
     */
    public KeyboardEvent(Canvas canvas, LinkedHashMap<String, PointCloud<PointXYZ>> linkedHashMapCloud,
            NeptusInteractorStyle neptusInteractorStyle) {
        this.neptusInteractorStyle = neptusInteractorStyle;
        this.canvas = canvas;
        this.interactor = neptusInteractorStyle.interactor;
        this.renderer = neptusInteractorStyle.renderer;
        this.linkedHashMapCloud = linkedHashMapCloud;
        colorMapRel = ColorMappingRelation.zMap; // on creation map color map is z related

        canvas.addKeyListener(this);
    }

    public void handleEvents(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_J:
                takeSnapShot();
                break;
            case KeyEvent.VK_U:
                try {
                    // canvas.lock();
                    if (!neptusInteractorStyle.lutEnabled) {
                        vtkActorCollection actorCollection = new vtkActorCollection();
                        actorCollection = renderer.GetActors();
                        actorCollection.InitTraversal();

                        for (int i = 0; i < actorCollection.GetNumberOfItems(); ++i) {
                            if (actorCollection.GetNextActor().IsA("vtkActor2D") > 0)
                                continue;
                            setOfClouds = linkedHashMapCloud.keySet();
                            for (String skey : setOfClouds) {
                                pointCloud = linkedHashMapCloud.get(skey);
                                switch (colorMapRel) {
                                    case xMap:
                                        neptusInteractorStyle.getScalarBar().setUpScalarBarLookupTable(
                                                pointCloud.getColorHandler().getLutX());
                                        break;
                                    case yMap:
                                        neptusInteractorStyle.getScalarBar().setUpScalarBarLookupTable(
                                                pointCloud.getColorHandler().getLutY());
                                        break;
                                    case zMap:
                                        neptusInteractorStyle.getScalarBar().setUpScalarBarLookupTable(
                                                pointCloud.getColorHandler().getLutZ());
                                        break;
                                    case iMap:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        canvas.lock();
                        renderer.AddActor(neptusInteractorStyle.getScalarBar().getScalarBarActor());
                        canvas.unlock();
                        neptusInteractorStyle.lutEnabled = true;
                    }
                    else {
                        canvas.lock();
                        renderer.RemoveActor(neptusInteractorStyle.getScalarBar().getScalarBarActor());
                        canvas.unlock();
                        neptusInteractorStyle.lutEnabled = false;
                    }
                    canvas.lock();
                    canvas.Render();
                    canvas.unlock();
                }
                catch (Exception e6) {
                    e6.printStackTrace();
                }
                break;
            case KeyEvent.VK_G:
                try {
                    // canvas.lock();
                    if (!neptusInteractorStyle.gridEnabled) {
                        neptusInteractorStyle.gridActor.TopAxisVisibilityOn();
                        canvas.lock();
                        renderer.AddViewProp(neptusInteractorStyle.gridActor);
                        canvas.unlock();
                        neptusInteractorStyle.gridEnabled = true;
                    }
                    else {
                        canvas.lock();
                        renderer.RemoveViewProp(neptusInteractorStyle.gridActor);
                        canvas.unlock();
                        neptusInteractorStyle.gridEnabled = false;
                    }
                    canvas.lock();
                    // interactor.Render();
                    canvas.Render();
                    canvas.unlock();
                }
                catch (Exception e5) {
                    e5.printStackTrace();
                }
                break;
            // case KeyEvent.VK_C: // FIXME - not good enough, better check this one for a better implementation.
            // problems: seems to be disconected of the rendered actor
            // try {
            //
            // if (!neptusInteractorStyle.compassEnabled) {
            // canvas.lock();
            // neptusInteractorStyle.compass.addCompassToVisualization(interactor);
            // canvas.unlock();
            // neptusInteractorStyle.compassEnabled = true;
            // }
            // else {
            // canvas.lock();
            // neptusInteractorStyle.compass.removeCompassFromVisualization(interactor);
            // canvas.unlock();
            // neptusInteractorStyle.compassEnabled = false;
            // }
            // canvas.lock();
            // canvas.Render();
            // canvas.unlock();
            // }
            // catch (Exception e4) {
            // e4.printStackTrace();
            // }
            // break;
            // case KeyEvent.VK_W:
            // try {
            // if (!neptusInteractorStyle.wireframeRepEnabled) {
            // neptusInteractorStyle.wireframeRepEnabled = true;
            // neptusInteractorStyle.solidRepEnabled = false;
            // neptusInteractorStyle.pointRepEnabled = false;
            //
            // setOfClouds = linkedHashMapCloud.keySet();
            // for (String sKey : setOfClouds) {
            // vtkLODActor tempActor = new vtkLODActor();
            // pointCloud = linkedHashMapCloud.get(sKey);
            // tempActor = pointCloud.getCloudLODActor();
            // tempActor.GetProperty().SetRepresentationToWireframe();
            // }
            // }
            // }
            // catch (Exception e3) {
            // e3.printStackTrace();
            // }
            // break;
            // case KeyEvent.VK_S:
            // try {
            // if (!neptusInteractorStyle.solidRepEnabled) {
            // neptusInteractorStyle.solidRepEnabled = true;
            // neptusInteractorStyle.wireframeRepEnabled = false;
            // neptusInteractorStyle.pointRepEnabled = false;
            //
            // for (String sKey : setOfClouds) {
            // vtkLODActor tempActor = new vtkLODActor();
            // pointCloud = linkedHashMapCloud.get(sKey);
            // tempActor = pointCloud.getCloudLODActor();
            // tempActor.GetProperty().SetRepresentationToSurface();
            // }
            // }
            // }
            // catch (Exception e2) {
            // e2.printStackTrace();
            // }
            // break;
            case KeyEvent.VK_P: // FIXME the default vtk key handler creates a vtkOpenGL object that sends a exception
                                // on depth exaggeration (casting vtkLODActor to vtkOpenGL)
                try {
                    // if (!neptusInteractorStyle.pointRepEnabled) {
                    // neptusInteractorStyle.pointRepEnabled = true;
                    // neptusInteractorStyle.solidRepEnabled = false;
                    // neptusInteractorStyle.wireframeRepEnabled = false;
                    //
                    // for (String sKey : setOfClouds) {
                    // vtkLODActor tempActor = new vtkLODActor();
                    // pointCloud = linkedHashMapCloud.get(sKey);
                    // tempActor = pointCloud.getCloudLODActor();
                    // tempActor.GetProperty().SetRepresentationToPoints();
                    // }
                    // }
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
                break;
            case KeyEvent.VK_M:
                try {
                    canvas.lock();
                    if (!markerEnabled) {
                        markerEnabled = true;
                        // neptusInteractorStyle.renderer.AddActor(marker);
                    }
                    else {
                        markerEnabled = false;
                    }
                    canvas.unlock();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
                break;
            case KeyEvent.VK_I:
                if (!captionEnabled) {
                    try {
                        canvas.lock();
                        // vtkActorCollection actorCollection = new vtkActorCollection();
                        // actorCollection = renderer.GetActors();
                        // actorCollection.InitTraversal();
                        // for (int i = 0; i < actorCollection.GetNumberOfItems(); ++i) {
                        // vtkLODActor tempActor = new vtkLODActor();
                        // tempActor = (vtkLODActor) actorCollection.GetNextActor();
                        // setOfClouds = linkedHashMapCloud.keySet();
                        // for (String sKey : setOfClouds) {
                        // vtkLODActor tempActorFromHashMap = new vtkLODActor();
                        // pointCloud = linkedHashMapCloud.get(sKey);
                        // tempActorFromHashMap = pointCloud.getCloudLODActor();
                        // if (tempActor.equals(tempActorFromHashMap)) {
                        // captionInfo = new Caption(4, 250, pointCloud.getNumberOfPoints(), pointCloud.getCloudName(),
                        // pointCloud.getBounds(), pointCloud.getMemorySize());
                        // renderer.AddActor(captionInfo.getCaptionNumberOfPointsActor());
                        // renderer.AddActor(captionInfo.getCaptionCloudNameActor());
                        // renderer.AddActor(captionInfo.getCaptionMemorySizeActor());
                        // renderer.AddActor(captionInfo.getCaptionCloudBoundsActor());
                        // interactor.Render();
                        // }
                        // }
                        // }
                        // setOfClouds = linkedHashMapCloud.keySet();
                        captionInfo = new Caption(4, 250, linkedHashMapCloud.get("multibeam").getNumberOfPoints(),
                                linkedHashMapCloud.get("multibeam").getCloudName(), linkedHashMapCloud.get("multibeam")
                                        .getBounds(), linkedHashMapCloud.get("multibeam").getMemorySize());

                        renderer.AddActor(captionInfo.getCaptionNumberOfPointsActor());
                        renderer.AddActor(captionInfo.getCaptionCloudNameActor());
                        renderer.AddActor(captionInfo.getCaptionMemorySizeActor());
                        renderer.AddActor(captionInfo.getCaptionCloudBoundsActor());

                        captionEnabled = true;
                        canvas.unlock();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        canvas.lock();
                        renderer.RemoveActor(captionInfo.getCaptionNumberOfPointsActor());
                        renderer.RemoveActor(captionInfo.getCaptionCloudNameActor());
                        renderer.RemoveActor(captionInfo.getCaptionMemorySizeActor());
                        renderer.RemoveActor(captionInfo.getCaptionCloudBoundsActor());
                        captionEnabled = false;
                        canvas.Render();
                        canvas.unlock();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case KeyEvent.VK_PLUS: // increment size of rendered cell point
                try {

                    vtkActorCollection actorCollection = new vtkActorCollection();
                    actorCollection = renderer.GetActors();
                    actorCollection.InitTraversal();

                    for (int i = 0; i < actorCollection.GetNumberOfItems(); ++i) {
                        vtkLODActor tempActor = new vtkLODActor();
                        tempActor = (vtkLODActor) actorCollection.GetNextActor();
                        setOfClouds = linkedHashMapCloud.keySet();
                        for (String sKey : setOfClouds) {
                            pointCloud = linkedHashMapCloud.get(sKey);
                            if (tempActor.equals(pointCloud.getCloudLODActor())) {
                                double pointSize = tempActor.GetProperty().GetPointSize();
                                if (pointSize <= 9.0) {
                                    canvas.lock();
                                    tempActor.GetProperty().SetPointSize(pointSize + 1);
                                    canvas.Render();
                                    canvas.unlock();
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case KeyEvent.VK_MINUS: // case '-': // decrement size of rendered cell point
                try {
                    vtkActorCollection actorCollection = new vtkActorCollection();
                    actorCollection = renderer.GetActors();
                    actorCollection.InitTraversal();

                    for (int i = 0; i < actorCollection.GetNumberOfItems(); ++i) {
                        vtkLODActor tempActor = new vtkLODActor();
                        tempActor = (vtkLODActor) actorCollection.GetNextActor();
                        setOfClouds = linkedHashMapCloud.keySet();
                        for (String sKey : setOfClouds) {
                            pointCloud = linkedHashMapCloud.get(sKey);
                            if (tempActor.equals(pointCloud.getCloudLODActor())) {
                                double pointSize = tempActor.GetProperty().GetPointSize();
                                if (pointSize > 1.0) {
                                    canvas.lock();
                                    tempActor.GetProperty().SetPointSize(pointSize - 1);
                                    canvas.Render();
                                    canvas.unlock();
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // case '1':
            // //int numberOfProps = neptusInteractorStyle.renderer.GetNumberOfPropsRendered();
            // //System.out.println("numberOfProps: " + numberOfProps);
            // setOfClouds = linkedHashMapCloud.keySet();
            // for (String sKey : setOfClouds) {
            // //System.out.println("String from set: " + setOfClouds);
            // vtkLODActor tempActor = new vtkLODActor();
            // tempActor = linkedHashMapCloud.get(sKey);
            // //tempActor.GetProperty().SetColor(PointCloudHandlers.getRandomColor());
            // }
            // neptusInteractorStyle.interactor.Render();
            // break;
            case KeyEvent.VK_6:
                try {
                    if (!(colorMapRel == ColorMappingRelation.iMap)) {
                        setOfClouds = linkedHashMapCloud.keySet();
                        for (String sKey : setOfClouds) {
                            pointCloud = linkedHashMapCloud.get(sKey);
                            if (pointCloud.isHasIntensities()) {
                                pointCloud.getPoly().GetPointData()
                                        .SetScalars(pointCloud.getColorHandler().getIntensities());
                                if (neptusInteractorStyle.lutEnabled)
                                    neptusInteractorStyle.getScalarBar().setUpScalarBarLookupTable(
                                            pointCloud.getColorHandler().getLutIntensities());
                                colorMapRel = ColorMappingRelation.iMap;
                            }
                        }
                        canvas.lock();
                        canvas.Render();
                        canvas.unlock();
                    }
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
                break;

            case KeyEvent.VK_7: // color map X axis related
                try {

                    if (!(colorMapRel == ColorMappingRelation.xMap)) {
                        setOfClouds = linkedHashMapCloud.keySet();
                        for (String sKey : setOfClouds) {
                            pointCloud = linkedHashMapCloud.get(sKey);
                            pointCloud.getPoly().GetPointData().SetScalars(pointCloud.getColorHandler().getColorsX());
                            if (neptusInteractorStyle.lutEnabled)
                                neptusInteractorStyle.getScalarBar().setUpScalarBarLookupTable(
                                        pointCloud.getColorHandler().getLutX());
                            colorMapRel = ColorMappingRelation.xMap;

                        }
                        canvas.lock();
                        canvas.Render();
                        canvas.unlock();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case KeyEvent.VK_8: // color map Y axis related
                try {
                    if (!(colorMapRel == ColorMappingRelation.yMap)) {
                        setOfClouds = linkedHashMapCloud.keySet();
                        for (String sKey : setOfClouds) {
                            pointCloud = linkedHashMapCloud.get(sKey);
                            pointCloud.getPoly().GetPointData().SetScalars(pointCloud.getColorHandler().getColorsY());
                            if (neptusInteractorStyle.lutEnabled)
                                neptusInteractorStyle.getScalarBar().setUpScalarBarLookupTable(
                                        pointCloud.getColorHandler().getLutY());
                            colorMapRel = ColorMappingRelation.yMap;
                        }
                        canvas.lock();
                        canvas.Render();
                        canvas.unlock();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case KeyEvent.VK_9: // color map Z axis related
                try {
                    if (!(colorMapRel == ColorMappingRelation.zMap)) {
                        setOfClouds = linkedHashMapCloud.keySet();
                        for (String sKey : setOfClouds) {
                            pointCloud = linkedHashMapCloud.get(sKey);
                            pointCloud.getPoly().GetPointData().SetScalars(pointCloud.getColorHandler().getColorsZ());
                            if (neptusInteractorStyle.lutEnabled)
                                neptusInteractorStyle.getScalarBar().setUpScalarBarLookupTable(
                                        pointCloud.getColorHandler().getLutZ());
                            colorMapRel = ColorMappingRelation.zMap;
                        }
                        canvas.lock();
                        canvas.Render();
                        canvas.unlock();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case KeyEvent.VK_R:
                try {
                    canvas.lock();
                    // renderer.GetActiveCamera().SetPosition(0.0 ,0.0 ,100);
                    renderer.GetActiveCamera().SetViewUp(0.0, 0.0, -1.0);
                    renderer.ResetCamera();
                    canvas.Render();
                    canvas.unlock();
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case KeyEvent.VK_F:
                canvas.lock();
                AnimeState = VTKIS_ANIMEON;

                vtkAssemblyPath path = null;
                neptusInteractorStyle.FindPokedRenderer(interactor.GetEventPosition()[0],
                        interactor.GetEventPosition()[1]);
                interactor.GetPicker().Pick(interactor.GetEventPosition()[0], interactor.GetEventPosition()[1], 0.0,
                        renderer);

                vtkAbstractPropPicker picker;
                if ((picker = (vtkAbstractPropPicker) interactor.GetPicker()) != null) {
                    path = picker.GetPath();
                }
                if (path != null) {
                    interactor.FlyTo(renderer, picker.GetPickPosition()[0], picker.GetPickPosition()[1],
                            picker.GetPickPosition()[2]);
                }
                AnimeState = VTKIS_ANIMEOFF;
                canvas.unlock();
                break;
            default:
                break;
        }
    }

    /**
     * Syncronously take a snapshot of a 3D view Saves on neptus directory
     */
    void takeSnapShot() {
        Utils.goToAWTThread(new Runnable() {

            @Override
            public void run() {
                try {
                    neptusInteractorStyle.FindPokedRenderer(interactor.GetEventPosition()[0],
                            interactor.GetEventPosition()[1]);
                    neptusInteractorStyle.wif.SetInput(interactor.GetRenderWindow());
                    neptusInteractorStyle.wif.Modified();
                    neptusInteractorStyle.snapshotWriter.Modified();

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssmm").format(Calendar.getInstance()
                            .getTimeInMillis());
                    timeStamp = "snapshot_" + timeStamp;
                    NeptusLog.pub().info("timeStamp: " + timeStamp);

                    neptusInteractorStyle.snapshotWriter.SetFileName(timeStamp);

                    if (!canvas.isWindowSet()) {
                        canvas.lock();
                        canvas.Render();
                        canvas.unlock();
                    }

                    canvas.lock();
                    neptusInteractorStyle.wif.Update();
                    canvas.unlock();

                    neptusInteractorStyle.snapshotWriter.Write();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        handleEvents(e.getKeyCode());
    }
}
