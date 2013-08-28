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
 * Aug 28, 2013
 */
package pt.up.fe.dceg.neptus.plugins.vtk.multibeampluginutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pt.up.fe.dceg.neptus.gui.PropertiesEditor;

/**
 * @author hfq
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)        
public @interface MultibeamPluginProperty {
    /**
     * When make jars distinguishes properties that clients don't hava access
     * @author hfq
     */
    public enum DistributionEnum {
        DEVELOPER,
        CLIENT;
    }
    
    /**
     * Configs level o visibility
     * - Regular: clients can edit
     * - Advanced: only developers can edit
     * @author hfq
     */
    public enum LEVEL {
        REGULAR(1),
        ADVANCED(3);
        
        private int level;
        
        /**
         * @param level
         */
        private LEVEL(int level) {
            this.level = level;
        }
        
        /**
         * @return the level
         */
        public int getLevel() {
            return level;
        }
    }
    
    /**
     * @author hfq
     * FIXME - Why need this?
     *
     */
    public enum VALIDATOR {
        POSITIVE_INT,
        DEVIMAL,
        POSITIVE_DECIMAL,
        STRING,
        CUSTOM_VALIDATOR;
    }
    
    /**
     * The name of the property
     * @return The name of the field as presented in the GUI
     * If not set, the name of the variable will be used
     */
    String name() default "";
    
    /**
     * The description will provide further info on the field
     * @return The description of the property. If not set the <b>name</b> parameter will be used.
     */
    String description() default "";
    
    /**
     * Used to group various properties that are somehow related
     * @return The category for this config property
     */
    String category() default "General";
    
    
    /**
     * wether this property is to be hidden in user-input dialogs (changed only by code)
     * @return
     */
    boolean editable() default false;
    
    /**
     * Visibility of setting:
     * - Regular: clients can edit
     * - Advanced: only developers can edit
     * @return
     */
    LEVEL userLevel() default LEVEL.ADVANCED;
    
    DistributionEnum distribution() default DistributionEnum.CLIENT;
    
    
    /**
     * The (full) class name of the editor to be used for this property
     * @return
     */
    Class<? extends PropertiesEditor> editorClass() default PropertiesEditor.class;
    
}
