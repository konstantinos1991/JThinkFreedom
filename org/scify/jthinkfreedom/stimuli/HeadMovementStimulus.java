/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacpp.Loader;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawRect;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import com.googlecode.javacv.cpp.opencv_objdetect;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.stimuli.haarModels.HaarCascadeModel;

/**
 *
 * @author eustratiadis-hua
 */
public abstract class HeadMovementStimulus extends StimulusAdapter<IplImage> {
    
    // Constants
    protected static final int RECT_OFFSET = 20; // Pixels larger
    
    // Declare classifiers for face elements
    protected opencv_objdetect.CvHaarClassifierCascade eyeClassifier = null, 
            faceClassifier = null, noseClassifier = null;
    protected static IplImage grabbedImage = null;
    // To zoom in the face
    protected static IplImage faceImage = null;
    protected CvSeq facesDetected = null;
    // For the eyes
    protected static CvRect lastLeftRect = null, lastRightRect = null;
    protected static CvRect previousLeftRect = null, previousRightRect = null;
    // For the faces
    protected static CvRect faceRect = null;
    // For the nose
    protected static CvRect noseRect = null;
    protected static CvRect previousNoseRect = null;
    
    
    private long lastUpdate = 0;
    private int updateTimer = 100;
    
    // Each subclass should declare their own storage, grayImage, and smallImage
    private CvMemStorage storage = null;
    private IplImage grayImage = null, smallImage = null;

    public HeadMovementStimulus() {
        super();
        initClassifier(); // For eye and face detection
    }

    public IplImage getGrabbedImage() {
        return grabbedImage;
    }
    
    public IplImage getFaceImage() {
        return faceImage;
    }

    private void initClassifier() {
        try {
            // Preload the opencv_objdetect module to work around a known bug.
            Loader.load(opencv_objdetect.class);
            
            // Load the classifier files from Java resources.
            String openClassifierName = "haarcascade_eye.xml";
            String faceClassifierName = "haarcascade_frontalface_alt.xml";
            String noseClassifierName = "haarcascade_mcs_nose.xml";
            
            File openClassifierFile = new File(HaarCascadeModel.class.getResource(openClassifierName).toURI());
            File faceClassifierFile = new File(HaarCascadeModel.class.getResource(faceClassifierName).toURI());
            File noseClassifierFile = new File(HaarCascadeModel.class.getResource(noseClassifierName).toURI());
            
            if(openClassifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + openClassifierName + "\" from Java resources.");
            }
            if(faceClassifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + faceClassifierName + "\" from Java resources.");
            } 
            if(noseClassifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + noseClassifierName + "\" from Java resources.");
            }

            eyeClassifier = new CvHaarClassifierCascade(cvLoad(openClassifierFile.getAbsolutePath())); 
            faceClassifier = new CvHaarClassifierCascade(cvLoad(faceClassifierFile.getAbsolutePath())); 
            noseClassifier = new CvHaarClassifierCascade(cvLoad(noseClassifierFile.getAbsolutePath()));
            
            if (eyeClassifier.isNull() || faceClassifier.isNull() || noseClassifier.isNull()) {
                throw new IOException("Could not load the classifier files.");
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace(System.err);
        }

    }
    
    @Override
    public void onDataReceived() {
        // To be implemented by offspring
        if (lSensors.isEmpty()) {
            return;
        }
        
        if (new Date().getTime() - lastUpdate < updateTimer) {
            return;
        }
        lastUpdate = new Date().getTime();
        
        for (ISensor<IplImage> isCurSensor : lSensors) {
            // Get latest data from sensor
            grabbedImage = isCurSensor.getData();
            // Detect all faces in current frame
            facesDetected = detectFaces(grabbedImage);
            // If no faces were found, terminate
            if(facesDetected.total() == 0) {
                return;
            }
            // Get most central face
            faceRect = getCentralRectangle(facesDetected);
            
            // If a face was found
            if(faceRect != null && faceRect.width() > 0 && faceRect.height() > 0) {
                // Set region of interest (the face)
                cvSetImageROI(grabbedImage, faceRect);
                faceImage = cvCreateImage(cvGetSize(grabbedImage),
                        grabbedImage.depth(),
                        grabbedImage.nChannels());
                cvCopy(grabbedImage, faceImage, null);
                // Call detect() method of offspring
                // It detects the element of interest in the current face
                detect();
                
                // Reset region of interest
                cvResetImageROI(grabbedImage);
            }
            
            // If you didnt succeed in finding any eyes or a nose, return
            if(lastLeftRect == null || lastRightRect == null || noseRect == null) {
                return;
            }
            
            // Makes system slow - Only to be called when debugging
            //drawTrackingData();

            // Call defineReactionCriteria() of offspring
            // It decides whether or not a reactor should be called
            defineReactionCriteria();
        }
        
    }
    
    // Returns a sequence of faces in the specified image
    protected CvSeq detectFaces(IplImage curImage) {
        grayImage = IplImage.create(cvGetSize(curImage), IPL_DEPTH_8U, 1);
        cvCvtColor(curImage, grayImage, CV_BGR2GRAY);
        
        smallImage = IplImage.create(curImage.width(),
                curImage.height(), IPL_DEPTH_8U, 1);
        
        cvResize(grayImage, smallImage, CV_INTER_LINEAR);
        
        // Equalize the small grayscale
        cvEqualizeHist(smallImage, smallImage);
        
        // Create temp storage, used during object detection
        storage = CvMemStorage.create();
        
        // Determine whether a face has been found
        CvSeq faces = cvHaarDetectObjects(smallImage, faceClassifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
        
        cvClearMemStorage(storage);
        return faces;
    }
    
    // Returns the most central rectangle in the image
    protected CvRect getCentralRectangle(CvSeq sequence) {
        CvRect rect = new CvRect(0, 0, 0, 0);
        CvPoint rectCenter = new CvPoint(0, 0);
        try {
            // For every rectangle detected
            for(int i=0; i<sequence.total(); i++) {
                CvRect r = new CvRect(cvGetSeqElem(sequence, i));
                // Get the center of the current rectangle
                CvPoint curCenter = getRectangleCenter(r);
                // If current face is closer to the middle of the screen
                if(Math.abs(curCenter.x() - getGrabbedImage().width()/2) < 
                        Math.abs(rectCenter.x() - getGrabbedImage().width()/2) &&
                        Math.abs(curCenter.y() - getGrabbedImage().height()/2) <
                        Math.abs(rectCenter.y() - getGrabbedImage().height()/2)) {
                    // Make it our new central face
                    rect = r;
                }
            }
            return rect;
        }
        catch(NullPointerException e) {
            e.printStackTrace(System.err);
            System.err.println("Has detect been called?");
            return rect;
        }
    }
    
    protected void drawTrackingData () {
        // Draw a magenta rectangle around the face
        //if(faceRect != null){
        //    cvDrawRect(grabbedImage,
        //        new CvPoint(faceRect.x(), faceRect.y()),
        //        new CvPoint((faceRect.x()+faceRect.width()),
        //            (faceRect.y()+faceRect.height())),
        //        CvScalar.MAGENTA, 2, CV_AA, 0);
        //}
        // Draw a black rectangle around the nose
        if(noseRect != null) {
            cvDrawRect(faceImage,
                new CvPoint(noseRect.x(), noseRect.y()),
                new CvPoint((noseRect.x()+noseRect.width()),
                    (noseRect.y()+noseRect.height())),
                CvScalar.BLACK, 2, CV_AA, 0);
        }
        // Draw a green rectangle around the left eye
        if(lastLeftRect != null) {
            cvDrawRect(faceImage,
                new CvPoint(lastLeftRect.x(), lastLeftRect.y()),
                new CvPoint((lastLeftRect.x()+lastLeftRect.width()),
                    (lastLeftRect.y()+lastLeftRect.height())),
                CvScalar.GREEN, 2, CV_AA, 0);
        }
        // Draw a red rectangle around the right eye
        if(lastRightRect != null) {
            cvDrawRect(faceImage,
                new CvPoint(lastRightRect.x(), lastRightRect.y()),
                new CvPoint((lastRightRect.x()+lastRightRect.width()),
                    (lastRightRect.y()+lastRightRect.height())),
                CvScalar.RED, 2, CV_AA, 0);
        }
        // Snapshot
        cvSaveImage("tracked.jpg", faceImage);
    }
    
    // Return the central point of a rectangle
    public CvPoint getRectangleCenter(CvRect r) {
        return new CvPoint(r.x() + (r.x()+r.width())/2,
                r.y() + (r.y()+r.height())/2);
    }
    
    // Check if larger (by offset) rectangle contains the smaller one
    public boolean containsRect(CvRect big, CvRect small, int offset) {
        // If big rectangle with offset leaves the screen borders
        if(big.x() - offset/2 < 0 ||
                big.y() - offset/2 < 0 ||
                big.x() + big.width() + offset/2 > grabbedImage.width() ||
                big.y() + big.height() + offset/2 > grabbedImage.height()) {
            return false; // Do nothing
        }
        // Construct a rectangle RECT_OFFSTET pixels larger than the big one
        CvRect container = new CvRect(big.x() - offset/2,
                big.y() - offset/2,
                big.x() + big.width() + offset/2,
                big.y() + big.height() + offset/2);
        // See if the new rectangle contains the smaller one
        if(container.x() < small.x() &&
                container.y() < small.y() &&
                container.x() + container.width() > small.x() + small.width() &&
                container.y() + container.height() > small.y() + small.height()) {
            return true;
        }
        else {
            return false;
        }
    }
    
    // To be overriden by offspring
    protected abstract void detect();
    protected abstract void defineReactionCriteria();
}
