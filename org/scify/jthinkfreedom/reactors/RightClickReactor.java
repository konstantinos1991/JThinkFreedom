/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.reactors;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Requires xautomation package (Ubuntu, Debian) or xte program.
 *
 * @author ggianna
 */
public class RightClickReactor extends ReactorAdapter {

    @Override
    public void react() {
        try {
//            String[] sCmd = new String[]{"/usr/bin/xte", "'mouseclick 3'"};
//            String[] sCmd = new String[]{"/usr/bin/xte", "'key A'"};
            
            URL url = RightClickReactor.class.getResource("../../../../eventScripts/rightClick.sh");
            String sCmd = url.getPath();
            Process p = Runtime.getRuntime().exec(sCmd);
            
            try {
                p.waitFor();
                System.err.println("Right Click!" + p.exitValue());
            } catch (InterruptedException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(
                    Level.SEVERE, "Please install xte program.", ex);
        }
    }


}
