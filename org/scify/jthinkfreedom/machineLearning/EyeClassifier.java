package org.scify.jthinkfreedom.machineLearning;

import java.io.File;
import java.io.FileReader;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

import weka.core.*;
import weka.classifiers.misc.SerializedClassifier;

/**
 *
 * @author konstantinos
 */

/*
 *This class decides if an image is an open or a closed
 * eye
 */
public class EyeClassifier {

    private SerializedClassifier classifier;
    private Instances set;
    private ImageProcessor imgproc;

    /**
     * constructor
     *
     * @param modelDirectory, the directory where the model is stored
     */
    public EyeClassifier(String modelDirectory) {
        try {
            this.classifier = new SerializedClassifier();
            classifier.setModelFile(new File(modelDirectory + "/eyeModel.model"));
            this.set = new Instances(new FileReader(modelDirectory + "/dataset.arff"));
            System.out.println("Model is loaded!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param image , an IplImage object
     * @return "OPEN" or "CLOSED" , a string indicating the state of an Eye
     */
    public String predictEyeTypeOfIplImage(IplImage image) {
        String eyeType = null;
        try {
            imgproc = new ImageProcessor(image);
            Instance i = new Instance(2);
            i.setDataset(set);
            i.setValue(0, imgproc.getPerimeter());

            double[] probabilities = classifier.distributionForInstance(i);

            if (probabilities[0] > probabilities[1] || probabilities[0] == probabilities[1]) //better to consider an eye open if the two probabilities are equal
            {
                eyeType = "OPEN";
            } else {
                eyeType = "CLOSED";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return eyeType;
    }

    /**
     *
     * @param path , the path to an image
     * @return "OPEN" or "CLOSED" , a string indicating the state of an Eye
     */
    public String predictEyeType(String path) {

        String eyeType = null;
        try {
            imgproc = new ImageProcessor(path);
            Instance i = new Instance(2);
            i.setDataset(set);
            //i.setValue(0, imgproc.getMeanOfPixelValues());
            //i.setValue(1, imgproc.getStdevOfPixelValues());
            //i.setValue(2, imgproc.getArea());
            i.setValue(0, imgproc.getPerimeter());
            System.out.println("Perimeter is: " + imgproc.getPerimeter());
            //i.setValue(4, imgproc.getCompactness());
            // i.setValue(5, "?");

            double[] probabilities = classifier.distributionForInstance(i);

            if (probabilities[0] > probabilities[1] || probabilities[0] == probabilities[1]) //better to consider an eye open if the two probabilities are equal
            {
                eyeType = "OPEN";
            } else {
                eyeType = "CLOSED";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return eyeType;
    }

}
