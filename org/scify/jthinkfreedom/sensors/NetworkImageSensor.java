/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.sensors;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_objdetect;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scify.jthinkfreedom.reactors.IReactor;

/**
 *
 * @author ggianna
 */
public class NetworkImageSensor extends SensorAdapter<IplImage> implements Runnable {
    protected FrameGrabber grabber = null;
    protected String sCameraUrl = null;
    private boolean stop = false;
    protected opencv_core.IplImage grabbedImage = null;
    protected Exception exception = null;
    Thread tDataReader;
    
    public NetworkImageSensor() {
        if (sCameraUrl == null)
            sCameraUrl = "http://localhost:80/cam";
    }
    
    public NetworkImageSensor(String sCameraUrl) {
        this.sCameraUrl = sCameraUrl;
    }

    @Override
    public IplImage getData() {
        try {
            stop = false;
//            while (!stop && (grabbedImage = grabber.grab()) != null) {
            if (!stop && (grabbedImage = grabber.grab()) != null) {
                return grabbedImage;
            }
        }
        catch (Exception e) {
            if (exception == null) {
                exception = e;
            }            
        }
        
        return null;
        
    }

    @Override
    protected void finalize() throws Throwable {
        grabbedImage = null;
        
        if (grabber != null) {
            grabber.stop();
            grabber.release();
            grabber = null;
        }
        super.finalize();
    }

    @Override
    public void stop() {
        // Stop data reader thread
        bRunning = false;
        try {
            if (tDataReader != null)
                // Wait for its completion
                tDataReader.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(
                    NetworkImageSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Release images
        grabbedImage = null;        
        if (grabber != null) {
            try {
                grabber.stop();
                grabber.release();
            }
            catch (FrameGrabber.Exception e) {
                if (exception == null) {
                    exception = e;
                }                            
            }
            finally {
                grabber = null;
            }
        }        
    }

    protected int getWidth() {
        // TODO: Change
        return 640;
    }

    protected int getHeight() {
        // TODO: Change
        return 400;
    }
    
    @Override
    public void start() {
            try {
                grabber = FrameGrabber.createDefault(sCameraUrl);
                grabber.setImageWidth(getWidth());
                grabber.setImageHeight(getHeight());
                grabber.start();
                grabbedImage = grabber.grab();
                // Running
                this.bRunning = true;
                // Init and start reader thread
                tDataReader = new Thread(this);
                tDataReader.start();
            } catch (Exception e) {
//                if (grabber != null) grabber.release();
//                grabber = new OpenCVFrameGrabber(0);
//                grabber.setImageWidth(getWidth());
//                grabber.setImageHeight(getHeight());
//                grabber.start();
//                grabbedImage = grabber.grab();
                e.printStackTrace(System.err);
                this.exception = e;
                // No longer running
                this.bRunning = false;
            }
    }

    @Override
    public void run() {
        while (isRunning())
        {
            try {
                grabbedImage = grabber.grab();
                // Update all stimuli of new data
                updateStimuli();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                this.exception = e;
                // No longer running
                this.bRunning = false;
            }
        }
    }


}
