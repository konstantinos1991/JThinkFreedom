/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawRect;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import java.util.Date;
import org.scify.jthinkfreedom.sensors.ISensor;

/**
 *
 * @author eustratiadis-hua
 */

public class LeftEyeBlinkStimulus extends EyeBlinkStimulus{
    
    public LeftEyeBlinkStimulus() {
        super();
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
            
            // Get rightmost and leftmost eyes
            lastLeftRect = getLeftmostEye();
            lastRightRect = getRightmostEye();
            
            // DEBUG LINES
            // Draw a green rectangle around left eye
            cvDrawRect(grabbedImage,
                new CvPoint(lastLeftRect.x()*SCALE, lastLeftRect.y()*SCALE),
                new CvPoint((lastLeftRect.x()+lastLeftRect.width())*SCALE,
                    (lastLeftRect.y()+lastLeftRect.height())*SCALE),
                CvScalar.GREEN, 2, CV_AA, 0);
            // Draw a red rectangle around right eye
            cvDrawRect(grabbedImage,
                new CvPoint(lastRightRect.x()*SCALE, lastRightRect.y()*SCALE),
                new CvPoint((lastRightRect.x()+lastRightRect.width())*SCALE,
                    (lastRightRect.y()+lastRightRect.height())*SCALE),
                CvScalar.RED, 2, CV_AA, 0);
            cvSaveImage("eye.jpg", grabbedImage);
            //////////////
        }
    }
}
