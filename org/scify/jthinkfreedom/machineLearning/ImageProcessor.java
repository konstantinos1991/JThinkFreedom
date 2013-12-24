package org.scify.jthinkfreedom.machineLearning;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author konstantinos
 */

/*
 * This class can read an image, transform it to a binary image(black and white)
 * and save it to to disk It also extracts geometric properties from the image
 * as these will be used as features when classification takes place.
 */
public class ImageProcessor {

    static {
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private Mat source; // will read info from input image and store info to
    // source as grayscale
    private Mat destination; // destination matrix will store info of binary
    // matrix
    private Mat equalized; // will hold info after the histogram equalization of
    // the grayscale image
    private String imageName; // name of the input image
    private Double[][] binaryImgData; // will store the values of pixels of
    // binary image , we use the 2D array so
    // that we have easy access to pixels
    private Double[][] equalizedImgData; // will store the values of pixels of
    // the gray-scale image

    private ArrayList<Double> features; // stores visual features , geometric
    // properties of an image

    //when an image is in memory and is given as an IplImage
    //we will use some other variables
    private IplImage iplimageGiven;
    private IplImage iplImageBinary;

    /**
     * a constructor , that takes an IplImage as parameter
     *
     * @param iplimageIn
     */
    public ImageProcessor(IplImage iplimageIn) {
        this.iplimageGiven = iplimageIn;
        //convert to binary
        iplImageToBinary(iplimageGiven);
    }

    /**
     * set the name of the image to read read the image initialize the
     * destination matrix for the binary image initialize a data structure to
     * store features of image
     *
     * @param imageName
     */
    public ImageProcessor(String imageName) {
        this.imageName = imageName;
        features = new ArrayList<>();
        readImageAsGrayScale();
        createBinaryImage();
    }

    /**
     * load image as gray-scale image
     */
    private void readImageAsGrayScale() {
        source = Highgui.imread(imageName, Highgui.CV_LOAD_IMAGE_GRAYSCALE); // read
        // input
        // image
        //equalized = new Mat(); // hold image data after histogram equalization
        //Imgproc.equalizeHist(resizeImage(source), equalized); // equalize histogram of the resized
        // grayscale image
        equalized = source;
        // initialize and fill the grayScale image data array
        equalizedImgData = new Double[equalized.rows()][equalized.cols()];
        for (int rows = 0; rows < equalized.rows(); rows++) {
            for (int cols = 0; cols < equalized.cols(); cols++) {
                double pixelValue = equalized.get(rows, cols)[0];
                equalizedImgData[rows][cols] = pixelValue;
            }
        }
    }

    /**
     *
     * @param inputImage
     * @return a resized image in 32*32 pixels
     */
    private Mat resizeImage(Mat inputImage) {
        //if input image is 32x32 then return the input image and do not apply any transformation
        if ((inputImage.width() == inputImage.height()) && (inputImage.height() == 32)) {
            return inputImage;
        }
        //else apply transformation
        Mat resized = new Mat();
        Size size = new Size(32, 32);
        Imgproc.resize(inputImage, resized, size, 0, 0, Imgproc.INTER_LINEAR);
        return resized;
    }

    /**
     * creates a binary image from the input image
     */
    private void createBinaryImage() {
        destination = new Mat();
        // threshold to create binary image
		/*
         * Imgproc.threshold(source, destination, 128, 255,
         * Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
         */
        Imgproc.adaptiveThreshold(equalized, destination, 255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 3, 1);
        binaryImgData = new Double[destination.rows()][destination.cols()];
        // now fill the 2D array
        for (int rows = 0; rows < destination.rows(); rows++) {
            for (int cols = 0; cols < destination.cols(); cols++) {
                double pixelValue = destination.get(rows, cols)[0];
                binaryImgData[rows][cols] = pixelValue;
            }
        }
    }

    /**
     * converts an IplImage object to binary and stores the pixels to a 2D array
     *
     * @param given , an IplImage object
     */
    private void iplImageToBinary(IplImage given) {
        //create a grayscale image from the given IplImage object
        IplImage grayIplImage = cvCreateImage(cvGetSize(given), IPL_DEPTH_8U, 1);
        cvCvtColor(given, grayIplImage, CV_BGR2GRAY);
        //adaptive threshold to create a binary image
        cvAdaptiveThreshold(grayIplImage, grayIplImage, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 3, 1);
        iplImageBinary = grayIplImage;
        //get the cvMat of the binary iplImage
        CvMat binaryMat = iplImageBinary.asCvMat();
        binaryImgData = new Double[binaryMat.rows()][binaryMat.cols()];
        // now fill the 2D array
        for (int rows = 0; rows < binaryMat.rows(); rows++) {
            for (int cols = 0; cols < binaryMat.cols(); cols++) {
                double pixelValue = binaryMat.get(rows, cols);
                binaryImgData[rows][cols] = pixelValue;
            }
        }
    }

    /**
     * calculates visual features needed for classification
     */
    public void calculateImageFeatures() {
        if (!features.isEmpty()) {
            features.clear();
        }
        //features.add(this.getMeanOfPixelValues()); // calculate and add the
        // mean value
        //features.add(this.getStdevOfPixelValues()); // calculate and add the
        // standard deviation
        //features.add(this.getArea()); // calculate and add the area
        features.add(this.getPerimeter()); // calculate and add the
        // perimeter
        //features.add(this.getCompactness()); // calculate and add the
        // compactness
    }

    /**
     *
     * @return the list that contains the calculated image features
     */
    public ArrayList<Double> getImageFeatures() {
        if (features.isEmpty()) {
            System.err
                    .println("You must first call the method: 'calculateImageFeatures()'");
            return null;
        } else {
            return this.features;
        }
    }

    /**
     * return the matrix of the black and white image
     *
     * @return
     */
    public Mat getBinaryImage() {
        return destination;
    }

    /**
     * return the binary image(black and white) from an IplImage as CvMat object
     *
     * @return
     */
    public CvMat getIplImageBinary() {
        return iplImageBinary.asCvMat();
    }

    /**
     * save a binary image to disk
     */
    public void saveBinaryImage(String fileName) {
        Highgui.imwrite(fileName, destination);
    }

    /**
     * calculate the mean value of all pixel values(in equalized gray-scale
     * image)
     *
     * @return
     */
    public double getMeanOfPixelValues() {
        double mean = 0.0;
        for (int rows = 0; rows < equalizedImgData.length; rows++) {
            for (int cols = 0; cols < equalizedImgData[0].length; cols++) {
                mean += equalizedImgData[rows][cols];
            }
        }
        mean = (double) mean / (equalized.height() * equalized.width());
        return mean;
    }

    /**
     * calculate the standard deviation of all pixel values(in gray-scale image)
     *
     * @return
     */
    public double getStdevOfPixelValues() {
        double stdev = 0.0;
        double mean = getMeanOfPixelValues();

        for (int rows = 0; rows < equalizedImgData.length; rows++) {
            for (int cols = 0; cols < equalizedImgData[0].length; cols++) {
                stdev += (equalizedImgData[rows][cols] - mean)
                        * (equalizedImgData[rows][cols] - mean);
            }
        }
        stdev = Math.sqrt((double) stdev
                / (equalized.height() * equalized.width()));
        return stdev;
    }

    /**
     *
     * @return the area geometric property
     */
    public double getArea() {
        double area = 0.0;
        for (int rows = 0; rows < binaryImgData.length; rows++) {
            for (int cols = 0; cols < binaryImgData[0].length; cols++) {
                area += binaryImgData[rows][cols];
            }
        }
        return area;
    }

    /**
     *
     * @return the perimeter geometric property
     */
    public double getPerimeter() {
        double perimeter = 0.0;
        int rows = binaryImgData.length;
        int columns = binaryImgData[0].length;

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {

                double pixelValue = binaryImgData[x][y];

                // x=0 , y=0
                if (x == 0 && y == 0) {
                    if (pixelValue != 0
                            && (binaryImgData[x][y + 1] != 0 || binaryImgData[x + 1][y] != 0)) {
                        ++perimeter;
                    }
                }

                // x=rows-1,y=0
                if (x == rows - 1 && y == 0) {
                    if (pixelValue != 0
                            && (binaryImgData[x - 1][y] != 0 || binaryImgData[x][y + 1] != 0)) {
                        ++perimeter;
                    }
                }

                // first column at left excluding the two above positions
                if (x > 0 && x < rows - 1 && y == 0) {
                    if (pixelValue != 0
                            && (binaryImgData[x][y + 1] != 0 || binaryImgData[x - 1][y] != 0)) {
                        ++perimeter;
                    }
                }

                // done with the most left column
                // x=0,y=columns-1
                if (x == 0 && y == columns - 1) {
                    if (pixelValue != 0
                            && (binaryImgData[x][y - 1] != 0 || binaryImgData[x + 1][y] != 0)) {
                        ++perimeter;
                    }
                }

                // x=rows-1 , y=columns-1
                if (x == rows - 1 && y == columns - 1) {
                    if (pixelValue != 0
                            && (binaryImgData[x - 1][y] != 0 || binaryImgData[x][y - 1] != 0)) {
                        ++perimeter;
                    }
                }

                // x>0 , x<rows-1 , y=columns-1
                if (x > 0 && x < rows - 1 && y == columns - 1) {
                    if (pixelValue != 0
                            && (binaryImgData[x - 1][y] != 0
                            || binaryImgData[x][y - 1] != 0 || binaryImgData[x + 1][y] != 0)) {
                        ++perimeter;
                    }
                }

                // done with the most right column
                // x=0,y>0,y<columns-1
                if (x == 0 && y > 0 && y < columns - 1) {
                    if (pixelValue != 0
                            && (binaryImgData[x][y - 1] != 0
                            || binaryImgData[x][y + 1] != 0 || binaryImgData[x + 1][y] != 0)) {
                        ++perimeter;
                    }
                }
				// done with top row

                // x=rows-1,y>0,y<columns-1
                if (x == rows - 1 && y > 0 && y < columns - 1) {
                    if (pixelValue != 0
                            && (binaryImgData[x][y - 1] != 0
                            || binaryImgData[x][y + 1] != 0 || binaryImgData[x - 1][y] != 0)) {
                        ++perimeter;
                    }
                }
				// done with bottom row

                // for all the the other pixels
                if ((x > 0 && x < rows - 1) && (y > 0 && y < columns - 1)) {
                    if (pixelValue != 0
                            && (binaryImgData[x - 1][y] != 0
                            || binaryImgData[x + 1][y] != 0
                            || binaryImgData[x][y - 1] != 0 || binaryImgData[x][y + 1] != 0)) {
                        ++perimeter;
                    }
                }
            }
        }
        return perimeter;
    }

    /**
     *
     * @return the compactness geometric property
     */
    public double getCompactness() {
        return (double) getPerimeter() / getArea();
    }

    /**
     *
     * @return the pixels of a binary(black and white) image
     */
    public Double[][] getBinaryImageData() {
        return this.binaryImgData;
    }

    public String toString() {
        String result = "";
        if (!features.isEmpty()) {
            for (int i = 0; i < features.size(); i++) {
                if (i == features.size() - 1) {
                    result += features.get(i);
                } else {
                    result += features.get(i) + ",";
                }
            }
        }

        return result;
    }
}
