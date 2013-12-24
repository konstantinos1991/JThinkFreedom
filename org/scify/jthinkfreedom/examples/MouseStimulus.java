package org.scify.jthinkfreedom.examples;

import java.util.Date;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.stimuli.StimulusAdapter;

/**
 *
 * @author eustratiadis-hua
 */
public class MouseStimulus extends StimulusAdapter<java.awt.PointerInfo> {

    protected java.awt.PointerInfo info = null;
    protected java.awt.PointerInfo lastInfo = null;
    private long lastUpdate = 0;

    public MouseStimulus() {
        super();
    }

    protected boolean shouldReact(java.awt.PointerInfo piOld, java.awt.PointerInfo piNew) {
        return piNew.getLocation().getX() < piOld.getLocation().getX();
    }

    @Override
    public void onDataReceived() {
        if (lSensors.isEmpty()) {
            return;
        }

        // Refresh rate 200ms
        if (new Date().getTime() - lastUpdate < 200) {
            return;
        }

        // TODO: Really implement for many sensors
        // For every sensor
        for (ISensor<java.awt.PointerInfo> isCurSensor : lSensors) {
            info = isCurSensor.getData();
            // If this is the first time we run
            if (lastInfo == null) {
                lastInfo = info; // Update last value
                continue; // And go on
            }
            // Otherwise
            // If we should react
            if (shouldReact(lastInfo, info)) {
                callReactors();
            }
            // Also update last info
            lastInfo = info;
        }

        // Update for refresh rate
        lastUpdate = new Date().getTime();
    }
}
