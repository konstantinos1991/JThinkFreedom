package org.scify.jthinkfreedom.stimuli;

import java.util.LinkedList;
import java.util.List;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.reactors.IReactor;

/**
 *
 * @author ggianna
 * @param <T>
 */
public abstract class StimulusAdapter<T> implements IStimulus<T> {

    protected List<IReactor> lReactors = new LinkedList<>();
    protected List<ISensor<T>> lSensors = new LinkedList<>();
    protected boolean bRunning;

    @Override
    public void addReactor(IReactor rToUse) {
        lReactors.add(rToUse);
    }

    @Override
    public void addSensor(ISensor sToUse) {
        lSensors.add(sToUse);
    }

    @Override
    public void clearReactors() {
        lReactors.clear();
    }

    @Override
    public void clearSensors() {
        lSensors.clear();
    }

    @Override
    public List<IReactor> listReactors() {
        return new LinkedList<>(lReactors);
    }

    @Override
    public List<ISensor<T>> listSensors() {
        return new LinkedList<>(lSensors);
    }

    @Override
    public boolean removeReactor(IReactor rToRemove) {
        return lReactors.remove(rToRemove);
    }

    @Override
    public boolean removeSensor(ISensor sToRemove) {
        return lSensors.remove(sToRemove);
    }

    /**
     * Should be overridden by offspring.
     */
    @Override
    public abstract void onDataReceived();

    @Override
    public void start() {
        bRunning = true;
    }

    @Override
    public void stop() {
        bRunning = false;
    }

    @Override
    public void callReactors() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                for (IReactor iCur : lReactors) {
                    iCur.react();
                }
            }
        });
        t.start();
    }

}
