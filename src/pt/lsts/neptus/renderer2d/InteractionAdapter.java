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
 * Version 1.1 only (the "Licence"), appearing in the file LICENCE.md
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
 * Author: José Pinto
 * May 11, 2010
 */
package pt.lsts.neptus.renderer2d;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.gui.ToolbarSwitch;
import pt.lsts.neptus.plugins.PluginUtils;
import pt.lsts.neptus.plugins.SimpleSubPanel;
import pt.lsts.neptus.util.AngleCalc;
import pt.lsts.neptus.util.ImageUtils;

/**
 * @author zp
 *
 */
public class InteractionAdapter extends SimpleSubPanel implements StateRendererInteraction {


    private static final long serialVersionUID = 1L;
    protected int lastClickedButton;
	protected Point2D lastDragPoint = null;	
	
	protected double deltaX = 0, deltaY = 0;
	protected static Cursor cursor;
	protected static Image image;
	protected boolean active = false;
	protected StateRenderer2D source = null;	
	protected ToolbarSwitch associatedSwitch = null;
	
	static {
		cursor = Toolkit.getDefaultToolkit().createCustomCursor(ImageUtils.getImage("images/cursors/crosshair_cursor.png"), new Point(6,6), "Zoom");
		image = ImageUtils.getImage("images/buttons/alarm.png");
	}
	
    /**
     * @param console
     */
    public InteractionAdapter(ConsoleLayout console) {
        super(console);
    }

	@Override
	public Cursor getMouseCursor() {
		return cursor;
	}
	
	@Override
	public Image getIconImage() {
		return image;
	}
	
	@Override
	public String getName() {
	    return PluginUtils.getPluginName(getClass());
	}
	
	@Override
	public boolean isExclusive() {
		return true;
	}
	
	public void keyPressed(KeyEvent event, StateRenderer2D source) {
        switch (event.getKeyCode()) {
            case (KeyEvent.VK_PLUS):
            case (KeyEvent.VK_PAGE_UP):
                source.setLevelOfDetail(source.getLevelOfDetail() + 1);
                source.repaint();
                break;

            case (KeyEvent.VK_MINUS):
            case (KeyEvent.VK_PAGE_DOWN):
                source.setLevelOfDetail(source.getLevelOfDetail() - 1);
                source.repaint();
                break;

            case (KeyEvent.VK_LEFT):
                if (!event.isControlDown()) {
//                    source.worldPixelXY.setLocation(source.worldPixelXY.getX() - source.getWidth() / 16.0,
//                            source.worldPixelXY.getY());
//                    source.setLevelOfDetail(source.getLevelOfDetail());
                    double deltaX = -source.getWidth() / 16.0, deltaY = 0;
                    if (source.getRotation() != 0) {
                        double[] offsets = AngleCalc.rotate(source.getRotation(), deltaX, deltaY, false);
                        deltaX = offsets[0];
                        deltaY = offsets[1];
                    }
                    source.worldPixelXY.setLocation(source.worldPixelXY.getX() + deltaX, source.worldPixelXY.getY() + deltaY);
                }
                else
                    source.setRotation(source.getRotation() - 0.05);
                source.repaint();
                break;

            case (KeyEvent.VK_RIGHT):
                if (!event.isControlDown()) {
//                    source.worldPixelXY.setLocation(source.worldPixelXY.getX() + source.getWidth() / 16.0,
//                            source.worldPixelXY.getY());
//                    source.setLevelOfDetail(source.getLevelOfDetail());
                    double deltaX = source.getWidth() / 16.0, deltaY = 0;
                    if (source.getRotation() != 0) {
                        double[] offsets = AngleCalc.rotate(source.getRotation(), deltaX, deltaY, false);
                        deltaX = offsets[0];
                        deltaY = offsets[1];
                    }
                    source.worldPixelXY.setLocation(source.worldPixelXY.getX() + deltaX, source.worldPixelXY.getY() + deltaY);
                }
                else
                    source.setRotation(source.getRotation() + 0.05);

                source.repaint();
                break;

            case (KeyEvent.VK_UP):
//                source.worldPixelXY.setLocation(source.worldPixelXY.getX(),
//                        source.worldPixelXY.getY() - source.getWidth() / 16.0);
//                source.setLevelOfDetail(source.getLevelOfDetail());
                double deltaXU = 0, deltaYU = -source.getHeight() / 16.0;
                if (source.getRotation() != 0) {
                    double[] offsets = AngleCalc.rotate(source.getRotation(), deltaXU, deltaYU, false);
                    deltaXU = offsets[0];
                    deltaYU = offsets[1];
                }
                source.worldPixelXY.setLocation(source.worldPixelXY.getX() + deltaXU, source.worldPixelXY.getY() + deltaYU);
                source.repaint();
                break;

            case (KeyEvent.VK_DOWN):
//                source.worldPixelXY.setLocation(source.worldPixelXY.getX(),
//                        source.worldPixelXY.getY() + source.getWidth() / 16.0);
//                source.setLevelOfDetail(source.getLevelOfDetail());
                double deltaXD = 0, deltaYD = source.getHeight() / 16.0;
                if (source.getRotation() != 0) {
                    double[] offsets = AngleCalc.rotate(source.getRotation(), deltaXD, deltaYD, false);
                    deltaXD = offsets[0];
                    deltaYD = offsets[1];
                }
                source.worldPixelXY.setLocation(source.worldPixelXY.getX() + deltaXD, source.worldPixelXY.getY() + deltaYD);
                source.repaint();
                break;

            case (KeyEvent.VK_N):
                source.setRotation(0);
                source.repaint();
                break;

            case (KeyEvent.VK_L):
                source.setLegendShown(!source.isLegendShown());
                source.repaint();
                break;
        }
	}
	
	public void keyReleased(java.awt.event.KeyEvent event, StateRenderer2D source) {};
	
	public void keyTyped(java.awt.event.KeyEvent event, StateRenderer2D source) {};
	
	public void mouseDragged(MouseEvent event, StateRenderer2D source) {
		
		if (lastDragPoint == null) {
			lastDragPoint = event.getPoint();
			deltaX = deltaY = 0;
			return;
		}
		else {
			deltaX = event.getPoint().getX() - lastDragPoint.getX();
			deltaY = event.getPoint().getY() - lastDragPoint.getY();	
		}

		if (source.show_mode != StateRenderer2D.VEHICLE_MOVES)
			return;
		
		double rotationRads = source.getRotation();
				
		if (rotationRads != 0) {
			
			double dist = event.getPoint().distance(lastDragPoint);
			double angle =  Math.atan2(event.getPoint().getY() - lastDragPoint.getY(), event.getPoint()
                    .getX() - lastDragPoint.getX());
			
			deltaX = dist * Math.cos(angle + rotationRads);
            deltaY = dist * Math.sin(angle + rotationRads);
		}
		
		source.worldPixelXY.setLocation(source.worldPixelXY.getX()-deltaX, source.worldPixelXY.getY()-deltaY);
		lastDragPoint = event.getPoint();
		source.repaint();
	}
	

	public void mousePressed(MouseEvent event, StateRenderer2D source) {
		lastClickedButton = event.getButton();
		lastDragPoint = event.getPoint();
	}
	
	public void mouseReleased(MouseEvent event, StateRenderer2D source) {
		lastDragPoint = null;
		if (source.getViewMode() != Renderer.NONE)
			source.repaint();
	}	
	
	long lastMouseWheelMillis = 0;
	public void wheelMoved(MouseWheelEvent arg0, StateRenderer2D source) {
	    if (arg0.getWhen() - lastMouseWheelMillis < 50)
	        return;
	    lastMouseWheelMillis = arg0.getWhen();
	    
	    source.zoomInOut(arg0.getWheelRotation() < 0, arg0.getPoint().getX(), arg0.getPoint().getY());
		source.repaint();
	}

	public void mouseClicked(MouseEvent event, StateRenderer2D source) {
		
	}

	public void mouseMoved(MouseEvent event, StateRenderer2D source) {
		
	}
	
	@Override
	public void setActive(boolean mode, StateRenderer2D source) {
	    this.active = mode;
	    this.source = source;
	}
	
	public boolean isActive() {
	    return active;
	}

    /**
     * @return the associatedSwitch
     */
    public ToolbarSwitch getAssociatedSwitch() {
        return associatedSwitch;
    }

    /**
     * @param associatedSwitch the associatedSwitch to set
     */
    public void setAssociatedSwitch(ToolbarSwitch associatedSwitch) {
        this.associatedSwitch = associatedSwitch;
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.plugins.SimpleSubPanel#initSubPanel()
     */
    @Override
    public void initSubPanel() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.plugins.SimpleSubPanel#cleanSubPanel()
     */
    @Override
    public void cleanSubPanel() {
        // TODO Auto-generated method stub
        
    }
}
