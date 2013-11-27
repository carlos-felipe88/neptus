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
package pt.up.fe.dceg.neptus.plugins.vtk.utils;

import java.io.File;

/**
 * @author hfq
 *
 */
public class File3DUtils {
//    public final static String xyzStr = "xyz";
//    public final static String wrlStr = "wrl";
//    public final static String stlStr = "stl";
//    public final static String plyStr = "ply";
//    public final static String objStr = "obj";
//    public final static String treedsStr = "3ds";
//    public final static String x3dStr = "x3d";
//    public final static String vtkStr = "vtk";
    
    public final static String[] TYPES_3D_FILES = {
      "xyz", "wrl", "stl", "ply", "obj", "vtk", "3ds", "x3d"
    };
    
    /**
     * missing 3DS exporter
     * @author hfq
     *
     */
    public enum FileType {
        XYZ, WRL, STL, PLY, OBJ, ThreeDS, X3D, VTK
    }
    
    /**
     * Returns the string extension from filename
     * @param f
     * @return ext 
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        
        if (i > 0 && i < s.length() -1) {
            ext = s.substring(i+1).toLowerCase();
        }
        
        return ext;
    }
    
    /**
     * 
     * @param ext
     * @return
     */
    public static FileType getFileType(String ext) {
        
        FileType type = null;
        int index = 0;
        for (String f : TYPES_3D_FILES) {
            if(f.equals(ext)) {
                break;
            }
            ++index;
        }
        switch(index) {
            case 0:
                type = FileType.XYZ;
                break;
            case 1:
                type = FileType.WRL;
                break;
            case 2:
                type = FileType.STL;
                break;
            case 3:
                type = FileType.PLY;
                break;
            case 4:
                type = FileType.OBJ;
                break;
            case 5:
                type = FileType.ThreeDS;
                break;
            case 6:
                type = FileType.X3D;
                break;
            case 7:
                type = FileType.VTK;
                break;
        }
        return type;
    }
}


