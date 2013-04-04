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
 * Mar 28, 2013
 */
package pt.up.fe.dceg.neptus.mra.api;

import java.io.File;

import pt.up.fe.dceg.neptus.mra.importers.IMraLogGroup;
import pt.up.fe.dceg.neptus.mra.importers.jsf.JsfSidescanParser;
import pt.up.fe.dceg.neptus.util.llf.LogUtils;

/**
 * @author jqcorreia
 *
 */
public class SidescanParserFactory {
    static File dir;
    static File file;
    static IMraLogGroup source;
    
    public static SidescanParser build(IMraLogGroup log) {
        file = null;
        dir = log.getFile("Data.lsf").getParentFile();
        source = log;
        
        return getParser();
    }
    
    public static SidescanParser build(File fileOrDir) {
        source = null;
        if(fileOrDir.isDirectory())
            dir = file;
        else {
            file = fileOrDir;
        }
        return getParser();
    }
    
    private static SidescanParser getParser() {
        if(file != null) {
            return null; //FIXME for now only directories are supported 
        }
        else if(dir != null) {
            
            file = new File(dir.getAbsolutePath()+"/Data.jsf");
            if(file.exists()) {
                return new JsfSidescanParser(file);
            }
            
            // Next cases should be file = new File(...) and check for existence
            // TODO

            // Defaults to using IMC (in case of sidescan data existence)
            if(source != null) {
                if(LogUtils.hasIMCSidescan(source))
                    return new ImcSidescanParser(source);
            }
        }
        return null;
    }
}