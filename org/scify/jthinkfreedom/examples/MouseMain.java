/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.examples;

import java.util.Date;
import org.scify.jthinkfreedom.sensors.ISensor;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseMain {
    public static void main(String[] saArgs) {
        //create a sensor that detects where the mouse pointer is
        ISensor<java.awt.PointerInfo> mouse = new MouseSensor();
        mouse.start();
        
        //create stimuli
        //they call the reactors if the mouse pos has changed
        MouseUpStimulus up = new MouseUpStimulus();
        MouseDownStimulus down = new MouseDownStimulus();
        MouseLeftStimulus left = new MouseLeftStimulus();
        MouseRightStimulus right = new MouseRightStimulus();
        
        //connect stimuli to sensor
        mouse.addStimulus(up);
        mouse.addStimulus(down);
        mouse.addStimulus(left);
        mouse.addStimulus(right);
        
        //connect sensor to stimuli
        down.addSensor(mouse);
        up.addSensor(mouse);
        left.addSensor(mouse);
        right.addSensor(mouse);
        
        //add reactors to stimuli
        down.addReactor(new MouseDownReactor());
        up.addReactor(new MouseUpReactor());
        left.addReactor(new MouseLeftReactor());
        right.addReactor(new MouseRightReactor());
        
        Date dStart = new Date();
        while(true) {
            //run for 15 seconds
            if (new Date().getTime() - dStart.getTime() > 15000) {
                break;
            }
            Thread.yield();
        }
        
        mouse.stop();
    }
}
