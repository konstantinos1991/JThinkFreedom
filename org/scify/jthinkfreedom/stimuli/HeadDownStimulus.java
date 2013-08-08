/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

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
        // If nose
        if(noseRect.y() > previousNoseRect.y()) {
            // then call reactors
            shouldReact();
            return "Head Moved Down!";
        }
        return "";
    }
    
}
