/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

/**
 *
 * @author eustratiadis-hua
 */

public class LeftEyeBlinkStimulus extends EyeBlinkStimulus {
    
    public LeftEyeBlinkStimulus() {
        super();
    }
    
    @Override
    protected String whichEyeBlinked() {
        // If the eye is closer to the previous right one
        if(Math.abs(lastLeftRect.x() - previousRightRect.x()) < 
                Math.abs(lastLeftRect.x() - previousLeftRect.x())) {
            // then the left eye must have closed, call reactors
            shouldReact();
            return "Left Eye Blinked!";
        }
        else {
            // the right eye must have closed, false alarm
            validityCount = 0; // Reset validity
            return "";
        }
    }
    
}
