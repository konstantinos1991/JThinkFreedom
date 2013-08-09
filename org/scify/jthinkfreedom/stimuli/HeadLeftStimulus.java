/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;

/**
 *
 * @author eustratiadis-hua
 */
public class HeadLeftStimulus extends HeadDirectionStimulus {
    
    public HeadLeftStimulus() {
        super();
    }
    
    @Override
    protected String whichWayHeadWent() {
        // If cursor is already going right
        if(lock[RIGHT]) {
            return "Right is Locked";
        }
        // If the distance of the current nose's rectangle's center
        // from the left eye is less than the last one
        // then the head moved left
        if(Math.abs(curNoseCenter.x() - prevLeftEyeCenter.x()) <
                Math.abs(curNoseCenter.x() - prevRightEyeCenter.x())) {
            // Lock direction
            lock[LEFT] = true;
            // call reactors
            shouldReact();
            return "Head Moved Left!";
        }
        else {
            // Unlock direction
            lock[LEFT] = false;
            return "Head Stopped Moving Left.";
        }
    }
}
