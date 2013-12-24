package org.scify.jthinkfreedom.stimuli;

/**
 *
 * @author eustratiadis-hua
 */
public class RightEyeBlinkStimulus extends EyeBlinkStimulus {

    public RightEyeBlinkStimulus() {
        super();
    }

    @Override
    protected String whichEyeBlinked() {
        // If the eye is closer to the previous left one
        if (Math.abs(lastLeftRect.x() - previousRightRect.x())
                > Math.abs(lastLeftRect.x() - previousLeftRect.x())) {
            // then the right eye must have closed, call reactors
            shouldReact();
            return "Right Eye Blinked!";
        } else {
            // the left eye must have closed, false alarm
            validityCount = 0; // Reset validity
            return "";
        }
    }

}
