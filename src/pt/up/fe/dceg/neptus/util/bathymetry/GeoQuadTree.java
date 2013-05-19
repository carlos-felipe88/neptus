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
 * May 15, 2013
 */
package pt.up.fe.dceg.neptus.util.bathymetry;

import java.io.File;
import java.util.ArrayList;

import pt.up.fe.dceg.neptus.NeptusLog;
import pt.up.fe.dceg.neptus.mra.api.BathymetrySwath;
import pt.up.fe.dceg.neptus.mra.importers.deltat.DeltaTParser;
import pt.up.fe.dceg.neptus.types.coord.LocationType;
import pt.up.fe.dceg.neptus.util.llf.LsfLogSource;

/**
 * @author jqcorreia
 *
 */
public class GeoQuadTree<O> {
    
    static int num = 0;
    
    GeoQuadTree<O> northWest;
    GeoQuadTree<O> northEast;
    GeoQuadTree<O> southWest;
    GeoQuadTree<O> southEast;

    ArrayList<O> list;
    
    int capacity;

    double minLat, minLon, maxLat, maxLon;
    
    public GeoQuadTree(int capacity, double minLat, double minLon, double maxLat, double maxLon) {
        this.capacity = capacity;
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
        
        list = new ArrayList<O>(capacity);
        
        System.out.println(minLat + " " + minLon + " " + maxLat + " " + maxLon);
        num++;
    }

    public boolean insert(O obj) {
        if(!contains(obj)) {
            return false;
        }
        
        if(list.size() < capacity) {
            list.add(obj);
            return true;
        }
        
        if(northWest == null) {
            subdivide();
        }
        
        if(northWest.insert(obj)) return true;
        if(northEast.insert(obj)) return true;
        if(southWest.insert(obj)) return true;
        if(southEast.insert(obj)) return true;
        
        System.out.println("error");
        return false;
    }
    
    public void subdivide() {
        double hdeltaLat = (maxLat - minLat) / 2.0;
        double hdeltaLon = (maxLon - minLon) / 2.0;
        
        northWest = new GeoQuadTree<>(capacity, minLat + hdeltaLat, minLon, maxLat, minLon + hdeltaLon);
        northEast = new GeoQuadTree<>(capacity, minLat + hdeltaLat, minLon + hdeltaLon, maxLat, maxLon);
        southWest = new GeoQuadTree<>(capacity, minLat, minLon, minLat + hdeltaLat, minLon + hdeltaLon);
        southEast = new GeoQuadTree<>(capacity, minLat, minLon + hdeltaLon, minLat + hdeltaLat, maxLon);
        System.out.println();
    }
    
    boolean contains(O obj) {
        BathymetrySwath s = (BathymetrySwath) obj;
        
        LocationType loc = s.getPose().getPosition();
        
        if(loc.getLatitudeAsDoubleValue() < maxLat && loc.getLatitudeAsDoubleValue() > minLat && loc.getLongitudeAsDoubleValue() < maxLon && loc.getLongitudeAsDoubleValue() > minLon) {
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) {
        GeoQuadTree<Object> tree = new GeoQuadTree<>(5000, -90, -180, 90, 180);
        
        try {
            LsfLogSource source = new LsfLogSource(new File("/home/jqcorreia/lsts/logs/lauv-noptilus-1/20130208/124645_bathym_plan/Data.lsf"), null);
            DeltaTParser p = new DeltaTParser(source);
            int c = 0;
            BathymetrySwath s;
            while((s = p.nextSwath()) != null) {
                c++;
                tree.insert(s);
            }
            
            NeptusLog.pub().info("<###> "+c);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(num);
    }
}