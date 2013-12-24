package org.scify.jthinkfreedom.reactors;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseMoveUpReactor extends ReactorAdapter {

    private static final int MOUSE_SPEED = 20;

    @Override
    public void react() {
        try {
            Robot mouseUp = new Robot();
            // Get current mouse coordinates
            double curX = MouseInfo.getPointerInfo().getLocation().getX();
            // Get target mouse coordinates
            double targetY = MouseInfo.getPointerInfo().getLocation().getY() - MOUSE_SPEED;

            // Move mouse up
            mouseUp.mouseMove((int) curX, (int) targetY);
        } catch (AWTException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
