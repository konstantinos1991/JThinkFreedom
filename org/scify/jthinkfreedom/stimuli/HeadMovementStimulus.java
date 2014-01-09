package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacpp.Loader;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawRect;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import com.googlecode.javacv.cpp.opencv_objdetect;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.stimuli.haarModels.HaarCascadeModel;

/**
 *
 * @author eustratiadis-hua
 */
public class HeadMovementStimulus extends StimulusAdapter<IplImage> {

    // Declare classifiers for face elements
    protected opencv_objdetect.CvHaarClassifierCascade eyeClassifier = null, faceClassifier = null;
    protected IplImage grabbedImage = null;
    // For the face
    protected IplImage faceImage = null;
    protected CvSeq facesDetected = null;
    protected CvRect faceRect = null;
    // For the eyes
    protected IplImage leftEyeImage = null, rightEyeImage = null;
    protected CvSeq eyesDetected = null;
    protected CvRect leftEyeRect = null, rightEyeRect = null;

    // Each subclass should declare their own storage, grayImage, and smallImage
    private CvMemStorage storage = null;
    private IplImage grayImage = null, smallImage = null;

    public HeadMovementStimulus() {
        super();
        initClassifier(); // For eye and face detection
    }

    public IplImage getGrabbedImage() {
        return grabbedImage;
    }

    public IplImage getFaceImage() {
        return faceImage;
    }

    private void initClassifier() {
        try {
            // Preload the opencv_objdetect module to work around a known bug.
            Loader.load(opencv_objdetect.class);

            // Load the classifier files from Java resources.
            String openClassifierName = "haarcascade_eye.xml";
            String faceClassifierName = "haarcascade_frontalface_alt.xml";

            File openClassifierFile = new File(HaarCascadeModel.class.getResource(openClassifierName).toURI());
            File faceClassifierFile = new File(HaarCascadeModel.class.getResource(faceClassifierName).toURI());

            if (openClassifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + openClassifierName + "\" from Java resources.");
            }
            if (faceClassifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + faceClassifierName + "\" from Java resources.");
            }

            eyeClassifier = new CvHaarClassifierCascade(cvLoad(openClassifierFile.getAbsolutePath()));
            faceClassifier = new CvHaarClassifierCascade(cvLoad(faceClassifierFile.getAbsolutePath()));

            if (eyeClassifier.isNull() || faceClassifier.isNull()) {
                throw new IOException("Could not load the classifier files.");
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace(System.err);
        }

    }

    @Override
    public void onDataReceived() {

        for (ISensor<IplImage> isCurSensor : lSensors) {
            // Get latest data from sensor
            grabbedImage = isCurSensor.getData();

            // Detect all faces in current frame
            facesDetected = detectFaces(grabbedImage);
            // If no faces were found, terminate
            if (facesDetected.total() == 0) {
                return;
            }
            // Get most central face
            faceRect = getCentralRectangle(facesDetected);

            // If a face was found and has been initialized properly
            if (faceRect != null && faceRect.width() > 0 && faceRect.height() > 0) {
                // Set region of interest (the face)
                cvSetImageROI(grabbedImage, faceRect);
                faceImage = cvCreateImage(cvGetSize(grabbedImage),
                        grabbedImage.depth(),
                        grabbedImage.nChannels());
                cvCopy(grabbedImage, faceImage, null);

                // Reset region of interest
                cvResetImageROI(grabbedImage);
            }

            // Now detect the eyes
            eyesDetected = detectEyes(faceImage);
            // If no eyes were found, terminate
            if (eyesDetected.total() == 0) {
                return;
            }
            // Get leftmost eye
            leftEyeRect = getLeftmostRectangle(eyesDetected);
            // Get rightmost eye
            rightEyeRect = getRightmostRectangle(eyesDetected);

            // If eyes were found and have been initialized properly
            if (leftEyeRect != null && rightEyeRect != null
                    && leftEyeRect.width() > 0 && leftEyeRect.height() > 0
                    && rightEyeRect.width() > 0 && rightEyeRect.height() > 0) {
                // Set region of interest (the left eye)
                cvSetImageROI(faceImage, leftEyeRect);
                leftEyeImage = cvCreateImage(cvGetSize(faceImage),
                        faceImage.depth(),
                        faceImage.nChannels());
                cvCopy(faceImage, leftEyeImage, null);

                // Reset region of interest
                cvResetImageROI(faceImage);

                // Set region of interest (the right eye)
                cvSetImageROI(faceImage, rightEyeRect);
                rightEyeImage = cvCreateImage(cvGetSize(faceImage),
                        faceImage.depth(),
                        faceImage.nChannels());
                cvCopy(faceImage, rightEyeImage, null);

                // Reset region of interest
                cvResetImageROI(faceImage);
            }

            // Makes system slow - Only to be called when debugging
//            drawTrackingData();
        }

    }

    // Returns a sequence of faces in the specified image
    protected CvSeq detectFaces(IplImage curImage) {
        grayImage = IplImage.create(cvGetSize(curImage), IPL_DEPTH_8U, 1);
        cvCvtColor(curImage, grayImage, CV_BGR2GRAY);

        smallImage = IplImage.create(curImage.width(),
                curImage.height(), IPL_DEPTH_8U, 1);

        cvResize(grayImage, smallImage, CV_INTER_LINEAR);

        // Equalize the small grayscale
        cvEqualizeHist(smallImage, smallImage);

        // Create temp storage, used during object detection
        storage = CvMemStorage.create();

        // Determine whether faces have been found
        CvSeq faces = cvHaarDetectObjects(smallImage, faceClassifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);
        return faces;
    }

    // Returns a sequence of eyes in the specified image
    protected CvSeq detectEyes(IplImage curImage) {
        grayImage = IplImage.create(cvGetSize(curImage), IPL_DEPTH_8U, 1);
        cvCvtColor(curImage, grayImage, CV_BGR2GRAY);

        smallImage = IplImage.create(curImage.width(),
                curImage.height(), IPL_DEPTH_8U, 1);

        cvResize(grayImage, smallImage, CV_INTER_LINEAR);

        // Equalize the small grayscale
        cvEqualizeHist(smallImage, smallImage);

        // Create temp storage, used during object detection
        storage = CvMemStorage.create();

        // Determine whether eyes have been found
        CvSeq eyes = cvHaarDetectObjects(smallImage, eyeClassifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);
        return eyes;
    }

    // Returns the most central rectangle in the sequence
    protected CvRect getCentralRectangle(CvSeq sequence) {
        CvRect rect = new CvRect(0, 0, 0, 0);
        CvPoint rectCenter = new CvPoint(0, 0);

        // For every rectangle detected
        for (int i = 0; i < sequence.total(); i++) {
            CvRect r = new CvRect(cvGetSeqElem(sequence, i));
            // Get the center of the current rectangle
            CvPoint curCenter = getRectangleCenter(r);
            // If current rectangle is closer to the middle of the screen
            if (Math.abs(curCenter.x() - getGrabbedImage().width() / 2)
                    < Math.abs(rectCenter.x() - getGrabbedImage().width() / 2)
                    && Math.abs(curCenter.y() - getGrabbedImage().height() / 2)
                    < Math.abs(rectCenter.y() - getGrabbedImage().height() / 2)) {
                // Make it our new central rectangle
                rect = r;
            }
        }
        return rect;
    }

    // Returns the leftmost rectangle in the sequence
    protected CvRect getLeftmostRectangle(CvSeq sequence) {
        // Initialize last left rectangle at position 0x0
        CvRect left = new CvRect(0, 0, 0, 0);

        // For every rectangle detected
        for (int i = 0; i < sequence.total(); i++) {
            CvRect r = new CvRect(cvGetSeqElem(eyesDetected, i));
            // If current rectangle is at the left of the previous one (mirrored)
            if (r.x() > left.x()) {
                left = r; // Make it our new current left rectangle
            }
        }

        return left;
    }

    // Returns the rightmost rectangle in the sequence
    protected CvRect getRightmostRectangle(CvSeq sequence) {
        // Initialize last right rectangle at position IMG_WIDTHxIMG_HEIGHT
        CvRect right = new CvRect(grabbedImage.width(), grabbedImage.height(), 0, 0);

        // For every rectangle detected
        for (int i = 0; i < sequence.total(); i++) {
            CvRect r = new CvRect(cvGetSeqElem(eyesDetected, i));
            // If current rectangle is at the right of the previous one (mirrored)
            if (r.x() < right.x()) {
                right = r; // Make it our new current right rectangle
            }
        }

        return right;
    }

    protected void drawTrackingData() {

        // Draw a green rectangle around the left eye
        if (leftEyeRect != null) {
            cvDrawRect(faceImage,
                    new CvPoint(leftEyeRect.x(), leftEyeRect.y()),
                    new CvPoint((leftEyeRect.x() + leftEyeRect.width()),
                            (leftEyeRect.y() + leftEyeRect.height())),
                    CvScalar.GREEN, 2, CV_AA, 0);
        }
        // Draw a red rectangle around the right eye
        if (rightEyeRect != null) {
            cvDrawRect(faceImage,
                    new CvPoint(rightEyeRect.x(), rightEyeRect.y()),
                    new CvPoint((rightEyeRect.x() + rightEyeRect.width()),
                            (rightEyeRect.y() + rightEyeRect.height())),
                    CvScalar.RED, 2, CV_AA, 0);
        }

        // Snapshot
        cvSaveImage("tracked.jpg", faceImage);
    }

    // Return the central point of a rectangle
    protected CvPoint getRectangleCenter(CvRect r) {
        return new CvPoint(r.x() + (r.x() + r.width()) / 2,
                r.y() + (r.y() + r.height()) / 2);
    }

}
