package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.util.Date;
import org.scify.jthinkfreedom.sensors.ISensor;

/**
 *
 * @author nikos
 */
public class EyeBlinkStimulus extends StimulusAdapter<IplImage> {
    
    private long lastUpdate = 0;

    @Override
    public void onDataReceived() {
        
        if (lSensors.isEmpty()) 
        {
            return;
        }
        
         if (new Date().getTime() - lastUpdate < 100)
            return;
        lastUpdate = new Date().getTime();
        
        for (ISensor<IplImage> isCurSensor : lSensors) {
            
        }
    }
}
