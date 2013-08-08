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
public class HeadUpStimulus extends HeadDirectionStimulus {
    
    public HeadUpStimulus() {
        super();
    }
    
    @Override
    protected String whichWayHeadWent() {
        // If nose
        if(noseRect.y() < previousNoseRect.y()) {
            // then call reactors
            shouldReact();
            return "Head Moved Up!";
        }
        return "";
    }
}
