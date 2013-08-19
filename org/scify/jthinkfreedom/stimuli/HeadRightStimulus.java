/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

/**
 *
 * @author eustratiadis-hua
 */
public class HeadRightStimulus extends HeadDirectionStimulus {
    
    public HeadRightStimulus() {
        super();
    }
    
    @Override
    protected String whichWayHeadWent() {
        // If cursor is already going left
        if(lock[LEFT]) {
            return "Left is Locked";
        }
        // If the distance of the current nose's rectangle's center
        // from the right eye is less than the last one
        // then the head moved right
        if(Math.abs(curNoseCenter.x() - prevLeftEyeCenter.x()) >
                Math.abs(curNoseCenter.x() - prevRightEyeCenter.x()) + RECT_OFFSET/2) {
            // Lock direction
            lock[RIGHT] = true;
            // call reactors
            shouldReact();
            return "Head Moved Right!";
        }
        else {
            // Unlock direction
            lock[RIGHT] = false;
            return "Head Stopped Moving Right.";
        }
    }
}
