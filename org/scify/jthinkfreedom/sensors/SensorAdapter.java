package org.scify.jthinkfreedom.sensors;

import java.util.LinkedList;
import org.scify.jthinkfreedom.stimuli.IStimulus;

/**
 *
 * @author ggianna
 * @param <T>
 */
public abstract class SensorAdapter<T> implements ISensor<T> {

    protected LinkedList<IStimulus> ilStimuli = new LinkedList<>();
    protected boolean bRunning = false;

    @Override
    public void addStimulus(IStimulus iToAdd) {
        synchronized (ilStimuli) {
            ilStimuli.add(iToAdd);
        }
    }

    @Override
    public void clearStimuli() {
        synchronized (ilStimuli) {
            ilStimuli.clear();
        }
    }

    @Override
    public boolean removeStimulus(IStimulus iToRemove) {
        synchronized (ilStimuli) {
            return ilStimuli.remove(iToRemove);
        }
    }

    @Override
    public void start() {
        bRunning = true;
    }

    @Override
    public void stop() {
        bRunning = false;
    }

    @Override
    public boolean isRunning() {
        return bRunning;
    }

    /**
     * Updates all stimuli connected to this sensor that data has been received.
     */
    protected void updateStimuli() {
        synchronized (ilStimuli) {
            for (IStimulus<T> sCur : ilStimuli) {
                sCur.onDataReceived();
            }
        }
    }
}
