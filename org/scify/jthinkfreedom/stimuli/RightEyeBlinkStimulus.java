package org.scify.jthinkfreedom.stimuli;

import org.scify.jthinkfreedom.machineLearning.EyeClassifier;

/**
 *
 * @author eustratiadis-hua
 */
public class RightEyeBlinkStimulus extends HeadMovementStimulus {

    // Mechanism to determine if an eye is open or closed
    protected EyeClassifier ecl = null;
    // Eye status
    protected String eyeStatus = null;
    
    public RightEyeBlinkStimulus() {
        super();
        // Initialize the eye classifier
        ecl = new EyeClassifier("res");
    }

    @Override
    public void onDataReceived() {
        super.onDataReceived();
        
        // Determine if the right eye is open or closed
        eyeStatus = ecl.predictEyeTypeOfIplImage(rightEyeImage);
        
        // DEBUG LINES
        System.out.println("Right Eye Status: "+eyeStatus);
        //////////////
    }
}
