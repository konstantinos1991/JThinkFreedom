/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.scify.jthinkfreedom;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author ggianna
 */
public class MotionDemo {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws com.googlecode.javacv.FrameGrabber.Exception {
        // Start grabber
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        opencv_core.IplImage frame = grabber.grab();
        opencv_core.IplImage image = null;
        opencv_core.IplImage prevImage = null;
        opencv_core.IplImage diff = null;

        CanvasFrame canvasFrame = new CanvasFrame("Face detection");
        canvasFrame.setCanvasSize(frame.width(), frame.height());

        opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

        while (canvasFrame.isVisible() && (frame = grabber.grab()) != null) {
            cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
            if (image == null) {
                image = opencv_core.IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                cvCvtColor(frame, image, CV_RGB2GRAY);
            } else {
                prevImage = opencv_core.IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                prevImage = image;
                image = opencv_core.IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                cvCvtColor(frame, image, CV_RGB2GRAY);
            }

            if (diff == null) {
                diff = opencv_core.IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
            }

            if (prevImage != null) {
                // Perform ABS difference
                cvAbsDiff(image, prevImage, diff);
                // Do some threshold for wipe away useless details
                cvThreshold(diff, diff, 64, 255, CV_THRESH_BINARY);

                canvasFrame.showImage(diff);

                // Recognize contours
                opencv_core.CvSeq contour = new opencv_core.CvSeq(null);
                cvFindContours(diff, storage, contour, Loader.sizeof(opencv_core.CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

                while (contour != null && !contour.isNull()) {
                    if (contour.elem_size() > 0) {
                        opencv_core.CvBox2D box = cvMinAreaRect2(contour, storage);
                        // Test intersection
                        if (box != null) {
                            opencv_core.CvPoint2D32f center = box.center();
                            opencv_core.CvSize2D32f size = box.size();

                        }
                    }
                    contour = contour.h_next();
                }
            }
        }
        grabber.stop();
        canvasFrame.dispose();
    }

    
}
