/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.scify.jthinkfreedom.reactors.LeftClickReactor;
import org.scify.jthinkfreedom.reactors.MouseMoveDownReactor;
import org.scify.jthinkfreedom.reactors.MouseMoveLeftReactor;
import org.scify.jthinkfreedom.reactors.MouseMoveRightReactor;
import org.scify.jthinkfreedom.reactors.MouseMoveUpReactor;
import org.scify.jthinkfreedom.reactors.RightClickReactor;
import org.scify.jthinkfreedom.sensors.WebcamSensor;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.sensors.NetworkImageSensor;
import org.scify.jthinkfreedom.stimuli.HeadDownStimulus;
import org.scify.jthinkfreedom.stimuli.HeadLeftStimulus;
import org.scify.jthinkfreedom.stimuli.HeadRightStimulus;
import org.scify.jthinkfreedom.stimuli.HeadUpStimulus;
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
            } catch (Exception e) {
                System.err.println("Trying for network capture..." + saArgs[0]);
                sSensor = new NetworkImageSensor(saArgs[0]);
            }
        }
        else {
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
        // Left Click to Left Eye
        LeftEyeBlinkStimulus sLeftBlinkStimulus = new LeftEyeBlinkStimulus();
        sSensor.addStimulus(sLeftBlinkStimulus);
        sLeftBlinkStimulus.addSensor(sSensor);
        sLeftBlinkStimulus.addReactor(new LeftClickReactor());
        // Right Click to Right Eye
        RightEyeBlinkStimulus sRightBlinkStimulus = new RightEyeBlinkStimulus();
        sSensor.addStimulus(sRightBlinkStimulus);
        sRightBlinkStimulus.addSensor(sSensor);
        sRightBlinkStimulus.addReactor(new RightClickReactor());
        // Mouse Up to Head Up
        HeadUpStimulus sHeadUp = new HeadUpStimulus();
        sSensor.addStimulus(sHeadUp);
        sHeadUp.addSensor(sSensor);
        sHeadUp.addReactor(new MouseMoveUpReactor());
        // Mouse Down to Head Down
        HeadDownStimulus sHeadDown = new HeadDownStimulus();
        sSensor.addStimulus(sHeadDown);
        sHeadDown.addSensor(sSensor);
        sHeadDown.addReactor(new MouseMoveDownReactor());
        // Mouse Left to Head Left
        HeadLeftStimulus sHeadLeft = new HeadLeftStimulus();
        sSensor.addStimulus(sHeadLeft);
        sHeadLeft.addSensor(sSensor);
        sHeadLeft.addReactor(new MouseMoveLeftReactor());
        // Mouse Right to Head Right
        HeadRightStimulus sHeadRight = new HeadRightStimulus();
        sSensor.addStimulus(sHeadRight);
        sHeadRight.addSensor(sSensor);
        sHeadRight.addReactor(new MouseMoveRightReactor());
        
        // FOR SOCKET COMMUNICATION
        //TCPReactorClient rReactor = new TCPReactorClient();
        //rReactor.add(new Pair("83.212.112.152", 4444));
        //rReactor.add(new Pair("192.168.1.65", 4444));
        //sLeftBlinkStimulus.addReactor(rReactor);
        ///////////////////////////
        
        // Canvas
        final CanvasFrame win = new CanvasFrame("Source");
        opencv_core.IplImage iToRender;
        
        Date dStart = new Date();
        
        while (true) {
            // Canvas
            iToRender = sLeftBlinkStimulus.getFaceImage();
            if(iToRender!=null) {
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
        // Canvas
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        win.dispose();
        
    }
}
