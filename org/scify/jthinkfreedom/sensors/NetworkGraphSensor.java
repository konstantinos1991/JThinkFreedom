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
import org.scify.jthinkfreedom.reactors.IReactor;

/**
 *
 * @author ggianna
 */
public class NetworkGraphSensor extends SensorAdapter<DocumentNGramGraph> {
    private IReactor rToCall;
    
    protected opencv_objdetect.CvHaarClassifierCascade classifier = null;
    protected opencv_core.CvMemStorage storage = null;
    protected FrameGrabber grabber = null;
    protected opencv_core.IplImage grabbedImage = null, grayImage = null, smallImage = null;
    protected opencv_core.CvSeq faces = null;
    
    private boolean stop = false;
    protected Exception exception = null;
    
    protected int SensitivityCount = 5; // Frames before reaction
    protected int TriggerOffset = 5;
    protected int divider = 2;
    protected String sCameraUrl = null;
    
    private int iCurSensitivity = SensitivityCount;
    private int iFaceCenterX, iFaceCenterY, iPrvCenterX = -1, iPrvCenterY = -1;
    private long lastUpdate = 0;

    public void setTriggerOffset(int TriggerOffset) {
        this.TriggerOffset = TriggerOffset;
    }
    

    public void setSensitivityCount(int SensitivityCount) {
        this.SensitivityCount = SensitivityCount;
        iCurSensitivity = SensitivityCount; // Reset current sensitivity count
    }

    public int getSensitivityCount() {
        return SensitivityCount;
    }

    public void setDivider(int divider) {
        this.divider = divider;
    }

    public int getDivider() {
        return divider;
    }

    public IplImage getGrayImage() {
        return grayImage;
    }

    public IplImage getSmallImage() {
        return smallImage;
    }

    public IplImage getGrabbedImage() {
        return grabbedImage;
    }

    protected void initClassifier(){
            try {
                // Load the classifier file from Java resources.
                String classiferName = "haarcascade_frontalface_alt.xml";
                //File classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");
                File classifierFile = new File(NetworkGraphSensor.class.getResource(classiferName).toURI());
                if (classifierFile == null || classifierFile.length() <= 0) {
                    throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
                }

                // Preload the opencv_objdetect module to work around a known bug.
                Loader.load(opencv_objdetect.class);
                classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
                //classifierFile.delete();
                if (classifier.isNull()) {
                    throw new IOException("Could not load the classifier file.");
                }

                storage = CvMemStorage.create();
            } catch (Exception e) {
                if (exception == null) {
                    exception = e;
                }
            }
        
    }
    
    public NetworkGraphSensor() {
        if (sCameraUrl == null)
            sCameraUrl = "http://localhost:80/cam";
        initClassifier();
        
    }
    
    public NetworkGraphSensor(String sCameraUrl) {
        this.sCameraUrl = sCameraUrl;
        initClassifier();
    }

    @Override
    public DocumentNGramGraph getData() {
        DocumentNGramGraph dgRes = new DocumentNGramGraph();
        long curUpdate = new Date().getTime();
        // Once every 1/10sec
        if (curUpdate - lastUpdate < 100)
            return null;
        try {
            grayImage  = opencv_core.IplImage.create(grabbedImage.width(),   grabbedImage.height(),   IPL_DEPTH_8U, 1);
            smallImage = opencv_core.IplImage.create(grabbedImage.width()/divider, grabbedImage.height()/divider, IPL_DEPTH_8U, 1);
            stop = false;
//            while (!stop && (grabbedImage = grabber.grab()) != null) {
            if (!stop && (grabbedImage = grabber.grab()) != null) {
                lastUpdate = curUpdate;
                cvClearMemStorage(storage);
                cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
                cvResize(grayImage, smallImage, CV_INTER_AREA);
                faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                for (int i = 0; i < faces.total(); i++) {
                    CvRect r = new CvRect(cvGetSeqElem(faces, i));
                    cvRectangle(grabbedImage, 
                            cvPoint(r.x() * divider, r.y() * divider), cvPoint(r.x() * divider + r.width() * divider, 
                            r.y() * divider+r.height() * divider), CvScalar.RED, 1, CV_AA, 0);
                    // Detect center
                    iFaceCenterX = (r.x() + r.width()) + r.x() * divider;
                    iFaceCenterY = (r.y() + r.height()) + r.y() * divider;

                    shouldReact(iFaceCenterX, iFaceCenterY);
                }
            }
        }
        catch (Exception e) {
            if (exception == null) {
                exception = e;
            }            
        }
        
        return dgRes;
    }

    protected void shouldReact(int iCurX, int iCurY) {
        if (iPrvCenterY == -1) {
            iPrvCenterY = iCurY;
            return;
        }
        if (Math.abs(iPrvCenterY - iCurY) > 10.0)
        {
            if (iCurSensitivity-- == 0) {
                iPrvCenterY = iCurY;
                rToCall.react();
                iCurSensitivity = SensitivityCount;
            }
            System.err.println("Checking " + iCurSensitivity);
        }
        else
            // Reset counter
            iCurSensitivity = SensitivityCount;
    }
    
    @Override
    protected void finalize() throws Throwable {
        grabbedImage = grayImage = smallImage = null;
        
        if (grabber != null) {
            grabber.stop();
            grabber.release();
            grabber = null;
        }
        super.finalize();
    }

    @Override
    public void stop() {
        // Release images
        grabbedImage = grayImage = smallImage = null;        
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
    
    public void setReactor(IReactor rToCall) {
        this.rToCall = rToCall;
    }

    @Override
    public void start() {
            try {
                
                grabber = FrameGrabber.createDefault(sCameraUrl);
                grabber.setImageWidth(getWidth());
                grabber.setImageHeight(getHeight());
                grabber.start();
                grabbedImage = grabber.grab();
            } catch (Exception e) {
//                if (grabber != null) grabber.release();
//                grabber = new OpenCVFrameGrabber(0);
//                grabber.setImageWidth(getWidth());
//                grabber.setImageHeight(getHeight());
//                grabber.start();
//                grabbedImage = grabber.grab();
                e.printStackTrace();
                this.exception = e;
                return;
            }
    }

   

}
