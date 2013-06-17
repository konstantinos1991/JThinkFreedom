package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacv.cpp.opencv_core;
import java.awt.event.MouseEvent;
import java.util.List;
import org.scify.jthinkfreedom.reactors.IReactor;
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
