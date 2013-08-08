/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
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

/**
 *
 * @author eustratiadis-hua
 */
public class HeadDirectionStimulus extends HeadMovementStimulus {

    //Constants
    protected static final int VALIDITY = 2; // Frames that have to be valid to react
    
    protected CvSeq nosesDetected = null;
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
            // Make it our new nose
            noseRect = new CvRect(cvGetSeqElem(nosesDetected, 0));
        }
    }
    
    @Override
    protected void defineReactionCriteria() {
        
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
        if (++validityCount == VALIDITY) {
            callReactors();
            validityCount = 0; // Reset validity
        }
        // DEBUG LINES
        System.out.println(direction + " " + validityCount);
        //////////////
    }
}
