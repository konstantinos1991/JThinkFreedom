package org.scify.jthinkfreedom.reactors;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseMoveLeftReactor extends ReactorAdapter {
    
    private static final int MOUSE_SPEED = 20;
    
    @Override
    public void react() {
        try {
            Robot mouseLeft = new Robot();
            // Get current mouse coordinates
            double curY = MouseInfo.getPointerInfo().getLocation().getY();
            // Get target mouse coordinates
            double targetX = MouseInfo.getPointerInfo().getLocation().getX() - MOUSE_SPEED;

            // Move mouse up
            mouseLeft.mouseMove((int) targetX, (int) curY);
        } catch (AWTException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
}
