package hackx.volvo.nebula.detect;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class OpenCVHelper {
	
	public static Mat cropImage(Mat InputImage, int topPx, int bottomPx, int leftPx, int rightPx) {
		int x, y, width, height;	
		y = topPx;
		x = leftPx;
		
		width = rightPx - leftPx;
		height = bottomPx - topPx;
		
		Rect cropRectangle = new Rect(x, y, width, height);
		
		return new Mat(InputImage, cropRectangle);		
	}

	public static Mat resizeImage(Mat inputImage, int x, int y) {
		Mat resizedImage = new Mat();		
		Size newSize = new Size(x, y);
		
		Imgproc.resize(inputImage, resizedImage, newSize);
		return resizedImage;
	}
	
	public static Mat scaleImage(Mat inputImage, double factor) {
		int width = inputImage.width();
		int height = inputImage.height();
		
		double newWidth = width * factor;
		double newHeight = height * factor;
		return OpenCVHelper.resizeImage(inputImage, (int) newWidth, (int) newHeight);

	}
	
	public static Mat blur(Mat inputImage, int kernelSize) {
		Mat blurred = new Mat();
		Imgproc.GaussianBlur(inputImage, blurred, new Size(kernelSize, kernelSize), 0);
		
		return blurred;
	}
	
	public static Mat applyCanny(Mat inputImage, int min, int max) {
		Mat edges = new Mat();
		Imgproc.Canny(inputImage, edges, min, max, 3, false);
		return edges;
	}
	
	public static void writeImageToFile(Mat image, String fileName) {
		Imgcodecs.imwrite(fileName, image);
	}
	
	public static ArrayList<Line> getAllHoughLines(Mat inputImage) {		
	    Mat lineImage = new Mat();
	    double rho = 1.0;
	    double theta = Math.PI/180.0;
	    int threshold = 0;
	    Imgproc.HoughLines(inputImage, lineImage, rho, theta, threshold);
	    ArrayList<Line> lines = new ArrayList<>();
	    
	    for (int i = 0; i < lineImage.cols(); i++) {
	        double data[] = lineImage.get(0, i);
	        if (data.length == 0) {
	        	throw new DetectionException("No Hough lines deteced");
			}
	        double rho1 = data[0];
	        double theta1 = data[1];
	        double cosTheta = Math.cos(theta1);
	        double sinTheta = Math.sin(theta1);
	        
	        double x0 = cosTheta * rho1 + 10000 * (-sinTheta);
	        double x1 = cosTheta * rho1 - 10000 * (-sinTheta);
	        double y0 = sinTheta * rho1 + 10000 * cosTheta;
	        double y1 = sinTheta * rho1 - 10000 * cosTheta;
	        lines.add(new Line(x0, x1, y0, y1));
	        
	    }
		return lines;
	}
	
	public static double getAverageBrightness(Mat coloredImage) {
		Mat hsv = new Mat(); 
		Imgproc.cvtColor(coloredImage, hsv, Imgproc.COLOR_BGR2HSV);
		
		ArrayList<Mat> channels = new ArrayList<>();
		hsv = OpenCVHelper.resizeImage(hsv, 1, 1);
		
		Core.split(hsv, channels);
		Mat brightnessChannel = channels.get(2);
		return brightnessChannel.get(0,0)[0];
	}
}










