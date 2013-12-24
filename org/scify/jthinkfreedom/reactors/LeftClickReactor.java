package org.scify.jthinkfreedom.reactors;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ggianna
 */
public class LeftClickReactor  extends ReactorAdapter {

    @Override
    public void react() {
        try {
            Robot rClickBot = new Robot();

            // Press right click
            rClickBot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            // Release right click
            rClickBot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            // Message
            System.err.println("Left Click");
        } catch (AWTException ex) {
            Logger.getLogger(RightClickReactor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
