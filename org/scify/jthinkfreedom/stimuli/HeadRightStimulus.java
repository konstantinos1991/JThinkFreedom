/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import static org.scify.jthinkfreedom.stimuli.HeadMovementStimulus.noseRect;

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
        // If nose
        if(noseRect.x() > previousNoseRect.x()) {
            // then call reactors
            shouldReact();
            return "Head Moved Right!";
        }
        return "";
    }
}
