/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.reactors;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseMoveDownReactor extends ReactorAdapter {
    
    private static final int MOUSE_SPEED = 5;
    
    @Override
    public void react() {
        try {
            Robot mouseUp = new Robot();
            // Get current mouse coordinates
            double curX = MouseInfo.getPointerInfo().getLocation().getX();
            double curY = MouseInfo.getPointerInfo().getLocation().getY() + MOUSE_SPEED;
            System.err.println("Mouse Moved Down!");
            // Move mouse up
            mouseUp.mouseMove((int) curX, (int) curY);
        } catch (AWTException ex) {
            Logger.getLogger(RightClickReactor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
