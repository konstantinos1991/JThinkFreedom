package org.scify.jthinkfreedom.stimuli;

import org.scify.jthinkfreedom.machineLearning.EyeClassifier;

/**
 *
 * @author eustratiadis-hua
 */
public class LeftEyeBlinkStimulus extends HeadMovementStimulus {

    // Mechanism to determine if an eye is open or closed
    protected EyeClassifier ecl = null;
    // Eye status
    protected String eyeStatus = null;
    
    public LeftEyeBlinkStimulus() {
        super();
        // Initialize the eye classifier
        ecl = new EyeClassifier("res");
    }

    @Override
    public void onDataReceived() {
        super.onDataReceived();
        
        // Determine if the left eye is open or closed
        eyeStatus = ecl.predictEyeTypeOfIplImage(leftEyeImage);
        
        // DEBUG LINES
        System.out.println("Left Eye Status: "+eyeStatus);
        //////////////
    }
}
