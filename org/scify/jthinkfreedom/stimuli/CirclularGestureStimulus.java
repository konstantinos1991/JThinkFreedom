/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.leapmotion.leap.Gesture;

/**
 *
 * @author alexisz
 */
public class CirclularGestureStimulus extends LeapMotionStimulus {

    public CirclularGestureStimulus() {
        super();
    }

    @Override
    protected boolean shouldReact(Gesture g) {
        return (g.type() == Gesture.Type.TYPE_CIRCLE);
         
    }
}
