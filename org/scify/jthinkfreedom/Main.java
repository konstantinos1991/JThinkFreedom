package org.scify.jthinkfreedom;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.scify.jthinkfreedom.reactors.LeftClickReactor;
import org.scify.jthinkfreedom.reactors.RightClickReactor;
import org.scify.jthinkfreedom.sensors.WebcamSensor;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.sensors.NetworkImageSensor;
import org.scify.jthinkfreedom.stimuli.LeftEyeBlinkStimulus;
import org.scify.jthinkfreedom.stimuli.RightEyeBlinkStimulus;

/**
 *
 * @author ggianna
 */
public class Main {

    public static void main(String[] saArgs) {
        ISensor<opencv_core.IplImage> sSensor;
        if (saArgs.length > 0) {
            try {
                System.err.println("Trying local capture..." + saArgs[0]);
                sSensor = new WebcamSensor(Integer.parseInt(saArgs[0]));
            } catch (NumberFormatException e) {
                System.err.println("Trying for network capture..." + saArgs[0]);
                sSensor = new NetworkImageSensor(saArgs[0]);
            }
        } else {
            sSensor = new WebcamSensor(0);
            System.err.println("Webcam default (0).");
        }
        System.err.println("Camera:" + sSensor.toString());

        try {
            sSensor.start();
        } catch (Exception ex) {
            Logger.getLogger(WebcamSensor.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Connect sensor to reactors and stimuli
        LeftEyeBlinkStimulus lbStimulus = new LeftEyeBlinkStimulus();
        sSensor.addStimulus(lbStimulus);
        lbStimulus.addSensor(sSensor);
        lbStimulus.addReactor(new LeftClickReactor());

        RightEyeBlinkStimulus rbStimulus = new RightEyeBlinkStimulus();
        sSensor.addStimulus(rbStimulus);
        rbStimulus.addSensor(sSensor);
        rbStimulus.addReactor(new RightClickReactor());

        // FOR SOCKET COMMUNICATION
        //TCPReactorClient rReactor = new TCPReactorClient();
        //rReactor.add(new Pair("83.212.112.152", 4444));
        //rReactor.add(new Pair("192.168.1.65", 4444));
        //hmStimulus.addReactor(rReactor);
        ///////////////////////////
        // Canvas
        final CanvasFrame win = new CanvasFrame("Source");// Canvas
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        opencv_core.IplImage iToRender;

        Date dStart = new Date();

        while (true) {
            // Canvas
            iToRender = sSensor.getData();
            if (iToRender != null) {
                win.showImage(iToRender);
            }

            Thread.yield();
            // Break after 90 seconds
            if (new Date().getTime() - dStart.getTime() > 90000) {
                break;
            }
        }

        sSensor.stop();
        // Finalize
        win.dispose();

    }
}
