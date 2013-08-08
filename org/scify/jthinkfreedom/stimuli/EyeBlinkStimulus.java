package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import static org.scify.jthinkfreedom.stimuli.HeadMovementStimulus.RECT_OFFSET;

/**
 *
 * @author nikos
 */
public abstract class EyeBlinkStimulus extends HeadMovementStimulus {

    // Constants
    protected static final int BOTH_EYES = 2;
    protected static final int ONE_EYE = 1;
    protected static final int VALIDITY = 5; // Frames that have to be valid to react
    
    protected CvSeq eyesDetected = null;
    protected String whichEye = ""; // Determine which eye closed
    protected int validityCount = 0; // Current validity
    
    // Each subclass should declare their own storage, grayImage, and smallImage
    private CvMemStorage storage = null;
    private IplImage grayImage = null, smallImage = null;

    public EyeBlinkStimulus() {
        super();
    }

    @Override
    protected void detect() {
        // Detect all eyes in the face area
        eyesDetected = detectOpenEyes(faceImage);
        // Get rightmost and leftmost eyes
        lastLeftRect = getLeftmostEye();
        lastRightRect = getRightmostEye();
    }
    
    @Override
    protected void defineReactionCriteria() {
        // If the right and left eye are the same one
        // (if one rectangle contains the other)
        if(containsRect(lastLeftRect, lastRightRect, RECT_OFFSET) || 
                containsRect(lastRightRect, lastLeftRect, RECT_OFFSET)) {
            // then only one eye has been found (the other must be closed)

            // Make sure previous eyes were initialized
            if(previousRightRect == null || previousLeftRect == null) {
                return;
            }
            // Determine which eye blinked!
            whichEye = whichEyeBlinked();
        }
        else {
            // both eyes are open
            // Mark them as previous eyes
            previousLeftRect = lastLeftRect;
            previousRightRect = lastRightRect;
            validityCount = 0; // Reset validity
        }
    }

    // Returns a sequence of open eyes in the specified image
    protected CvSeq detectOpenEyes(IplImage curImage) {
        grayImage = IplImage.create(cvGetSize(curImage), IPL_DEPTH_8U, 1);
        cvCvtColor(curImage, grayImage, CV_BGR2GRAY);

        smallImage = IplImage.create(curImage.width(),
                curImage.height(), IPL_DEPTH_8U, 1);

        cvResize(grayImage, smallImage, CV_INTER_LINEAR);

        // Equalize the small grayscale
        cvEqualizeHist(smallImage, smallImage);

        // Create temp storage, used during object detection
        storage = CvMemStorage.create();

        // Determine whether an open eye has been found
        CvSeq openEyes = cvHaarDetectObjects(smallImage, eyeClassifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);
        return openEyes;
    }

    // Returns the rectangle which containst the leftmost eye
    // in the current CvSeq (which contains all open eyes)
    protected CvRect getLeftmostEye() {
        // Initialize last left rectangle at position 0x0
        CvRect left = new CvRect(0, 0, 0, 0);
        try {
            // For every eye detected
            for (int i = 0; i < eyesDetected.total(); i++) {
                CvRect r = new CvRect(cvGetSeqElem(eyesDetected, i));
                // If current eye is at the left of the previous one (mirrored)
                if (r.x() > left.x()) {
                    left = r; //make it our new current left eye
                }
            }
            return left;
        } catch (NullPointerException e) {
            e.printStackTrace(System.err);
            System.err.println("Has detectOpenEyes() been called?");
            return left;
        }
    }

    // Returns the rectangle which containst the rightmost eye
    // in the current CvSeq (which contains all open eyes)
    protected CvRect getRightmostEye() {
        // Initialize last right rectangle at position IMG_WIDTHxIMG_HEIGHT
        CvRect right = new CvRect(grabbedImage.width(), grabbedImage.height(),
                0, 0);
        try {
            // For every eye detected
            for (int i = 0; i < eyesDetected.total(); i++) {
                CvRect r = new CvRect(cvGetSeqElem(eyesDetected, i));
                // If current eye is at the right of the previous one (mirrored)
                if (r.x() < right.x()) {
                    right = r; //make it our new current right eye
                }
            }
            return right;
        } catch (NullPointerException e) {
            e.printStackTrace(System.err);
            System.err.println("Has detectOpenEyes() been called?");
            return right;
        }
    }
    
    protected void shouldReact() {
        if (++validityCount == VALIDITY) {
            callReactors();
            validityCount = 0; // Reset validity
        }
        // DEBUG LINES
        System.out.println(whichEye + " " + validityCount);
        //////////////
    }

    // To be overriden by offspring
    protected abstract String whichEyeBlinked();
}
