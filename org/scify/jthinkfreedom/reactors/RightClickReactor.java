/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.reactors;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Requires xautomation package (Ubuntu, Debian) or xte program.
 *
 * @author ggianna
 */
public class RightClickReactor extends ReactorAdapter  {

    @Override
    public void react() {
            /* NATIVE WITH LINUX XAUTOMATION
            try {
                String[] sCmd = new String[]{"/usr/bin/xte", "mouseclick 3"};
                //String[] sCmd = new String[]{"/usr/bin/xte", "'key A'"};

                //URL url = RightClickReactor.class.getResource("../../../../eventScripts/rightClick.sh");
                //String sCmd = url.getPath();
                Process p = Runtime.getRuntime().exec(sCmd);

                try {
                    p.waitFor();
                    System.err.println("Right Click! " + p.exitValue());
                } catch (InterruptedException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(
                        Level.SEVERE, "Please install xte program.", ex);
            }
            * NATIVE WITH LINUX XAUTOMATION*/
            
            //Native with awt.Robot class (to be recommended, much simpler)
        
        try {
            Robot rClickBot = new Robot();
            System.err.println("Right Click!");
            // Press right click
            rClickBot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
            // Release right click
            rClickBot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
        } catch (AWTException ex) {
            Logger.getLogger(RightClickReactor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
