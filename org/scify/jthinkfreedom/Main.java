/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom;

import com.googlecode.javacv.cpp.opencv_core;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scify.jthinkfreedom.reactors.LeftClickReactor;
import org.scify.jthinkfreedom.reactors.RightClickReactor;
import org.scify.jthinkfreedom.sensors.NetworkGraphSensor;
import org.scify.jthinkfreedom.sensors.WebcamSensor;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.sensors.NetworkImageSensor;
import org.scify.jthinkfreedom.stimuli.HeadUpStimulus;
import org.scify.jthinkfreedom.stimuli.LeftEyeClosedStimulus;

/**
 *
 * @author ggianna
 */
public class Main {
    public static void main(String[] saArgs) {
        ISensor<opencv_core.IplImage> sSensor;
        if (saArgs.length > 0)
            try {
                System.err.println("Trying local capture..." + saArgs[0]);
                sSensor = new WebcamSensor(Integer.parseInt(saArgs[0]));
            } catch (Exception e) {
                System.err.println("Trying for network capture..." + saArgs[0]);
                sSensor = new NetworkImageSensor(saArgs[0]);
            }
        else {
            sSensor = new WebcamSensor(0);
                System.err.println("Webcam default (0).");
        }
        System.err.println("Camera:" + sSensor.toString());
        
        
        try {
            sSensor.start();
        } catch (Exception ex) {
            Logger.getLogger(NetworkGraphSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Connect sensor to reactor and stimulus
        //HeadUpStimulus sHeadUp = new HeadUpStimulus();
//        sHeadUp.setTriggerOffset(10);
//        sSensor.addStimulus(sHeadUp);
//        sHeadUp.addSensor(sSensor);
//        sHeadUp.addReactor(new RightClickReactor());
        
        LeftEyeClosedStimulus sLeftClosedStimulus = new LeftEyeClosedStimulus();
        sSensor.addStimulus(sLeftClosedStimulus);
        sLeftClosedStimulus.addSensor(sSensor);
        sLeftClosedStimulus.addReactor(new LeftClickReactor());
        
//        final CanvasFrame win = new CanvasFrame("Source");
        Date dStart = new Date();
        
        while (true) {
//            opencv_core.IplImage iToRender = sSensor.getData();
//            win.showImage(iToRender);
            Thread.yield();
            
            // Break after 30 seconds
            if (new Date().getTime() - dStart.getTime() > 90000)
                break;
        }
        
        sSensor.stop();
        // Finalize
//        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
//        win.dispose();
        
    }
}
