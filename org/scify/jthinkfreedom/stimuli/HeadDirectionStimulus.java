/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import static org.scify.jthinkfreedom.stimuli.HeadMovementStimulus.noseRect;
import static org.scify.jthinkfreedom.stimuli.HeadMovementStimulus.previousLeftRect;
import static org.scify.jthinkfreedom.stimuli.HeadMovementStimulus.previousNoseRect;

/**
 *
 * @author eustratiadis-hua
 */
public abstract class HeadDirectionStimulus extends HeadMovementStimulus {
    
    // Constants
    protected static final int LEFT = 0;
    protected static final int UP = 1;
    protected static final int RIGHT = 2;
    protected static final int DOWN = 3;
    
    protected CvSeq nosesDetected = null;
    // Center of previous and current nose rectangles, used for calculations
    protected static CvPoint prevNoseCenter = null, curNoseCenter = null;
    // Center of left and right eye rectangles, when we last detected both eyes
    protected static CvPoint prevLeftEyeCenter = null, prevRightEyeCenter = null;
    
    // Lock opposite directions so there won't be back and forths
    protected boolean[] lock = new boolean[] {false, false, false, false};
    
    protected String direction = ""; // Determine the direction the head moved
    protected int validityCount = 0; // Current validity
    
    // Each subclass should declare their own storage, grayImage, and smallImage
    private CvMemStorage storage = null;
    private IplImage grayImage = null, smallImage = null;

    public HeadDirectionStimulus() {
        super();
    }

    @Override
    protected void detect () {
        // Detect all noses in the face area (should be 1)
        nosesDetected = detectNoses(faceImage);
        // If a nose was found
        if(nosesDetected.total() > 0) {
            // Get the most central nose (in case of false positives)
            noseRect = getCentralRectangle(nosesDetected);
        }
    }
    
    @Override
    protected void defineReactionCriteria() {
        // If previous left or right eye haven't received a value yet, return
        if(previousLeftRect == null || previousRightRect == null) {
            return;
        }
        // Also, if the nose rectangle remains with the initial values (0, 0)
        if(containsRect(noseRect, new CvRect(0, 0, 0, 0), 0)) {
            // Then we lost the nose
            noseRect = null;
            return;
        }
        
        // If previous nose has no value (first time only)
        if(previousNoseRect == null) {
            // Mark the center of the previous nose rectangle
            previousNoseRect = noseRect;
            prevNoseCenter = getRectangleCenter(previousNoseRect);
            // Mark the centers of the eye rectangles, when we last saw both eyes
            prevLeftEyeCenter = getRectangleCenter(previousLeftRect);
            prevRightEyeCenter = getRectangleCenter(previousRightRect);
        }
        // If nose and previous nose rectangles are the same
        if(containsRect(previousNoseRect, noseRect, 0)) {
            // Then the nose hasn't moved, do nothing
        }
        else {
            // The nose has moved
            // Mark the center of the current nose rectangle
            curNoseCenter = getRectangleCenter(noseRect);
            // Determine which way the head went
            direction = whichWayHeadWent();
            
        }
    }

    // Returns a sequence of noses in the specified image
    protected CvSeq detectNoses(IplImage curImage) {
        grayImage = IplImage.create(cvGetSize(curImage), IPL_DEPTH_8U, 1);
        cvCvtColor(curImage, grayImage, CV_BGR2GRAY);

        smallImage = IplImage.create(curImage.width(),
                curImage.height(), IPL_DEPTH_8U, 1);

        cvResize(grayImage, smallImage, CV_INTER_LINEAR);

        // Equalize the small grayscale
        cvEqualizeHist(smallImage, smallImage);

        // Create temp storage, used during object detection
        storage = CvMemStorage.create();

        // Determine whether a nose has been found
        CvSeq noses = cvHaarDetectObjects(smallImage, noseClassifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);
        return noses;
    }
    
    protected void shouldReact() {
        callReactors();
        // DEBUG LINES
        //System.out.println(direction + " " + validityCount);
        //////////////
    }
    
    // To be overriden by offspring
    protected abstract String whichWayHeadWent();
}
