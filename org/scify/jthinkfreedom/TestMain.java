package org.scify.jthinkfreedom;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scify.jthinkfreedom.reactors.RightClickReactor;

/**
 *
 * @author nikos
 */
public class TestMain {

    public static void main(String[] args) {
        try {
            URL url = TestMain.class.getResource("../../../eventScripts/rightClick.sh");

            System.out.println();
            String sCmd = url.getPath();
            Process p = Runtime.getRuntime().exec(sCmd);

            try {
                p.waitFor();
                System.err.println("Right Click!" + p.exitValue());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
