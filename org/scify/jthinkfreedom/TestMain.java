package org.scify.jthinkfreedom;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            p.waitFor();
            System.err.println("Right Click!" + p.exitValue());

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
