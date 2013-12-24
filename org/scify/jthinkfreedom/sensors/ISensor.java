package org.scify.jthinkfreedom.sensors;

import org.scify.jthinkfreedom.stimuli.IStimulus;

/**
 *
 * @author ggianna
 * @param <T>
 */
public interface ISensor<T> {

    /**
     * Returns the last read data from the sensor. If data were not previously
     * available, new data should be acquired.
     *
     * @return The last available data.
     */
    public T getData();

    /**
     * Adds a stimulus to the list of stimuli to inform when new data is made
     * available.
     *
     * @param sToAdd The stimulus.
     */
    public void addStimulus(IStimulus sToAdd);

    /**
     * Removes a stimulus from the list of stimuli to inform when new data are
     * made available.
     *
     * @param iToRemove
     * @return
     */
    public boolean removeStimulus(IStimulus iToRemove);

    /**
     * Clears the stimuli list, i.e. no stimulus will be made aware when new
     * data are available.
     */
    public void clearStimuli();

    /**
     * Start gathering data. This implies a possibly asynchronous process of
     * data gathering. If data are asynchronously gathered, then the derived
     * classes should call onDataAvailable method of the registered stimuli,
     * when data are available.
     */
    public void start();

    /**
     * Stop gathering data. Any call to getData after the call of stop should
     * return null data.
     */
    public void stop();

    /**
     * Indicates whether the sensor is running. If it is not, data returned from
     * calls to getData should be null.
     *
     * @return True if the sensor is running.
     */
    public boolean isRunning();
}
