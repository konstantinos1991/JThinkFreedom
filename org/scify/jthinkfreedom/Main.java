/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import gr.demokritos.iit.jinsect.structs.Pair;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.scify.jthinkfreedom.reactors.TCPReactorClient;
import org.scify.jthinkfreedom.sensors.NetworkGraphSensor;
import org.scify.jthinkfreedom.sensors.WebcamSensor;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.sensors.NetworkImageSensor;
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
        LeftEyeClosedStimulus sLeftClosedStimulus = new LeftEyeClosedStimulus();
        sSensor.addStimulus(sLeftClosedStimulus);
        sLeftClosedStimulus.addSensor(sSensor);
        //sLeftClosedStimulus.addReactor(new RightClickReactor());
        TCPReactorClient rReactor = new TCPReactorClient();
        rReactor.add(new Pair("192.168.1.38", 4444));
        sLeftClosedStimulus.addReactor(rReactor);
        
        // Canvas
        final CanvasFrame win = new CanvasFrame("Source");
        opencv_core.IplImage iToRender;
        
        Date dStart = new Date();
        
        while (true) {
            // Canvas
            iToRender = sLeftClosedStimulus.getGrabbedImage();
            if(iToRender!=null)
                win.showImage(iToRender);
            
            Thread.yield();
            // Break after 90 seconds
            if (new Date().getTime() - dStart.getTime() > 90000)
                break;
        }
        
        sSensor.stop();
        // Finalize
        // Canvas
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        win.dispose();
        
    }
}
