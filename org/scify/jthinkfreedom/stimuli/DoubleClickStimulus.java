package org.scify.jthinkfreedom.stimuli;

import java.awt.event.MouseEvent;
import org.scify.jthinkfreedom.sensors.ISensor;

/**
 *
 * @author nikos
 */
public class DoubleClickStimulus extends StimulusAdapter<MouseEvent> {
    //in milliseconds
    private int doubleClickThreshold;
    private long firstClick = 0;
    private int assignedButton;

    @Override
    public void onDataReceived() {
        if (lSensors.isEmpty()) 
        {
            return;
        }
        
        for (ISensor<MouseEvent> isCurSensor : lSensors) {
            
            MouseEvent mouseEvent = isCurSensor.getData();
            
            if(mouseEvent.getButton() == assignedButton /* && mouseEvent == MouseEvent.Mouse_Clicked*/ ){
                if(firstClick!=0 && (System.currentTimeMillis() - firstClick) <= doubleClickThreshold){
                    callReactors();
                    firstClick = 0;
                }else{
                    firstClick = System.currentTimeMillis();
                }
            }
        }
    }

    public int getAssignedButton() {
        return assignedButton;
    }

    public void setAssignedButton(int button) {
        this.assignedButton = button;
    }

    public int getDoubleClickThreshold() {
        return doubleClickThreshold;
    }

    public void setDoubleClickThreshold(int doubleClickThreshold) {
        this.doubleClickThreshold = doubleClickThreshold;
    }
}
