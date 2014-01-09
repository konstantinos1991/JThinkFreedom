package org.scify.jthinkfreedom.stimuli;

import java.util.LinkedList;
import org.scify.jthinkfreedom.machineLearning.EyeClassifier;

/**
 *
 * @author eustratiadis-hua
 */
public class RightEyeBlinkStimulus extends HeadMovementStimulus {

    // Constant threshold for eye conclusion
    protected static final int THRESHOLD = 10;

    // Mechanism to determine if an eye is open or closed
    protected EyeClassifier ecl = null;
    // List to hold the images that decide if the eye is open or closed
    protected LinkedList<String> eyeList = null;
    // Counters for open and closed eyes
    protected int iClosed;
    // Eye status
    protected String eyeStatus = null;

    public RightEyeBlinkStimulus() {
        super();

        ecl = new EyeClassifier("res");
        eyeList = new LinkedList<>();
        iClosed = 0;
    }

    @Override
    public void onDataReceived() {
        super.onDataReceived();

        // Determine if the right eye is open or closed
        if (rightEyeImage != null) {
            // Add it to the queue
            eyeList.add(ecl.predictEyeTypeOfIplImage(rightEyeImage));
        }

        // If the queue has enough data to make conclusions
        if (eyeList.size() >= THRESHOLD) {
            // Iterate through the list
            for (String status : eyeList) {
                // Count closed eyes
                if (status.equals("CLOSED")) {
                    iClosed++;
                }
            }

            // If closed eye percentage is above 70%, react
            if ((double) iClosed / eyeList.size() >= 0.7) {
                System.out.println("Right eye blinked!");
            }

            // Empty the list and reset the counters
            eyeList.clear();
            iClosed = 0;
        }

    }
}
