/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.sensors;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;

/**
 *
 * @author alexisz
 */
public class LeapMotionSensor extends SensorAdapter<Frame> implements Runnable{

    private Thread tDataReader;
    private Controller controller;
    
    public LeapMotionSensor() {
        super();
        controller = new Controller();
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        //controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }
    
    @Override
    public Frame getData() {
        
        return controller.frame();
       
    }
@Override
    public void start() {
        this.bRunning = true;
        tDataReader = new Thread(this);
        tDataReader.start();
    }

    @Override
    public void stop() {
        this.bRunning = false;
    }

    @Override
    public void run() {
        while (isRunning()) {
            updateStimuli(); //calls getData from sensors
            Thread.yield();
        }
    }
}
