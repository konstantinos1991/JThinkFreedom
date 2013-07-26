package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.cpp.opencv_objdetect;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.stimuli.haarModels.HaarCascadeModel;

/**
 *
 * @author ggianna
 */
public class LeftEyeClosedStimulus extends StimulusAdapter<opencv_core.IplImage> {
    
    private static final int BOTH_EYES = 2;
    private static final int ONE_EYE_CLOSED = 1;

    protected opencv_objdetect.CvHaarClassifierCascade openClassifier = null;
    protected opencv_core.IplImage grabbedImage = null, grayImage = null, smallImage = null;
    protected opencv_core.CvSeq openEye = null, openLeftEye = null;
    protected Exception exception = null;
    protected int SensitivityCount = 2; // Frames before reaction
    private int iCurSensitivity = SensitivityCount;
    protected int divider = 1; // To scale the image
    
    private long lastUpdate = 0, lastReaction = 0;
    private long reactionTimer = 300; // In milliseconds
    
    protected CvRect lastLeftRect = null, lastRightRect = null;
    
    protected opencv_core.CvMemStorage storage = null;

    public LeftEyeClosedStimulus() {
        super();
        initClassifier();
    }

    public opencv_core.IplImage getGrabbedImage() {
        return grabbedImage;
    }

    protected void initClassifier() {
        try {
            // Preload the opencv_objdetect module to work around a known bug.
            Loader.load(opencv_objdetect.class);
            
            // Load the classifier files from Java resources.
            String openClassfierName = "haarcascade_eye.xml";
            
            File openClassifierFile = new File(HaarCascadeModel.class.getResource(openClassfierName).toURI());
            if (openClassifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + openClassfierName + "\" from Java resources.");
            }

            openClassifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(openClassifierFile.getAbsolutePath())); 
            
            if (openClassifier.isNull()) {
                throw new IOException("Could not load the classifier files.");
            }

        } catch (URISyntaxException | IOException e) {
            if (exception == null) {
                exception = e;
            }
        }

    }

    @Override
    public void onDataReceived() {
        // If no source
        if (lSensors.isEmpty()) // Return
        {
            return;
        }

        // Once every 1/10sec        
        if (new Date().getTime() - lastUpdate < 100) {
            return;
        }
        lastUpdate = new Date().getTime();

        // For each source
        for (ISensor<opencv_core.IplImage> isCurSensor : lSensors) {
            
            getBothEyes(isCurSensor); // Get the coordinates of the eyes
            int total = openEye.total();
            
            // In the current snapshot
            do {
                getBothEyes(isCurSensor); // Get the coordinates of the eyes
            
                // DEBUG LINES
                // Draw a green rectangle around left eye
                //cvDrawRect(grabbedImage,
                //    new CvPoint(lastLeftRect.x()*divider, lastLeftRect.y()*divider),
                //    new CvPoint((lastLeftRect.x()+lastLeftRect.width())*divider,
                //        (lastLeftRect.y()+lastLeftRect.height())*divider),
                //    CvScalar.GREEN, 2, CV_AA, 0);
                // Draw a red rectangle around right eye
                //cvDrawRect(grabbedImage,
                //    new CvPoint(lastRightRect.x()*divider, lastRightRect.y()*divider),
                //    new CvPoint((lastRightRect.x()+lastRightRect.width())*divider,
                //        (lastRightRect.y()+lastRightRect.height())*divider),
                //    CvScalar.RED, 2, CV_AA, 0);
                //cvSaveImage("eye.jpg", grabbedImage);
                //////////////
                
                // Retake sample
                openLeftEye = openEyeSearch();
                int newTotal = openLeftEye.total();
                // If the number of total eyes becomes 1
                // one of them must be closed
                if(newTotal == ONE_EYE_CLOSED) {
                    CvRect r = new CvRect(cvGetSeqElem(openLeftEye, 0));
                    
                    // If that eye is closer to the right one, call the reactors
                    if(Math.abs(r.x()-lastLeftRect.x()) >= Math.abs(r.x()-lastRightRect.x())) {
                        // Sensitivity parameter
                        if (new Date().getTime() - lastReaction < reactionTimer) {
                            return;
                        }
                        shouldReact();
                        lastReaction = new Date().getTime();
                    }
                    else { // If it's the left eye that closed
                        iCurSensitivity = SensitivityCount; // Reset eye sensitivity
                        return;
                    }
                    openLeftEye = openEyeSearch();
                    newTotal = openLeftEye.total();
                }
                else{ // If there was no blink
                    iCurSensitivity = SensitivityCount; // Reset eye sensitivity
                    return;
                }
            } while(total == BOTH_EYES);
        }
    }

    protected void shouldReact() {
        if (iCurSensitivity-- == 0) {
            callReactors();
            iCurSensitivity = SensitivityCount; // Reset eye sensitivity
        }
        
        // DEBUG LINES
        System.err.println("Left eye: " + iCurSensitivity);
        //////////////
        
    }

    // Stores the coordinates of both eyes the current sensor has detected
    protected void getBothEyes(ISensor<opencv_core.IplImage> curSensor) {
        // Get latest data from sensor
        grabbedImage = curSensor.getData();

        // Initialize last left rectangle with size 0x0
        lastLeftRect = new CvRect(0, 0, 0, 0);

        // Initialize last right rectangle with size MAX_INTxMAX_INT
        lastRightRect = new CvRect(Integer.MAX_VALUE, Integer.MAX_VALUE, 
                Integer.MAX_VALUE, Integer.MAX_VALUE);

        // Search for an eye at the left of the previous found
        openEye = openEyeSearch();
        
        for(int i=0; i<openEye.total(); i++) {
            CvRect r = new CvRect(cvGetSeqElem(openEye, i));
            // If current eye is at the left of the previous one (mirrored)
            if(r.x() > lastLeftRect.x()) {
                lastLeftRect = r;
            } //make it our new current left eye

            // If current eye is at the right of the previous one (mirrored)
            if(r.x() < lastRightRect.x()) {
                lastRightRect = r;
            } //make it our new current right eye
        }
    }
    
    protected CvSeq openEyeSearch() { // Returns how many open eyes were detected
        grayImage = opencv_core.IplImage.create(cvGetSize(grabbedImage), IPL_DEPTH_8U, 1);
        cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
        
        smallImage = opencv_core.IplImage.create(grabbedImage.width()/divider, grabbedImage.height()/divider, IPL_DEPTH_8U, 1);
        cvResize(grayImage, smallImage, CV_INTER_LINEAR);
        
        // Equalize the small grayscale
        cvEqualizeHist(smallImage, smallImage);
        
        // Create temp storage, used during object detection
        storage = opencv_core.CvMemStorage.create();
        
        // Determine whether open eye has been found
        CvSeq openeye = cvHaarDetectObjects(smallImage, openClassifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
        
        cvClearMemStorage(storage);
        return openeye;
    }
    
    @Override
    protected void finalize() throws Throwable {
        grabbedImage = grayImage = smallImage = null;

        super.finalize();
    }
}
