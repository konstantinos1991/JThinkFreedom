package org.scify.jthinkfreedom.stimuli;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.cpp.opencv_objdetect;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;
import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.structs.ArrayGraph;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.scify.jthinkfreedom.sensors.ISensor;
import org.scify.jthinkfreedom.stimuli.haarModels.HaarCascadeModel;

/**
 *
 * @author ggianna
 */
public class LeftEyeClosedStimulus extends StimulusAdapter<opencv_core.IplImage> {

    protected opencv_objdetect.CvHaarClassifierCascade openClassifier = null;
    protected opencv_objdetect.CvHaarClassifierCascade closedClassifier = null;
    protected opencv_core.IplImage grabbedImage = null, grayImage = null, smallImage = null;
    protected opencv_core.CvSeq openeye = null;
    protected opencv_core.CvSeq closedeye = null;
    protected Exception exception = null;
    protected int SensitivityCount = 4; // Frames before reaction
    private int iCurSensitivity = SensitivityCount;
    protected double SignificantChangeLevel = 0.1;
    protected int divider = 1;
    private long lastUpdate = 0;
    protected int iFoundInLastFrames = 0;
    protected int iLastEyeSize = -1;
    protected CvRect lastLeftRect = null;
    
    protected DocumentNGramGraph dgLast = null;
    protected NGramCachedGraphComparator ngc = new NGramCachedGraphComparator();
    protected int iGraphLevels = 2;
    protected DocumentNGramGraph dgAvg = null;
    protected int iAvgGraphCount = 0;
    protected double dGraphSimThreshold = 0.85;
    protected int iTrainingPeriod = 20;
//    protected long ReactionInterval = 1000;
//    protected long lastReaction = 0;
    
    protected opencv_core.CvMemStorage storage = null;
    // DEBUG LINES
    protected CanvasFrame win = new CanvasFrame("Left eye");
    //////////////

    public LeftEyeClosedStimulus() {
        super();

        initClassifier();
        
    }

    public void setSignificantChangeLevel(double SignificantChangeLevel) {
        this.SignificantChangeLevel = SignificantChangeLevel;
    }

    public double getSignificantChangeLevel() {
        return SignificantChangeLevel;
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
            // Preload the opencv_objdetect module to work around a known bug.
            Loader.load(opencv_objdetect.class);
            
            // Load the classifier files from Java resources.
//            String leftClassiferName = "haarcascade_lefteye_2splits.xml";
            String openClassfierName = "haarcascade_eye.xml";           
//            String leftClassiferName = "haarcascade_mcs_righteye.xml"; // This is reverse
            String closedClassiferName = "haarcascade_mcs_lefteye.xml";
//            String rightClassiferName = "haarcascade_mcs_lefteye.xml"; // This is reverse
            //File classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");
            File openClassifierFile = new File(HaarCascadeModel.class.getResource(openClassfierName).toURI());
            if (openClassifierFile == null || openClassifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + openClassfierName + "\" from Java resources.");
            }
            File closedClassifierFile = new File(HaarCascadeModel.class.getResource(closedClassiferName).toURI());
            if (closedClassifierFile == null || closedClassifierFile.length() <= 0) {
                throw new IOException("Could not extract \"" + closedClassiferName + "\" from Java resources.");
            }

            openClassifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(openClassifierFile.getAbsolutePath()));            
            closedClassifier = new opencv_objdetect.CvHaarClassifierCascade(cvLoad(closedClassifierFile.getAbsolutePath()));
            
            //classifierFile.delete();
            if (openClassifier.isNull() || closedClassifier.isNull()) {
                throw new IOException("Could not load the classifier files.");
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

        // Once every 1/10sec        
        if (new Date().getTime() - lastUpdate < 100)
            return;
        lastUpdate = new Date().getTime();

        // For each source
        for (ISensor<opencv_core.IplImage> isCurSensor : lSensors) {            
            // Get latest data from sensor
            grabbedImage = isCurSensor.getData();
            grayImage = opencv_core.IplImage.create(grabbedImage.width(), grabbedImage.height(), IPL_DEPTH_8U, 1);
            smallImage = opencv_core.IplImage.create(grabbedImage.width() / divider, grabbedImage.height() / divider, IPL_DEPTH_8U, 1);

//                lastUpdate = curUpdate;
            cvClearMemStorage(storage);
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            cvResize(grayImage, smallImage, CV_INTER_AREA);
            
            // Determine whether open eye has been found
            openeye = cvHaarDetectObjects(smallImage, openClassifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
            boolean bFoundLeft = false;
            // Init current eye size to a big number
            int iCurLeftEyeSize = Integer.MAX_VALUE;
            
            // If we found a possible left eye
            int iRightEyes = 0;
            if (openeye.total() > 0) {
                CvRect r = new CvRect(0,0,0,0); // Init to left
                int iCurEye = 0;
                // While we can examine more eyes
                while (iCurEye < openeye.total()) {
                    CvRect rCur = new CvRect(cvGetSeqElem(openeye, iCurEye++));
                    // Get rightmost eye as left eye (mirror effect)
                    if (r.x() < rCur.x()) {
                        r = rCur;
                        iRightEyes++; // Found a possible right eye
                    }
                }
                iRightEyes--; // The last found left eye does not count
                
                // Init last pos if required
                if (lastLeftRect == null)
                    lastLeftRect = new CvRect(r.x(), r.y(), r.width(), r.height());

                // DEBUG LINES
//                System.err.println("r\t last\n" + r.toString() + "\t" + lastLeftRect.toString());
                //////////////
                
                // False positive. Possible right eye
                if (r.x() - lastLeftRect.x() < -lastLeftRect.width())     
                {
                    bFoundLeft = false;
                }
                else {
                    // True positive. Update last rect.
                    lastLeftRect = new CvRect(r.x(), r.y(), r.width(), r.height());
                    // Get last eye area size
                    iCurLeftEyeSize = r.width() * r.height();
                    
                    bFoundLeft = true;
                    // DEBUG LINES
    //                System.err.println("Found left eye.");
                    cvRectangle(grayImage,
                        cvPoint(r.x() * divider, r.y() * divider), cvPoint(r.x() * divider + r.width() * divider,
                        r.y() * divider + r.height() * divider), CvScalar.RED, 1, CV_AA, 0);
                    //////////////
                    // Note it down
                    iFoundInLastFrames += (iFoundInLastFrames == 10) ? 0 : 1;
                }
            }

            if (bFoundLeft) {
                // Do nothing
//                double dPercentChange = ((double)iCurLeftEyeSize - iLastEyeSize)/iCurLeftEyeSize;
//                boolean bSignificantChange = dPercentChange < -SignificantChangeLevel;
//                // If eye was resized (and significantly smaller)
//                if (bSignificantChange)
//                    shouldReact(lastLeftRect, null);
//                else { 
//                    ArrayGraph ag = new ArrayGraph();
//                    // Init array
//                    int[][] aRect = new int[lastLeftRect.height() * divider][];
//                    for (int iRows = 0; iRows < lastLeftRect.height()* divider; iRows++)
//                        aRect[iRows] = new int[lastLeftRect.width() * divider];
//                    cvSetImageROI(grayImage, lastLeftRect);
//                    // Process Image
//                    cvSmooth(grayImage, grayImage, CV_MEDIAN, 5);
//                    cvCanny(grayImage, grayImage, 10, 100, 5);
//                    cvResetImageROI(grayImage);
//                    // Copy from image
//                    for (int iXCur=lastLeftRect.x() * divider; 
//                            iXCur<(lastLeftRect.x() + lastLeftRect.width()) * divider; iXCur++)
//                        for (int iYCur=lastLeftRect.y()* divider; 
//                                iYCur<(lastLeftRect.y() + lastLeftRect.height()) * divider; iYCur++) {
//                            int iVal = (int)grayImage.asCvMat().get(iXCur, iYCur);
//                            aRect[iYCur -  lastLeftRect.y()*divider][iXCur - 
//                                    lastLeftRect.x()*divider] = iVal;
//                        }
//                    DocumentNGramGraph dgCur = ag.getGraphForArray(aRect, 
//                            5, Integer.MAX_VALUE);
//                    // Update average graph, if still training
//                    if (iAvgGraphCount < iTrainingPeriod) {
//                        CvFont cf = new CvFont();
//                        cvInitFont( cf, CV_FONT_HERSHEY_COMPLEX, 0.6, 0.6, 0, 1, 6);
//                        cvPutText( grayImage, "Training...", 
//                                cvPoint(5,20), cf, CV_RGB(255,255,255) );
//                        
//                        if (dgAvg == null) {
//                            dgAvg = dgCur;
//                            iAvgGraphCount=1;
//                        }
//                        else {
//                            //dgAvg = dgAvg.intersectGraph(dgCur);
//                            dgAvg.merge(dgCur, 1.0 / (1.0 + iAvgGraphCount));
//                            iAvgGraphCount++;
//                        };
//                        // Update normally
//                        win.showImage(grayImage);
//                    }
//                    else {
//                        if (win != null) {
//                            win.dispose();
//                            win = null;
//                        }
//                        shouldReact(lastLeftRect, dgCur);
//                    }
//                    dgLast = dgCur;
//                }
            }
            else // If left not found, it might be closed
            {
//            // If we found a closed/open eye
                closedeye = cvHaarDetectObjects(smallImage, closedClassifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
                // If found a right eye
                if ((closedeye.total() > 0) && (iFoundInLastFrames > 3)) {
                    // check whether we should react
                    CvRect r = new CvRect(cvGetSeqElem(closedeye, 0));
                    int iCurEye = 1;
                    while (iCurEye < openeye.total()) {
                        CvRect rCur = new CvRect(cvGetSeqElem(closedeye, iCurEye++));
                        // Get rightmost eye as left eye (mirror effect)
                        if (r.x() < rCur.x()) {
                            r = rCur;
                            iRightEyes++; // Found a possible right eye
                        }
                    }
                    
                    // If not a false positive
                    if (r.x() * divider - lastLeftRect.x() > -lastLeftRect.width()) {
                        // Check whether we need to react
                        shouldReact(lastLeftRect);
                        iFoundInLastFrames -= (iFoundInLastFrames == 0) ? 0 : 1 ;
                    }
                }
                else {
                    // Reset sensitivity count
                    iCurSensitivity = SensitivityCount;
                    iFoundInLastFrames -= (iFoundInLastFrames == 0) ? 0 : 1 ;
                }
            }
                        
//            for (int i = 0; i < lefteyes.total(); i++) {
//                opencv_core.CvRect r = new opencv_core.CvRect(cvGetSeqElem(eyes, i));
//                
//                // DEBUG LINES
//                // Save to image to see what happens
//                cvSetImageROI(grabbedImage, cvRect(r.x() * divider, r.y() * divider,
//                        r.width() * divider, r.height() * divider
//                        ));
//                IplImage iToSave = cvCreateImage(cvGetSize(grabbedImage), 
//                        grabbedImage.depth(), grabbedImage.nChannels());
//                cvCopy(grabbedImage, iToSave);
//                cvSaveImage("/tmp/" + new Date().getTime() + ".png", iToSave);
//                cvResetImageROI(grabbedImage);
//                //////////////
//                
//                // Get eye histogram
//                Distribution<Double> dData = new Distribution<Double>();
//                for (int iXCnt = 0; iXCnt < r.width() * divider; iXCnt++)
//                    for (int iYCnt = 0; iYCnt < r.height() * divider; iYCnt++)
//                        dData.increaseValue(grabbedImage.asCvMat().get(
//                                r.x() * divider + iXCnt, 
//                                r.y() * divider + iYCnt), 1.0);
//
//                shouldReact(dData);
//            }
        }
    }

    protected void shouldReact(CvRect r) {
//        if (ngg != null) {
//            // Init last graph if needed
//            if (dgLast == null)
//                dgLast = ngg;
//            GraphSimilarity gs = ngc.getSimilarityBetween(dgLast, dgAvg);
//            double dNVS = (gs.SizeSimilarity == 0.0) ? 0 : (gs.ValueSimilarity / gs.SizeSimilarity);
//            // DEBUG LINES
//            System.err.println(gs.toString() + "\tNVS:" + String.valueOf(dNVS));
////            System.err.println(ngc.getSimilarityBetween(ngg, dgLast));
//            //////////////
//            if (dNVS > dGraphSimThreshold) {
//                iCurSensitivity += iCurSensitivity == SensitivityCount ? 0 : 1;
//                dgLast = ngg;
//                return;
//            }
//        }
        
//        if (new Date().getTime() - lastReaction < ReactionInterval)
//            return;
        
        if (iCurSensitivity-- == 0) {
            callReactors();
//            lastReaction = new Date().getTime();
            iCurSensitivity = SensitivityCount;
        }
        // DEBUG LINES
        System.err.println("Left eye:" + iCurSensitivity);
        //////////////
        
    }

    @Override
    protected void finalize() throws Throwable {
        grabbedImage = grayImage = smallImage = null;

        super.finalize();
    }
}
