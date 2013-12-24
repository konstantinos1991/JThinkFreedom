package org.scify.jthinkfreedom.examples;

import org.scify.jthinkfreedom.sensors.SensorAdapter;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseSensor extends SensorAdapter<java.awt.PointerInfo> implements Runnable {

    private Thread tDataReader;

    public MouseSensor() {
        super();
    }

    @Override
    public java.awt.PointerInfo getData() {
        return java.awt.MouseInfo.getPointerInfo();
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
