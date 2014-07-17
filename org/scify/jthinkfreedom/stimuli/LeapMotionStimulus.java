/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.scify.jthinkfreedom.sensors.ISensor;

/**
 *
 * @author alexisz
 */
public abstract class LeapMotionStimulus extends StimulusAdapter<Frame> {

    protected Frame info = null;
    
    protected LeapMotionStimulus() {
        super();
    }
    
    @Override
    public void onDataReceived() {
        if (lSensors.isEmpty()) {
            return;
        }
        for (ISensor<Frame> isCurSensor : lSensors) {
            info = isCurSensor.getData();
            for(Gesture g:info.gestures())
            {
                if(shouldReact(g))
                {
                    callReactors();
                    try {
                        Thread.sleep(500); //for avoiding multiple triggers
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LeapMotionStimulus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    protected abstract boolean shouldReact(Gesture g);
}

   
