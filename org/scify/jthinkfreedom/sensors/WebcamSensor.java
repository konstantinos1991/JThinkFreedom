package org.scify.jthinkfreedom.sensors;

import com.googlecode.javacv.FrameGrabber;

/**
 *
 * @author ggianna
 */
public class WebcamSensor extends NetworkImageSensor {

    protected int CameraNo = 0;

    public WebcamSensor(int iCamera) {
        super();
        this.CameraNo = iCamera;
    }

    @Override
    public void start() {
        try {
            grabber = FrameGrabber.createDefault(CameraNo);
            grabber.setImageWidth(getWidth());
            grabber.setImageHeight(getHeight());
            grabber.start();
            // Running
            this.bRunning = true;
            // Init and start reader thread
            tDataReader = new Thread(this);
            tDataReader.start();
        } catch (java.lang.Exception e) {
            // Ignore
            e.printStackTrace(System.err);
        }
    }

}
