/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import static org.scify.jthinkfreedom.stimuli.HeadDirectionStimulus.prevNoseCenter;

/**
 *
 * @author eustratiadis-hua
 */
public class HeadUpStimulus extends HeadDirectionStimulus {
    
    public HeadUpStimulus() {
        super();
    }
    
    @Override
    protected String whichWayHeadWent() {
        // If cursor is already going down
        if(lock[DOWN]) {
            return "Down is Locked";
        }
        // Find the center of the two eye centers
        CvPoint totalCenter = new CvPoint(
                (prevLeftEyeCenter.x() + prevRightEyeCenter.x())/2,
                (prevLeftEyeCenter.y() + prevRightEyeCenter.y())/2);
        
        // If the distance of the current nose's rectangle's center from the eyes
        // is less than the last one, then the head moved up
        if(Math.abs(curNoseCenter.y() - totalCenter.y()) <
                Math.abs(prevNoseCenter.y() - totalCenter.y())) {
            // Lock direction
            lock[UP] = true;
            // call reactors
            shouldReact();
            return "Head Moved Up!";
        }
        else {
            // Unlock direction
            lock[UP] = false;
            return "Head Stopped Moving Up.";
        }
    }
}
