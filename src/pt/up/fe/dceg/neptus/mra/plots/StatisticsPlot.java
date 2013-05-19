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
 * May 10, 2013
 */
package pt.up.fe.dceg.neptus.mra.plots;

import java.util.LinkedHashMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.swing.MigLayout;
import pt.up.fe.dceg.neptus.mra.MRAPanel;
import pt.up.fe.dceg.neptus.mra.importers.IMraLogGroup;
import pt.up.fe.dceg.neptus.mra.visualizations.MRAVisualization;
import pt.up.fe.dceg.neptus.util.llf.LogUtils;

/**
 * @author jqcorreia
 *
 */
public class StatisticsPlot extends JPanel implements MRAVisualization {
    
    JTable table;
    TableModel model = new TableModel();
        
    public StatisticsPlot(MRAPanel panel) {
        setLayout(new MigLayout());
    }
    
    @Override
    public String getName() {
        return "Statistics";
    }

    @Override
    public JComponent getComponent(IMraLogGroup source, double timestep) {
        table = new JTable(model);
        
        table.setTableHeader(null);
        
        model.map = LogUtils.generateStatistics(source);
        add(new JScrollPane(table), "w 100%, h 100%");
        
        return this;
    }

    @Override
    public boolean canBeApplied(IMraLogGroup source) {
        return true;
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public Double getDefaultTimeStep() {
        return null;
    }

    @Override
    public boolean supportsVariableTimeSteps() {
        return false;
    }

    @Override
    public Type getType() {
        return Type.VISUALIZATION;
    }

    @Override
    public void onHide() {
        
    }

    @Override
    public void onShow() {
        
    }

    @Override
    public void onCleanup() {
        
    }
    
    class TableModel extends AbstractTableModel {
        public LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        
//        public LinkedHashMap<String, String> getMap() {
//            return map;
//        }
//        
//        public void setMap(LinkedHashMap<String, String> map) {
//            this.map = map;
//        }
//        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object key = map.keySet().toArray()[rowIndex];
            if(columnIndex == 0)
                return key;
            else 
                return map.get(key);
        }
        
        @Override
        public int getRowCount() {
            return map.size();
        }
        
        @Override
        public int getColumnCount() {
            return 2;
        }
    };
}