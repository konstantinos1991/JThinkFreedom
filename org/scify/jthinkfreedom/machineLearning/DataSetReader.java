package org.scify.jthinkfreedom.machineLearning;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Scanner;

import weka.core.*;
import weka.classifiers.*;
import weka.classifiers.trees.SimpleCart;

/**
 *
 * @author konstantinos
 */

/*
 * This class will read all the images and using the
 * ImageProcessor class we will extract features from the images that we will
 * use in the machine learning. The features of all the images will be written
 * to a file that will be later converted to Weka file format.
 */
public class DataSetReader {

    private String readDirectory; // directory that contains two folders of
    // images
    private String outputDirectory; // directory to output necessary files for
    // the model
    private File directory; // a directory that contains
    private File[] images; // an array that will hold images as files
    private String openEyesURL; // the URL of
    // open eyes
    // directory
    private String closedEyesURL;
    private ImageProcessor imgProc; // an ImageProcessor object, will read an
    // image and extract attributes
    private PrintStream dataOutStream; // stream that will write the attributes
    // of the images to a file
    private Scanner dataInStream; // will read the dataset.txt file
    private PrintStream arffStream; // will write the data from plain text to
    // .arff format

    public DataSetReader(String readDirectory, String outputDirectory) {
        directory = null;
        images = null;
        imgProc = null;
        this.readDirectory = readDirectory;
        this.openEyesURL = this.readDirectory + "/open_eyes";
        this.closedEyesURL = this.readDirectory + "/closed_eyes";
        this.outputDirectory = outputDirectory;

        try {

            File outDir = new File(this.outputDirectory);
            if (!outDir.exists()) {
                outDir.mkdir();
            }
            dataOutStream = new PrintStream(new File(this.outputDirectory + "/dataset.txt"));
            // System.out.println("Stream is open!");
            dataInStream = null;
            arffStream = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readADirectory(String eyeType) {
        if (eyeType.equalsIgnoreCase("open")) {
            directory = new File(openEyesURL);
        } else {
            directory = new File(closedEyesURL);
        }
        if (directory != null) {
            images = directory.listFiles();
            for (File image : images) {
                //System.out.println(image.getPath());
                if (!image.getPath().contains(".db")) {
                    imgProc = new ImageProcessor(image.getAbsolutePath());
                    imgProc.calculateImageFeatures();
                    dataOutStream.println(imgProc.toString() + "," + eyeType);
                    dataOutStream.flush();
                    imgProc = null;
                    //System.out.println("Instance written!!");
                }

            }
        } else {
            System.err.println("Directory does not exist!!!");
        }

    }

    public void read() {
        // read data
        readADirectory("open");
        readADirectory("closed");
        try {
            // close stream
            dataOutStream.close();
            // System.out.println("dataset file is written!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void convertToWekaFormat() {
        try {
            arffStream = new PrintStream(new File(this.outputDirectory + "/dataset.arff"));
            dataInStream = new Scanner(new File(this.outputDirectory + "/dataset.txt"));

            arffStream.println("@RELATION eyes");
            //arffStream.println("@ATTRIBUTE mean  NUMERIC");
            //arffStream.println("@ATTRIBUTE stdev  NUMERIC");
            //arffStream.println("@ATTRIBUTE area  NUMERIC");
            arffStream.println("@ATTRIBUTE perimeter  NUMERIC");
            //arffStream.println("@ATTRIBUTE compactness  NUMERIC");
            arffStream.println("@ATTRIBUTE class  {open,closed}");
            arffStream.println();
            arffStream.println("@DATA");
            arffStream.flush();

            String line;
            while (dataInStream.hasNextLine()) {
                line = dataInStream.nextLine();
                if (!line.isEmpty()) {
                    arffStream.println(line);
                    arffStream.flush();
                }
            }
        } catch (Exception e) {

        } finally {
            dataInStream.close();
            arffStream.close();
        }
    }

    public void trainWithWeka() {
        try {
            Instances trainInstances = new Instances(new FileReader(new File(
                    this.outputDirectory + "/dataset.arff")));
            // trainInstances.setClass(new Attribute("class"));
            trainInstances.setClassIndex(trainInstances.numAttributes() - 1);

            Classifier model = new SimpleCart();
            model.buildClassifier(trainInstances);

            weka.core.SerializationHelper.write(this.outputDirectory + "/eyeModel.model", model);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
