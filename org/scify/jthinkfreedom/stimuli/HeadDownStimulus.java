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
public class HeadDownStimulus extends HeadDirectionStimulus {
    
    public HeadDownStimulus() {
        super();
    }
    
    @Override
    protected String whichWayHeadWent() {
        // If cursor is already going up
        if(lock[UP]) {
            return "Up is Locked";
        }
        // Find the center of the two eye centers
        CvPoint totalCenter = new CvPoint(
                (prevLeftEyeCenter.x() + prevRightEyeCenter.x())/2,
                (prevLeftEyeCenter.y() + prevRightEyeCenter.y())/2);
        
        // If the distance of the current nose's rectangle's center from the eyes
        // is greater than the last one, then the head moved down
        if(Math.abs(curNoseCenter.y() - totalCenter.y()) >
                Math.abs(prevNoseCenter.y() - totalCenter.y()) + RECT_OFFSET/2) {
            // Lock direction
            lock[DOWN] = true;
            // call reactors
            shouldReact();
            return "Head Moved Down!";
        }
        else {
            // Unlock direction
            lock[DOWN] = false;
            return "Head Stopped Moving Down.";
        }
    }
    
}
