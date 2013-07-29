/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawRect;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import java.util.Date;
import org.scify.jthinkfreedom.sensors.ISensor;
import static org.scify.jthinkfreedom.stimuli.EyeBlinkStimulus.SCALE;

/**
 *
 * @author eustratiadis-hua
 */

public class LeftEyeBlinkStimulus extends EyeBlinkStimulus{
    
    //Constants
    private static final int RECT_OFFSET = 20; // Pixels larger
    private static final int VALIDITY = 10; // Frames that have to be valid to react
    
    private opencv_core.CvRect previousLeftRect = null, previousRightRect = null;
    
    private int validityCount = 0; // Current validity
    
    public LeftEyeBlinkStimulus() {
        super();
    }
    
    // Check if larger (by offset) rectangle contains the smaller one
    public boolean containsRect(CvRect big, CvRect small) {
        // Don't leave screen borders
        if(big.x() - RECT_OFFSET/2 < 0 ||
                big.y() - RECT_OFFSET/2 < 0 ||
                big.x() + big.width() + RECT_OFFSET/2 > grabbedImage.width() ||
                big.y() + big.height() + RECT_OFFSET/2 > grabbedImage.height()) {
            return false; // TODO: change
        }
        // Construct a rectangle RECT_OFFSTET pixels larger than the big one
        CvRect container = new CvRect(big.x() - RECT_OFFSET/2,
                big.y() - RECT_OFFSET/2,
                big.x() + big.width() + RECT_OFFSET/2,
                big.y() + big.height() + RECT_OFFSET/2);
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
        
        for (ISensor<opencv_core.IplImage> isCurSensor : lSensors) {
            // Get latest data from sensor
            grabbedImage = isCurSensor.getData();
            
            // Detect all eyes in the current frame
            eyesDetected = detectOpenEyes();
            
            // Detect all faces in current frame
            facesDetected = detectFaces();
            
            // Get rightmost and leftmost eyes
            lastLeftRect = getLeftmostEye();
            lastRightRect = getRightmostEye();
            
            //Get most central face
            faceRect = getCentralFace();
            
            // DEBUG LINES
            // Draw a green rectangle around left eye
            //cvDrawRect(grabbedImage,
            //    new CvPoint(lastLeftRect.x()*SCALE, lastLeftRect.y()*SCALE),
            //    new CvPoint((lastLeftRect.x()+lastLeftRect.width())*SCALE,
            //        (lastLeftRect.y()+lastLeftRect.height())*SCALE),
            //    CvScalar.GREEN, 2, CV_AA, 0);
            // Draw a red rectangle around right eye
            //cvDrawRect(grabbedImage,
            //    new CvPoint(lastRightRect.x()*SCALE, lastRightRect.y()*SCALE),
            //    new CvPoint((lastRightRect.x()+lastRightRect.width())*SCALE,
            //        (lastRightRect.y()+lastRightRect.height())*SCALE),
            //    CvScalar.RED, 2, CV_AA, 0);
            // Draw a magenta rectangle around face
            //cvDrawRect(grabbedImage,
            //    new CvPoint(faceRect.x()*SCALE, faceRect.y()*SCALE),
            //    new CvPoint((faceRect.x()+faceRect.width())*SCALE,
            //        (faceRect.y()+faceRect.height())*SCALE),
            //    CvScalar.MAGENTA, 2, CV_AA, 0);
            // Snapshot
            //cvSaveImage("eye.jpg", grabbedImage);
            //////////////
            
            // If the right and left eye are the same one
            // (if one rectangle contains the other)
            if(containsRect(lastLeftRect, lastRightRect) || 
                    containsRect(lastRightRect, lastLeftRect)) {
                // then only one eye has been found (the other must be closed)
                
                // Make sure previous eyes were initialized
                if(previousRightRect == null || previousLeftRect == null) {
                    return;
                }
                // If the eye is closer to the previous right one
                if(Math.abs(lastLeftRect.x() - previousRightRect.x()) < 
                        Math.abs(lastLeftRect.x() - previousLeftRect.x())) {
                    // then the left eye must have closed, call reactors
                    shouldReact();
                }
                else {
                    // the right eye must have closed, false alarm
                    validityCount = 0; // Reset validity
                }
            }
            else {
                // both eyes are open
                // Mark them as previous eyes
                previousLeftRect = lastLeftRect;
                previousRightRect = lastRightRect;
                validityCount = 0; // Reset validity
            }
        }
    }
    
    public void shouldReact() {
        if(validityCount++ == VALIDITY) {
            callReactors();
            validityCount = 0; // Reset validity
        }
        // DEBUG LINES
        System.out.println("Left Eye: " + validityCount);
        //////////////
    }
    
}
