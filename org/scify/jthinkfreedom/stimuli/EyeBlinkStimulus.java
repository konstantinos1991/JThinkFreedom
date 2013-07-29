package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
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
 * @author nikos
 */
public class EyeBlinkStimulus extends StimulusAdapter<IplImage> {

    // Constants
    protected static final int BOTH_EYES = 2;
    protected static final int ONE_EYE = 1;
    protected static final int SCALE = 1;
    
    protected opencv_objdetect.CvHaarClassifierCascade eyeClassifier = null, faceClassifier = null;
    protected opencv_core.IplImage grabbedImage = null, grayImage = null, smallImage = null;
    protected opencv_core.CvSeq eyesDetected = null, facesDetected = null;
    protected opencv_core.CvMemStorage storage = null;
    // For the eyes
    protected opencv_core.CvRect lastLeftRect = null, lastRightRect = null;
    // For the faces
    protected opencv_core.CvRect faceRect = null;
    
    protected long lastUpdate = 0;
    protected int updateTimer = 100;

    public EyeBlinkStimulus() {
        super();
        initClassifier(); // For eye and face detection
    }

    public IplImage getGrabbedImage() {
        return grabbedImage;
    }
    
    private void initClassifier() {
        try {
            // Preload the opencv_objdetect module to work around a known bug.
            Loader.load(opencv_objdetect.class);
            
            // Load the classifier files from Java resources.
            String openClassfierName = "haarcascade_eye.xml";
            String faceClassfierName = "haarcascade_frontalface_alt.xml";
            
            File openClassifierFile = new File(HaarCascadeModel.class.getResource(openClassfierName).toURI());
            File faceClassifierFile = new File(HaarCascadeModel.class.getResource(faceClassfierName).toURI());
            if (openClassifierFile.length() <= 0 || faceClassifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + openClassfierName + "\" from Java resources.");
            }

            eyeClassifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(openClassifierFile.getAbsolutePath())); 
            faceClassifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(faceClassifierFile.getAbsolutePath())); 
            
            if (eyeClassifier.isNull() || faceClassifier.isNull()) {
                throw new IOException("Could not load the classifier files.");
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace(System.err);
        }

    }
    
    @Override
    public void onDataReceived() {
        // To be implemented by offspring
        if (lSensors.isEmpty()) 
        {
            return;
        }
        
        if (new Date().getTime() - lastUpdate < 100) {
            return;
        }
        lastUpdate = new Date().getTime();
        
        for (ISensor<IplImage> isCurSensor : lSensors) {
            
        }
    }
    
    // Returns a sequence of open eyes in the current frame
    protected opencv_core.CvSeq detectOpenEyes() {
        grayImage = opencv_core.IplImage.create(cvGetSize(grabbedImage), IPL_DEPTH_8U, 1);
        cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
        
        smallImage = opencv_core.IplImage.create(grabbedImage.width()/SCALE,
                grabbedImage.height()/SCALE, IPL_DEPTH_8U, 1);
        
        cvResize(grayImage, smallImage, CV_INTER_LINEAR);
        
        // Equalize the small grayscale
        cvEqualizeHist(smallImage, smallImage);
        
        // Create temp storage, used during object detection
        storage = opencv_core.CvMemStorage.create();
        
        // Determine whether open eye has been found
        opencv_core.CvSeq openEyes = cvHaarDetectObjects(smallImage, eyeClassifier, storage, 1.2, 3, CV_HAAR_DO_CANNY_PRUNING);
        
        cvClearMemStorage(storage);
        return openEyes;
    }
    
    // Returns a sequence of faces in the current frame
    protected opencv_core.CvSeq detectFaces() {
        grayImage = opencv_core.IplImage.create(cvGetSize(grabbedImage), IPL_DEPTH_8U, 1);
        cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
        
        smallImage = opencv_core.IplImage.create(grabbedImage.width()/SCALE,
                grabbedImage.height()/SCALE, IPL_DEPTH_8U, 1);
        
        cvResize(grayImage, smallImage, CV_INTER_LINEAR);
        
        // Equalize the small grayscale
        cvEqualizeHist(smallImage, smallImage);
        
        // Create temp storage, used during object detection
        storage = opencv_core.CvMemStorage.create();
        
        // Determine whether open eye has been found
        opencv_core.CvSeq faces = cvHaarDetectObjects(smallImage, faceClassifier, storage, 1.2, 3, CV_HAAR_DO_CANNY_PRUNING);
        
        cvClearMemStorage(storage);
        return faces;
    }
    
    // Returns the rectangle which containst the leftmost eye
    // in the current CvSeq (which contains all open eyes)
    protected CvRect getLeftmostEye() {
        // Initialize last left rectangle with size 0x0
        CvRect left = new CvRect(0, 0, 0, 0);
        try {
            // For every eye detected
            for(int i=0; i<eyesDetected.total(); i++) {
                CvRect r = new CvRect(cvGetSeqElem(eyesDetected, i));
                // If current eye is at the left of the previous one (mirrored)
                if(r.x() > left.x()) {
                    left = r; //make it our new current left eye
                }
            }
            return left;
        }
        catch(NullPointerException e) {
            e.printStackTrace(System.err);
            System.err.println("Has detectOpenEyes() been called?");
            return left;
        }
    }
    
    // Returns the rectangle which containst the rightmost eye
    // in the current CvSeq (which contains all open eyes)
    protected CvRect getRightmostEye() {
        // Initialize last right rectangle with size MAX_INTxMAX_INT
        CvRect right = new CvRect(Integer.MAX_VALUE, Integer.MAX_VALUE, 
                Integer.MAX_VALUE, Integer.MAX_VALUE);
        try {
            // For every eye detected
            for(int i=0; i<eyesDetected.total(); i++) {
                CvRect r = new CvRect(cvGetSeqElem(eyesDetected, i));
                // If current eye is at the right of the previous one (mirrored)
                if(r.x() < right.x()) {
                    right = r; //make it our new current right eye
                }
            }
            return right;
        }
        catch(NullPointerException e) {
            e.printStackTrace(System.err);
            System.err.println("Has detectOpenEyes() been called?");
            return right;
        }
    }
    
    // Returns the most central face in the image
    protected CvRect getCentralFace() {
        CvRect face = new CvRect(0, 0, 0, 0);
        CvPoint faceCenter = new CvPoint(0, 0);
        try {
            // For every face detected
            for(int i=0; i<facesDetected.total(); i++) {
                CvRect r = new CvRect(cvGetSeqElem(facesDetected, i));
                // Get the center of the current rectangle
                CvPoint curCenter = getRectangleCenter(r);
                // If current face is closer to the middle of the screen
                if(Math.abs(curCenter.x() - getGrabbedImage().width()/2) < 
                        Math.abs(faceCenter.x() - getGrabbedImage().width()/2) &&
                        Math.abs(curCenter.y() - getGrabbedImage().height()/2) <
                        Math.abs(faceCenter.y() - getGrabbedImage().height()/2)) {
                    // Make it our new central face
                    face = r;
                }
            }
            return face;
        }
        catch(NullPointerException e) {
            e.printStackTrace(System.err);
            System.err.println("Has detectFaces() been called?");
            return face;
        }
    }
    
    // Return the central point of a rectangle
    protected CvPoint getRectangleCenter(CvRect r) {
        return new CvPoint(r.x() + (r.x()+r.width())/2,
                r.y() + (r.y()+r.height())/2);
    }
}
