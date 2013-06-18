/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import java.util.LinkedList;
import java.util.List;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.reactors.IReactor;

/**
 *
 * @author ggianna
 */
public abstract class StimulusAdapter<T> implements IStimulus<T> {
    List<IReactor> lReactors = new LinkedList<IReactor>();
    List<ISensor<T>> lSensors = new LinkedList<ISensor<T>>();
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
        return new LinkedList<IReactor>(lReactors);
    }

    @Override
    public List<ISensor<T>> listSensors() {
        return new LinkedList<ISensor<T>>(lSensors);
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
    public void onDataReceived() {
        
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
    public void callReactors() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                for (IReactor iCur : lReactors)
                    iCur.react();
            }
        });
        t.start();
    }

    
}
