package org.scify.jthinkfreedom.machineLearning.eyeclassificationhorizontalprojection;

/**
 * javacv imports
 */
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_imgproc;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author konstantinos kostis <kekosis@gmail.com>
 *
 */
public class HorizontalProjectionImageProcessing {
    
    /**
     * The image to be manipulated.
     */
    IplImage image_to_manipulate;
    CvMat image_to_manipulate_as_cvmat;
    CvMat binary_image;
    
    /**
     * Sobel convolution masks.
     */
    CvMat Gx;
    CvMat Gy;
    
    /**
     * Horizontal Projection  vector.
     */
    double [] horizontal_projection_vector;
    
    /**
     * Standard deviation of the horizontal projection.
     */
    double standard_deviation;
    
    /**
     *
     * @param ipl_image, the image given to be manipulated
     *
     */
    public HorizontalProjectionImageProcessing(IplImage ipl_image) {
        this.image_to_manipulate = ipl_image;
        this.binary_image = new CvMat();
        this.horizontal_projection_vector = null;
        this.standard_deviation = 0.d;
    }

    /**
     * Starts the process of horizontal projection to extract information.
     */
    public void start_processing() {
        this.init_convolution_masks();
        this.iplimage_to_grayscale();
        this.iplimage_to_cvmat();
        this.produce_horizontal_edge_image();
        this.to_binary();
        this.horizontal_projection();
    }

    /**
     * Initialize convolution masks.
     */
    private void init_convolution_masks() {
        Gx = new CvMat(9);
        Gx.rows(3);
        Gx.cols(3);
        Gx.type(CV_8U, 1);
        Gy = new CvMat(9);
        Gy.rows(3);
        Gy.cols(3);
        Gy.type(CV_8U, 1);
        // values of convolution masks
        double[][] _Gx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        double[][] _Gy = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
        int ROWS = _Gx.length;
        int COLUMNS = _Gx[0].length;
        // insert values to G_x, G_y
        for (int r = 0; r < ROWS; ++r) {
            for (int c = 0; c < COLUMNS; ++c) {
                Gx.put((r*COLUMNS)+c, _Gx[r][c]);
                Gy.put((r*COLUMNS)+c, _Gy[r][c]);
            }
        }
    }

    /**
     * Converts the given IplImage to gray-scale.
     */
    private void iplimage_to_grayscale() {
        IplImage grayIplImage = cvCreateImage(
                cvGetSize(this.image_to_manipulate), IPL_DEPTH_8U, 1);
        cvCvtColor(this.image_to_manipulate, grayIplImage, CV_BGR2GRAY);
        this.image_to_manipulate.copyFrom(grayIplImage.getBufferedImage());
        // or this.image_to_manipulate = grayIplImage;
    }

    /**
     * Convert IplImage to CvMat.
     */
    private void iplimage_to_cvmat() {
        this.image_to_manipulate_as_cvmat = this.image_to_manipulate.asCvMat();
    }

    /**
     * Horizontal edge image is obtained if we apply the horizontal sobel
     * convolution mask(Gy) to the gray-scale image The convolution algorithm is
     * given below in comments.
     */
    private void produce_horizontal_edge_image() {
        CvMat output_image = new CvMat(this.image_to_manipulate_as_cvmat.size());
        output_image.rows(this.image_to_manipulate_as_cvmat.rows());
        output_image.cols(this.image_to_manipulate_as_cvmat.cols());
        output_image.type(this.image_to_manipulate_as_cvmat.depth(),
                this.image_to_manipulate_as_cvmat.channels());

        for (int image_row = 0; image_row < output_image.rows(); ++image_row) {
            for (int pixel_position = 0; pixel_position < output_image.cols(); ++pixel_position) {
                int running_total = 0;
                for (int kernel_row = 0; kernel_row < Gy.rows(); ++kernel_row) {
                    for (int element_position = 0; element_position < Gy.cols(); ++element_position) {
//                        double element_value = Gy.get(kernel_row, element_position);
                        double element_value = Gy.get((kernel_row*Gy.cols())+element_position);
//                        double pixel_value = this.image_to_manipulate_as_cvmat.get(image_row, pixel_position);
                        double pixel_value = this.image_to_manipulate_as_cvmat.get((image_row*output_image.cols())+pixel_position);
                        running_total += element_value * pixel_value;
                    }
                }
                output_image.put((image_row*output_image.cols())+pixel_position, running_total);
            }
        }
        // copy output image to image_to_manipulate_as_cvmat
        for (int i = 0; i < output_image.rows(); ++i) {
            for (int j = 0; j < output_image.cols(); ++j) {
                this.image_to_manipulate_as_cvmat.put((i*output_image.cols())+j, output_image.get((i*output_image.cols())+j));
            }
        }
    }

    /**
     * Apply threshold to the output image of horizontal.
     */
    private void to_binary() {
        cvAdaptiveThreshold(this.image_to_manipulate_as_cvmat, this.binary_image, 255,
                opencv_imgproc.CV_ADAPTIVE_THRESH_MEAN_C, opencv_imgproc.CV_THRESH_BINARY, 3, 1);
    }

    /**
     * The binary image is projected onto the
     * vertical axis.
     */
    private void horizontal_projection(){
        int M = this.binary_image.cols();
        int N = this.binary_image.rows();
        this.horizontal_projection_vector = new double[N];
        for(int v = 0;  v < N; ++v){
            for(int u = 0; u < M; ++u){
                double p = this.binary_image.get((v*M)+u);
                this.horizontal_projection_vector[v] += p;
            }
        };
    }
    
    public double get_standard_deviation_from_horizontal_projection(){
        //find mean value
        double mean = 0.d;
        int Length = this.horizontal_projection_vector.length;
        for(int i = 0; i < Length; ++i){
            mean += this.horizontal_projection_vector[i];
        }
        mean = mean/((double)Length);
        //find standard deviation
        double sum = 0.d;
        for(int i = 0; i < Length; ++i){
            sum += (this.horizontal_projection_vector[i]-mean)*(this.horizontal_projection_vector[i]-mean);
        }
        this.standard_deviation = Math.sqrt(sum/(double)Length);
        return this.standard_deviation;
    }
    
    /**
     * ***************************** Helpful Resources ****************************
     */
    /**
     * A) convolution algorithm
     */
	// for each image row in output image:
    // for each pixel in image row:
    //
    // set running total to zero
    //
    // for each kernel row in kernel:
    // for each element in kernel row:
    //
    // multiply element value by corresponding* pixel value
    // add result to running total
    //
    // set output image pixel to value of running total
    /**
     * B) Project a point of an image onto vertical(y) axis
     * http://www.smccd.net/accounts/hasson/hcoords.html
     */
}
