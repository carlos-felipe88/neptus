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
 * Author: jqcorreia
 * Apr 2, 2013
 */
package pt.up.fe.dceg.neptus.mra.importers.deltat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import pt.up.fe.dceg.neptus.imc.IMCMessage;
import pt.up.fe.dceg.neptus.mp.SystemPositionAndAttitude;
import pt.up.fe.dceg.neptus.mra.api.BathymetryInfo;
import pt.up.fe.dceg.neptus.mra.api.BathymetryParser;
import pt.up.fe.dceg.neptus.mra.api.BathymetryPoint;
import pt.up.fe.dceg.neptus.mra.api.BathymetrySwath;
import pt.up.fe.dceg.neptus.mra.importers.IMraLog;
import pt.up.fe.dceg.neptus.mra.importers.IMraLogGroup;
import pt.up.fe.dceg.neptus.util.llf.LsfLogSource;

/**
 * @author jqcorreia
 *
 */
public class DeltaTParser implements BathymetryParser {

    File file;
    IMraLogGroup source;
    FileInputStream fis;
    FileChannel channel;
    ByteBuffer buf;
    long curPos = 0;
    
    IMCMessage state;
    IMraLog stateParser;

    BathymetryInfo info;
    
    public DeltaTParser(IMraLogGroup source) {
        this.source = source;
        file = source.getFile("multibeam.83P");
        try {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        channel = fis.getChannel();
        stateParser = source.getLog("EstimatedState");
        
        initialize();
    }
    
    /**
     * Used to gather bathymetry info and generate BathymetryInfo object
     */
    private void initialize() {
        File f = new File(source.getFile("Data.lsf").getParent() + "/mra/bathy.info");
        File folder = new File(source.getFile("Data.lsf").getParent() + "/mra/");
        
        if(!folder.exists())
            folder.mkdirs();
        
        if(!f.exists()) {
            info = new BathymetryInfo();
            
            double maxLat = -Math.PI;
            double minLat = Math.PI;
            double maxLon = -Math.PI * 2;
            double minLon = Math.PI * 2;
            
            BathymetrySwath bs;

            while ((bs = nextSwath()) != null) {
                double lat = bs.getPose().getPosition().getLatitudeAsDoubleValueRads();
                double lon = bs.getPose().getPosition().getLongitudeAsDoubleValueRads();

                maxLat = Math.max(lat, maxLat);
                maxLon = Math.max(lon, maxLon);
                minLat = Math.min(lat, minLat);
                minLon = Math.min(lon, minLon);
                for(int c = 0; c < bs.numBeams; c++) {
                    BathymetryPoint p = bs.getData()[c];
                    
                    info.minDepth = Math.min(info.minDepth, p.depth);
                    info.maxDepth = Math.max(info.maxDepth, p.depth);
                }
            }
            
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
                out.writeObject(info);
                out.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            curPos = 0;
        }
        else {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
                info = (BathymetryInfo) in.readObject();
                in.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println(info.maxDepth);
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
        return info;
    }

    @Override
    public BathymetrySwath getSwathAt(long timestamp) {
        return null;
    }

    @Override
    public BathymetrySwath nextSwath() {
        try {
            if(curPos >= channel.size())
                return null;
            
            BathymetryPoint data[];
            
            buf = channel.map(MapMode.READ_ONLY, curPos, 256);
            DeltaTHeader header = new DeltaTHeader();
            header.parse(buf);
            
            // Parse and process data ( no need to create another structure for this )
            buf = channel.map(MapMode.READ_ONLY, curPos + 256, header.numBeams * 2);
            data = new BathymetryPoint[header.numBeams];
            state = stateParser.getEntryAtOrAfter(header.timestamp);
            
            // Use the navigation data from EstimatedState 
            SystemPositionAndAttitude pose = new SystemPositionAndAttitude();
            pose.getPosition().setLatitudeRads(state.getDouble("lat"));
            pose.getPosition().setLongitudeRads(state.getDouble("lon"));
            pose.getPosition().setOffsetNorth(state.getDouble("x"));
            pose.getPosition().setOffsetEast(state.getDouble("y"));
            pose.getPosition().setDepth(state.getDouble("depth"));
            pose.setYaw(state.getDouble("psi"));
            
            for(int c = 0; c < header.numBeams; c++) { 
                double range = buf.getShort(c*2) * (header.rangeResolution / 1000.0);
                double angle = header.startAngle + header.angleIncrement * c;
                
                float height = (float) (range * Math.cos(Math.toRadians(angle)) + pose.getPosition().getDepth());

                double x = range * Math.sin(Math.toRadians(angle));
                double theta = -pose.getYaw();
                float ox = (float) (x * Math.cos(Math.toRadians(theta)));
                float oy = (float) (x * Math.sin(Math.toRadians(theta)));
                
                data[c] = new BathymetryPoint((float)pose.getPosition().getOffsetNorth() + ox, (float)pose.getPosition().getOffsetEast() + oy, height);
            }
            curPos += header.numBytes; // Advance current position
            
            BathymetrySwath swath = new BathymetrySwath(header.timestamp,  new SystemPositionAndAttitude(), data);
            swath.numBeams = header.numBeams;
            
            return swath;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            LsfLogSource source = new LsfLogSource(new File("/home/jqcorreia/lsts/logs/lauv-noptilus-1/20130208/124645_bathym_plan/Data.lsf"), null);
            DeltaTParser p = new DeltaTParser(source);
            BathymetrySwath bs;
            
//            Kryo kryo = new Kryo();
//            Output output = new Output(new FileOutputStream("kryo.bin"));
            int c = 0;
            while((bs = p.nextSwath()) != null) {
//                for(BathymetryPoint bp : bs.getData()) {
//                    double r[] = CoordinateUtil.latLonAddNE2(bp.lat, bp.lon, bp.north, bp.east);
//                    float f[] = new float[2];
//                    
//                    f[0] = (float) (r[0] * 1000000f);
//                    f[1] = new Double(r[1]).floatValue();
//                    
//                    System.out.println(r[0]);
//                    System.out.println(" " + f[0]);
//                }
                c++;
//                kryo.writeObject(output, bs);
            }
            System.out.println(c);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}