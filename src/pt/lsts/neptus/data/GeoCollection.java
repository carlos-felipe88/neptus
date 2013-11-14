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
 * Author: zp
 * Nov 14, 2013
 */
package pt.lsts.neptus.data;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Vector;

import pt.lsts.neptus.types.coord.LocationType;

/**
 * This class is used to store a set of samples spread out in a 
 * geographic area and allows several ways of accessing the data
 * @author zp
 *
 */
public class GeoCollection<T> {

    private KDTree data = new KDTree(2);
    private Interpolator<T> interpolator = null;
    private LocationType center = null;
    private boolean roundCoordinates = true;
    
    /**
     * Creates a collection of generic samples. 
     * Interpolation is done by returning the value of nearest sample.
     * @author zp
     *
     * @param <X> The type of samples to store
     */
    public static class Generic<X> extends GeoCollection<X> {
        public Generic() {
            super(new Interpolator<X>() {
                @Override
                public X interpolate(double weightTotal, java.util.Vector<Pair<java.lang.Double, X>> weightToValues) {
                    X min = weightToValues.firstElement().second;
                    double maxWeight = weightToValues.firstElement().first;
                    
                    for (int i = 1; i < weightToValues.size(); i++)
                        if (weightToValues.get(i).first > maxWeight ) {
                            maxWeight = weightToValues.get(i).first;
                            min = weightToValues.get(i).second;
                        }                
                    return min;
                }
            });
        }
    }
    
    /**
     * This class stores numeric samples and uses Inverse distance weighting for interpolation
     * @author zp
     */
    public static class Double extends GeoCollection<java.lang.Double> {
        public Double() {
            super(new Interpolator<java.lang.Double>() {
                @Override
                public java.lang.Double interpolate(double weightTotal,
                        java.util.Vector<Pair<java.lang.Double, java.lang.Double>> weightToValues) {
                    double val = 0;
                    for (Pair<java.lang.Double, java.lang.Double> p : weightToValues) {
                        val += p.first * p.second;
                    }
                    return val/weightTotal;
                }
            });
        }
    }
    
    //FIXME
    public static class Vectorial extends GeoCollection<Vector<java.lang.Double>>{
        public Vectorial() {
            super (new Interpolator<Vector<java.lang.Double>>() {
                @Override
                public java.util.Vector<java.lang.Double> interpolate(double weightTotal,
                        java.util.Vector<Pair<java.lang.Double, java.util.Vector<java.lang.Double>>> weightToValues) {
                    // TODO Auto-generated method stub
                    return null;
                }
            });
        }        
    }
   
    /**
     * Add a new sample to this collection
     * @param point The location of the sample
     * @param value The value of the sample
     */
    public void add(LocationType point, T value) {
        
        if (center == null)
            center = new LocationType(point);
        double[] coord = point.getOffsetFrom(center);
        
        if (roundCoordinates) {
            coord[0] = Math.round(coord[0]);
            coord[1] = Math.round(coord[1]);
        }
        data.insert(coord, value);
    }
    
    /**
     * Remove an existing sample
     * @param point The location of the sample to be removed
     */
    public void remove(LocationType point) {
        double[] coord = point.getOffsetFrom(center);

        if (roundCoordinates) {
            coord[0] = Math.round(coord[0]);
            coord[1] = Math.round(coord[1]);
        }
        data.delete(coord);        
    }

    /**
     * Class constructor
     * @param interp An interpolator which is used to interpolate samples
     */
    public GeoCollection(Interpolator<T> interp) {
        this.interpolator = interp;
    }

    /**
     * Return N nearest samples of given location
     * @param numberOfPoints The maximum number of samples to return
     * @param loc The location where to search from
     * @return N samples which are closest to given location
     */
    @SuppressWarnings("unchecked")
    public Vector<T> nearestN(int numberOfPoints, LocationType loc) {
        double[] coord = loc.getOffsetFrom(center);
        
        if (roundCoordinates) {
            coord[0] = Math.round(coord[0]);
            coord[1] = Math.round(coord[1]);
        }
        Object[] objs = data.nearest(coord, Math.min(numberOfPoints, data.m_count));
        Vector<T> ret = new Vector<>();
        for (Object o : objs) {
            ret.add((T)o);
        }
        return ret;
    }

    /**
     * Return the nearest sample
     * @param loc A location
     * @return The sample which is closer to the given location
     */
    public T nearest(LocationType loc) {
        return nearestN(1, loc).firstElement();
    }
    
    /**
     * Query the value for a given location
     * @param loc A location
     * @return If there is a sample at the given location, 
     * return its value, otherwise interpolate existing samples to return a predicted value.
     */
    public T valueAt(LocationType loc) {
        if (interpolator == null)
            return nearest(loc);
        else {
            LinkedHashMap<LocationType, T> points = nearestNPoints(Math.min(10, data.m_count), loc);
            
            double distSum = 0;
            
            Vector<Pair<java.lang.Double, T>> values = new Vector<>();
            
            for (Entry<LocationType, T> p : points.entrySet()) {
                double dist = p.getKey().getDistanceInMeters(loc);
                dist = Math.pow(dist, 2);
                if (dist > 0)
                    dist = 1 / dist;
                else
                    dist =  1.e20;
                //System.out.println(dist);
                distSum += dist;
                //System.out.println(distSum);
                values.add(new Pair<java.lang.Double, T>(dist, p.getValue()));                
            }
            
            return interpolator.interpolate(distSum, values);
        }            
    }
    
    /**
     * Query all samples within a given distance
     * @param loc A location
     * @param distance A distance, in meters
     * @param maxPoints Maximum number of samples to return
     * @return Samples lying in the circle centered in <code>loc</code> and radius of <code>distance</code>
     */
    @SuppressWarnings("unchecked")
    public Vector<T> within(LocationType loc, double distance, int maxPoints) {
        double[] pivot = loc.getOffsetFrom(center);
        double[] coord = loc.getOffsetFrom(center);
        
        if (roundCoordinates) {
            coord[0] = Math.round(coord[0]);
            coord[1] = Math.round(coord[1]);
        }
        Vector<Pair<double[], Object>> points = data.nearestNPoints(coord, maxPoints);
        Vector<T> ret = new Vector<>();
        
        for (Pair<double[], Object> p : points) {
            double dist = Point2D.distance(pivot[0], pivot[1], p.first[0], p.first[1]);
            if (dist < distance)
                ret.add((T)p.second);
        }
        return ret;
    }
    
    /**
     * Query samples inside a bounded rectangular area
     * @param sw The south west corner of the rectangle
     * @param ne The north east corner of the rectangle
     * @param maxPoints Maximum number of samples to return
     * @return Samples, together with their locations, that exist inside given area
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<LocationType, T> insidePoints(LocationType sw, LocationType ne, int maxPoints) {
        double[] low = sw.getOffsetFrom(center);
        double[] high = ne.getOffsetFrom(center);
        Vector<Pair<double[], Object>> points = data.rangePoints(low, high);
        LinkedHashMap<LocationType, T> ret = new LinkedHashMap<>();
        for (Pair<double[], Object> p : points) {
            LocationType loc = new LocationType(center);
            loc.translatePosition(p.first);
            loc.convertToAbsoluteLatLonDepth();
            ret.put(loc, (T)p.second);
        }
        return ret;
    }
    
    /**
     * Query samples inside a rectangular area
     * @param sw The south west corner of the rectangle
     * @param ne The north east corner of the rectangle
     * @param maxPoints Maximum number of samples to return
     * @return Samples that exist inside given area
     */
    public Vector<T> inside(LocationType sw, LocationType ne, int maxPoints) {
        Vector<T> vec = new Vector<>();
        vec.addAll(insidePoints(sw, ne, maxPoints).values());
        return vec;        
    }
    
    /**
     * Query all samples within a given distance
     * @param loc A location
     * @param distance A distance, in meters
     * @param maxPoints Maximum number of samples to return
     * @return Samples lying in the circle centered in <code>loc</code> and radius of <code>distance</code>, together with their locations
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<LocationType, T> pointsWithin(LocationType loc, double distance, int maxPoints) {
        double[] pivot = loc.getOffsetFrom(center);
        double[] coord = loc.getOffsetFrom(center);
        
        if (roundCoordinates) {
            coord[0] = Math.round(coord[0]);
            coord[1] = Math.round(coord[1]);
        }
        Vector<Pair<double[], Object>> points = data.nearestNPoints(coord, maxPoints);
        LinkedHashMap<LocationType, T> ret = new LinkedHashMap<>();
        for (Pair<double[], Object> p : points) {
            double dist = Point2D.distance(pivot[0], pivot[1], p.first[0], p.first[1]);
            if (dist < distance) {
                LocationType l = new LocationType(center);
                l.translatePosition(p.first);
                l.convertToAbsoluteLatLonDepth();
                ret.put(l, (T)p.second);
            }
        }        
        return ret;
    }
    
    /**
     * Return N nearest samples of given location
     * @param numberOfPoints The maximum number of samples to return
     * @param loc The location where to search from
     * @return N samples which are closest to given location, together with their actual locations
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<LocationType, T> nearestNPoints(int numberOfPoints, LocationType loc) {
        
        Vector<Pair<double[], Object>> res = data.nearestNPoints(loc.getAbsoluteLatLonDepth(), numberOfPoints);
        LinkedHashMap<LocationType, T> ret = new LinkedHashMap<>();
        for (Pair<double[], Object> pair : res) {
            LocationType l = new LocationType(center);
            l.translatePosition(pair.first);
            ret.put(l, (T)pair.second);
        }        
        return ret;
    }

    public static void main(String[] args) {
        LocationType loc1 = new LocationType(41, -8.23);
        LocationType loc2 = new LocationType(41.04, -8);
        LocationType loc3 = new LocationType(41.07, -8.01);

        GeoCollection<java.lang.Double> states = new GeoCollection.Generic<java.lang.Double>();
        states.add(loc1, 10d);
        states.add(loc2, 121d);
        states.add(loc3, 34d);

        System.out.println(states.nearestN(2, loc2));
        System.out.println(states.valueAt(new LocationType(41, -8.1)));
    }
}