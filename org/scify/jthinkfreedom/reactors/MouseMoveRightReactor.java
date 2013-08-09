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
public class MouseMoveRightReactor extends ReactorAdapter {
    
    private static final int MOUSE_SPEED = 20;
    
    @Override
    public void react() {
        try {
            Robot mouseUp = new Robot();
            // Get current mouse coordinates
            double curX = MouseInfo.getPointerInfo().getLocation().getX();
            double curY = MouseInfo.getPointerInfo().getLocation().getY();
            // Get target mouse coordinates
            double targetX = MouseInfo.getPointerInfo().getLocation().getX() + MOUSE_SPEED;

            System.err.println("Mouse Moved Right!");
            // Move mouse up
            for (int i = 0; i < 100; i++) {
                int mov_x = (((int) targetX * i) / 100) + ((int) curX * (100 - i) / 100);
                int mov_y = (((int) curY * i) / 100) + ((int) curY * (100 - i) / 100);
                mouseUp.mouseMove(mov_x, mov_y);
                mouseUp.delay(10);
            }
        } catch (AWTException ex) {
            Logger.getLogger(RightClickReactor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
