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
 * May 1, 2013
 */
package pt.up.fe.dceg.neptus.plugins.vtk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Date;

import pt.up.fe.dceg.neptus.NeptusLog;
import pt.up.fe.dceg.neptus.imc.IMCMessage;
import pt.up.fe.dceg.neptus.mp.SystemPositionAndAttitude;
import pt.up.fe.dceg.neptus.mra.NeptusMRA;
import pt.up.fe.dceg.neptus.mra.api.BathymetryInfo;
import pt.up.fe.dceg.neptus.mra.api.BathymetryParser;
import pt.up.fe.dceg.neptus.mra.api.BathymetryPoint;
import pt.up.fe.dceg.neptus.mra.api.BathymetrySwath;
import pt.up.fe.dceg.neptus.mra.importers.IMraLog;
import pt.up.fe.dceg.neptus.mra.importers.IMraLogGroup;
import pt.up.fe.dceg.neptus.mra.importers.deltat.DeltaTHeader;
import pt.up.fe.dceg.neptus.plugins.vtk.pointcloud.PointCloud;
import pt.up.fe.dceg.neptus.plugins.vtk.pointtypes.PointXYZ;
import pt.up.fe.dceg.neptus.util.bathymetry.LocalData;

/**
 * @author hfq
 *
 */
public class MultibeamDeltaTParser implements BathymetryParser{
    private final IMraLogGroup logGroup;
    private final IMraLog stateParserLogMra;
    private IMCMessage stateIMCMsg;
    
    private File file;
    private FileInputStream fis;
    private final FileChannel channel;
    private ByteBuffer buf;
    private long currPos = 0;
    
    private int realNumberOfBeams = 0;
    private int totalNumberPoints = 0;
    
    private int count = 0;
    
    public BathymetryInfo info;
    
    private int numberSwaths = 0;
    
    private double maxLat = -Math.PI;
    private double minLat = Math.PI;
    private double maxLon = -Math.PI * 2;
    private double minLon = Math.PI * 2;
    //private double maxLat = -(Math.PI)/2;   // 90º North (+)
    //private double minLat = +(Math.PI)/2;   // 90º South (-)
    //private double maxLon = -Math.PI;       // 180º East (+)
    //private double minLon = +Math.PI;       // 180º West (-)
    
    public PointCloud<PointXYZ> pointCloud;
    
    private final LocalData ld;

    public MultibeamDeltaTParser(IMraLogGroup source, PointCloud<PointXYZ> pointCloud) {
        this.logGroup = source;
        this.pointCloud = pointCloud;
        ld = new LocalData(logGroup.getFile("tides.txt"));
        
        if(source.getFile("data.83P") != null)
            file = source.getFile("data.83P");
        else if (source.getFile("multibeam.83P") != null)
            file = source.getFile("multibeam.83P");
        
        try {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            NeptusLog.pub().info("File not found: " + e);        
            e.printStackTrace();
        }
        catch (Exception ioe) {
            NeptusLog.pub().info("Exception while reading the file: " + ioe);
            ioe.printStackTrace();
        }
        
        channel = fis.getChannel();       
        stateParserLogMra = logGroup.getLog("EstimatedState");
        
        initialize();
    }

    

    private double getTideOffset(long timestampMillis) {
        
        File tidesF = logGroup.getFile("tides.txt");;
        if (tidesF == null)
            return 0;
        else {
            try {
                return ld.getTidePrediction(new Date(timestampMillis), false);
            }
            catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    /**
     * 
     */
    private void initialize() {
        info = new BathymetryInfo();

        BathymetrySwath bs;
        
        while ((bs = nextSwath()) != null) {
            
            double lat = bs.getPose().getPosition().getLatitudeAsDoubleValueRads();
            double lon = bs.getPose().getPosition().getLongitudeAsDoubleValueRads();
            double tideOffset = getTideOffset(bs.getTimestamp());
            //NeptusLog.pub().info("tideoff: " + tideOffset);
            maxLat = Math.max(lat, maxLat);
            minLat = Math.min(lat, minLat);
            maxLon = Math.max(lon, maxLon);
            minLon = Math.min(lon, minLon);
      
            if (!NeptusMRA.approachToIgnorePts) {              
                for(int c = 0; c < bs.numBeams; c += NeptusMRA.ptsToIgnore) {
                    BathymetryPoint p = bs.getData()[c];
                    ++count;
                                       
                    info.minDepth = (float) Math.min(info.minDepth, p.depth + tideOffset);
                    info.maxDepth = (float) Math.max(info.maxDepth, p.depth + tideOffset);
                      
                    pointCloud.getVerts().InsertNextCell(1);                
                    pointCloud.getVerts().InsertCellPoint(pointCloud.getPoints().InsertNextPoint(p.north, p.east, p.depth - tideOffset));
                }  
            }
            else {
                for(int c = 0; c < bs.numBeams; c ++) {
                    if (Math.random() > 1.0 / NeptusMRA.ptsToIgnore)
                        continue;        
                    BathymetryPoint p = bs.getData()[c];                
                    
                    ++count;
                    info.minDepth = (float) Math.min(info.minDepth, p.depth + tideOffset);
                    info.maxDepth = (float) Math.max(info.maxDepth, p.depth + tideOffset);
            
                    pointCloud.getVerts().InsertNextCell(1);
                    pointCloud.getVerts().InsertCellPoint(pointCloud.getPoints().InsertNextPoint(p.north, p.east,  p.depth - tideOffset));
                }
            }
            //totalNumberPoints = totalNumberPoints + bs.numBeams;
            totalNumberPoints += count;
            count = 0;
            realNumberOfBeams = 0;
            numberSwaths++;
            // NeptusLog.pub().info("<###> total number of points: " + totalNumberPoints);
        }
        pointCloud.setNumberOfPoints(totalNumberPoints);
    }

    @Override
    public long getFirstTimestamp() {
        return 0;
    }

    @Override
    public long getLastTimestamp() {
        return 0;
    }

    @Override
    public BathymetryInfo getBathymetryInfo() {
        return null;
    }

    @Override
    public BathymetrySwath getSwathAt(long timestamp) {
        return null;
    }

    @Override
    public BathymetrySwath nextSwath() {
        try {

            if(currPos >= channel.size()) // got to the end of file
                return null;
            //NeptusLog.pub().info("<###> Swath number: " + numberSwaths);

            BathymetryPoint data[];
                
                // read ping header
            buf = channel.map(MapMode.READ_ONLY, currPos, 256);
            DeltaTHeader header = new DeltaTHeader();
            header.parse(buf);
            //printHeaderArgs(header);         
            //MultibeamDeltaTHeader header = new MultibeamDeltaTHeader(buf);
    
                // Parse and process data
            buf = channel.map(MapMode.READ_ONLY, currPos + 256, header.numBeams * 2); // numberBeam * 2 -> number of bytes
            data = new BathymetryPoint[header.numBeams];

                // get vehicle pos at the timestamp
            stateIMCMsg = stateParserLogMra.getEntryAtOrAfter(header.timestamp + NeptusMRA.timestampMultibeamIncrement);  // NeptusMRA.timestampIncrement 3600000 logs from 16-05-2013 need + 3600000 
            //NeptusLog.pub().info("header 83P timestamp: " + header.timestamp);

            SystemPositionAndAttitude pose = new SystemPositionAndAttitude();
            
            if (stateIMCMsg == null) {
                //NeptusLog.pub().info("IMC message is null");
                return null;
            }
            else {
                pose.getPosition().setLatitudeRads(stateIMCMsg.getDouble("lat"));         
                maxLat = Math.max(maxLat, pose.getPosition().getLatitudeAsDoubleValueRads());
                minLat = Math.min(minLat, pose.getPosition().getLatitudeAsDoubleValueRads());
                pose.getPosition().setLongitudeRads(stateIMCMsg.getDouble("lon"));
                maxLon = Math.max(maxLon, pose.getPosition().getLongitudeAsDoubleValueRads());
                minLon = Math.min(minLon, pose.getPosition().getLongitudeAsDoubleValueRads());
                pose.getPosition().setOffsetNorth(stateIMCMsg.getDouble("x"));
                pose.getPosition().setOffsetEast(stateIMCMsg.getDouble("y"));
                pose.getPosition().setDepth(stateIMCMsg.getDouble("depth"));
                //NeptusLog.pub().info("get x (IMCMessage): " + stateIMCMsg.getDouble("x"));
                //NeptusLog.pub().info("get y (IMCMessage): " + stateIMCMsg.getDouble("y"));
                //NeptusLog.pub().info("get depth (IMCMessage): " + stateIMCMsg.getDouble("depth"));                
                //pose.setRoll(stateIMCMsg.getDouble("phi"));
                //pose.setPitch(stateIMCMsg.getDouble("theta"));
                pose.setYaw(stateIMCMsg.getDouble("psi"));
                //NeptusLog.pub().info("get psi (IMCMessage): " + stateIMCMsg.getDouble("psi"));  
                //printPose(pose);

                for(int i = 0; i < header.numBeams; ++i) {
                    double range = buf.getShort(i*2) * (header.rangeResolution / 1000.0f);  // range resolution in mm -> 1000, range in meters -> short
                              
                    if(range == 0.0) { // when buf.getShort(i*2) -> range on 83P, is = 0, data is discarded
                        continue;
                    }
                    
                    double angle = header.startAngle + header.angleIncrement * i;
                    double height = range * Math.cos(Math.toRadians(angle)) + pose.getPosition().getDepth();                
                    double xBeamOffset = range * Math.sin(Math.toRadians(angle));                
                        // heading
                    double psi = -pose.getYaw(); //+ Math.PI
                    if (NeptusMRA.yawMultibeamIncrement) {
                        psi += Math.PI;
                    }
                    double ox = xBeamOffset * Math.sin(psi);               
                    double oy = xBeamOffset * Math.cos(psi);
                    
                    
                    data[realNumberOfBeams] = new BathymetryPoint((float) (pose.getPosition().getOffsetNorth() + ox),
                            (float) (pose.getPosition().getOffsetEast() + oy), (float) height);

                    //pointCloud.getVerts().InsertNextCell(1);
                    //pointCloud.getVerts().InsertCellPoint(pointCloud.getPoints().InsertNextPoint(
                    //        (pose.getPosition().getOffsetNorth() + ox),
                    //        (pose.getPosition().getOffsetEast() + oy),
                    //        height));
                    realNumberOfBeams++;
                }                       
                currPos += header.numBytes;     // advance to the next ping (position file pointer);
                
                BathymetrySwath swath = new BathymetrySwath(header.timestamp, pose, data);
                //swath.numBeams = header.numBeams; <- can't be this because of errors on range data available on the 83P file
                swath.numBeams = realNumberOfBeams;
                return swath;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * @param data
     */
    private void printPoint(BathymetryPoint[] data, int i) {
        NeptusLog.pub().info("Point in Swath: ");
        NeptusLog.pub().info("North (x): " + data[i].north);
        NeptusLog.pub().info("East (y): " + data[i].east);
        NeptusLog.pub().info("Height (z): " + data[i].depth);
    }


    /**
     * @param pose 
     */
    private void printPose(SystemPositionAndAttitude pose) {
        NeptusLog.pub().info("latitude: " + pose.getPosition().getLatitudeAsDoubleValue());
        NeptusLog.pub().info("longitude: " + pose.getPosition().getLongitudeAsDoubleValue());
        NeptusLog.pub().info("offSetNorth: " + pose.getPosition().getOffsetNorth());
        NeptusLog.pub().info("offSetEast: " + pose.getPosition().getOffsetEast());
        NeptusLog.pub().info("Yaw: " + pose.getYaw());
        //System.out.println("Roll: " + pose.getRoll());
        //System.out.println("Pitch: " + pose.getPitch());
        NeptusLog.pub().info("Depth: " + pose.getPosition().getDepth());
    }

    /**
     * @param header
     */
    private void printHeaderArgs(DeltaTHeader header) {
        NeptusLog.pub().info("timestamp: " + header.timestamp);
        NeptusLog.pub().info("startAngle: " + header.startAngle);
        NeptusLog.pub().info("numberByte: " + header.numBytes);
        NeptusLog.pub().info("angleIncrement: " + header.angleIncrement);
        NeptusLog.pub().info("range: " + header.range);
        NeptusLog.pub().info("range Resolution: " + header.rangeResolution);
        NeptusLog.pub().info("samples per beam: " + header.samplesPerBeam);
        NeptusLog.pub().info("Sector size: " + header.sectorSize);
    }
}