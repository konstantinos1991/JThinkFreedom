package samples;

import org.scify.jthinkfreedom.reactors.ApplicationLauncherReactor;
import org.scify.jthinkfreedom.sensors.MouseMotionSensor;
import org.scify.jthinkfreedom.stimuli.MouseClickStimulus;

/**
 *
 * @author nikos
 */
public class MouseClickWorkingSample {

    public static void main(String[] args) {
    	MouseMotionSensor mouseSensor = new MouseMotionSensor();
		MouseClickStimulus clickStimulus = new MouseClickStimulus(1);
		ApplicationLauncherReactor appLaunch = new ApplicationLauncherReactor("/usr/bin/gnome-terminal");
		
		mouseSensor.addStimulus(clickStimulus);
		clickStimulus.addSensor(mouseSensor);
		clickStimulus.addReactor(appLaunch);
    }
}