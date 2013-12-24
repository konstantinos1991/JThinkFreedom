package org.scify.jthinkfreedom.reactors;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseMoveDownReactor extends ReactorAdapter {
    
    private static final int MOUSE_SPEED = 20;
    
    @Override
    public void react() {
        try {
            Robot mouseDown = new Robot();
            // Get current mouse coordinates
            double curX = MouseInfo.getPointerInfo().getLocation().getX();
            // Get target mouse coordinates
            double targetY = MouseInfo.getPointerInfo().getLocation().getY() + MOUSE_SPEED;
            
            
            // Move mouse up
            mouseDown.mouseMove((int) curX, (int) targetY);
        } catch (AWTException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
}
