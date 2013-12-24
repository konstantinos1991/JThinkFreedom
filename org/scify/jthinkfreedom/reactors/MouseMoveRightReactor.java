package org.scify.jthinkfreedom.reactors;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseMoveRightReactor extends ReactorAdapter {
    
    private static final int MOUSE_SPEED = 20;
    
    @Override
    public void react() {
        try {
            Robot mouseRight = new Robot();
            // Get current mouse coordinates
            double curY = MouseInfo.getPointerInfo().getLocation().getY();
            // Get target mouse coordinates
            double targetX = MouseInfo.getPointerInfo().getLocation().getX() + MOUSE_SPEED;

            // Move mouse up
            mouseRight.mouseMove((int) targetX, (int) curY);
        } catch (AWTException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
}
