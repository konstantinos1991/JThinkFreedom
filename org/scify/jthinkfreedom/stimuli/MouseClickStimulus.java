package org.scify.jthinkfreedom.stimuli;

import java.awt.event.MouseEvent;
import org.scify.jthinkfreedom.sensors.ISensor;

/**
 * 
 * @author nikos
 */
public class MouseClickStimulus extends StimulusAdapter<MouseEvent> {
    
    private int assignedButton;
    

    /**
     * 
     */
    @Override
    public void onDataReceived() {

        if (lSensors.isEmpty()) // Return
        {
            return;
        }
        
        for (ISensor<MouseEvent> isCurSensor : lSensors) {
            
            MouseEvent mouseEvent = isCurSensor.getData();
            if(mouseEvent.getButton() == assignedButton /* && mouseEvent == MouseEvent.Mouse_Clicked*/ ){
                callReactors();
            }
        }
    }

    public int getAssignedButton() {
        return assignedButton;
    }

    public void setAssignedButton(int assignedButton) {
        this.assignedButton = assignedButton;
    }
}
