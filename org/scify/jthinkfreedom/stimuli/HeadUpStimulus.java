/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_objdetect;
import java.io.File;
import java.io.IOException;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import java.util.Date;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.stimuli.haarModels.HaarCascadeModel;

/**
 *
 * @author ggianna
 */
public class HeadUpStimulus extends StimulusAdapter<IplImage> {

    protected opencv_objdetect.CvHaarClassifierCascade classifier = null;
    protected opencv_core.IplImage grabbedImage = null, grayImage = null, smallImage = null;
    protected opencv_core.CvSeq faces = null;
    protected Exception exception = null;
    protected int SensitivityCount = 5; // Frames before reaction
    protected int TriggerOffset = 5;
    protected int divider = 2;
    private int iCurSensitivity = SensitivityCount;
    private int iFaceCenterX, iFaceCenterY, iPrvCenterX = -1, iPrvCenterY = -1;
    private long lastUpdate = 0;
    protected opencv_core.CvMemStorage storage = null;

    public HeadUpStimulus() {
        super();

        initClassifier();
    }

    public void setTriggerOffset(int TriggerOffset) {
        this.TriggerOffset = TriggerOffset;
    }

    public void setSensitivityCount(int SensitivityCount) {
        this.SensitivityCount = SensitivityCount;
        iCurSensitivity = SensitivityCount; // Reset current sensitivity count
    }

    public int getSensitivityCount() {
        return SensitivityCount;
    }

    public void setDivider(int divider) {
        this.divider = divider;
    }

    public int getDivider() {
        return divider;
    }

    public opencv_core.IplImage getGrayImage() {
        return grayImage;
    }

    public opencv_core.IplImage getSmallImage() {
        return smallImage;
    }

    public opencv_core.IplImage getGrabbedImage() {
        return grabbedImage;
    }

    protected void initClassifier() {
        try {
            // Load the classifier file from Java resources.
            String classiferName = "haarcascade_frontalface_alt.xml";
            //File classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");
            File classifierFile = new File(HaarCascadeModel.class.getResource(classiferName).toURI());
            if (classifierFile == null || classifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + classiferName + "\" from Java resources.");
            }

            // Preload the opencv_objdetect module to work around a known bug.
            Loader.load(opencv_objdetect.class);
            classifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
            //classifierFile.delete();
            if (classifier.isNull()) {
                throw new IOException("Could not load the classifier file.");
            }

            storage = opencv_core.CvMemStorage.create();
        } catch (Exception e) {
            if (exception == null) {
                exception = e;
            }
        }

    }

    @Override
    public void onDataReceived() {
        // If no source
        if (lSensors.isEmpty()) // Return
        {
            return;
        }

        // For each source
        for (ISensor<IplImage> isCurSensor : lSensors) {
            
            // Once every 1/10sec
            if (new Date().getTime() - lastUpdate < 100)
                return;
            lastUpdate = new Date().getTime();
            
            // Get latest data from sensor
            grabbedImage = isCurSensor.getData();
            grayImage = opencv_core.IplImage.create(grabbedImage.width(), grabbedImage.height(), IPL_DEPTH_8U, 1);
            smallImage = opencv_core.IplImage.create(grabbedImage.width() / divider, grabbedImage.height() / divider, IPL_DEPTH_8U, 1);

//                lastUpdate = curUpdate;
            cvClearMemStorage(storage);
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            cvResize(grayImage, smallImage, CV_INTER_AREA);
            faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
            for (int i = 0; i < faces.total(); i++) {
                CvRect r = new CvRect(cvGetSeqElem(faces, i));
//                cvRectangle(grabbedImage,
//                        cvPoint(r.x() * divider, r.y() * divider), cvPoint(r.x() * divider + r.width() * divider,
//                        r.y() * divider + r.height() * divider), CvScalar.RED, 1, CV_AA, 0);
                // Detect center
                iFaceCenterX = (r.x() + r.width()) + r.x() * divider;
                iFaceCenterY = (r.y() + r.height()) + r.y() * divider;

                shouldReact(iFaceCenterX, iFaceCenterY);
            }
        }
    }

    protected void shouldReact(int iCurX, int iCurY) {
        if (iPrvCenterY == -1) {
            iPrvCenterY = iCurY;
            return;
        }
        if (iPrvCenterY - iCurY > 10.0) {
            if (iCurSensitivity-- == 0) {
                iPrvCenterY = iCurY;
                callReactors();
                iCurSensitivity = SensitivityCount;
            }
            System.err.println("Head up: " + iCurSensitivity);
        } else // Reset counter and update center
        {
            iCurSensitivity = SensitivityCount;
            iPrvCenterY = iCurY;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        grabbedImage = grayImage = smallImage = null;

        super.finalize();
    }
}
