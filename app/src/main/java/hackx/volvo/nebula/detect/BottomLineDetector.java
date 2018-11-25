package hackx.volvo.nebula.detect;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class BottomLineDetector {
	
	private Mat _grayImage;
	private Mat _colouredImage;
	private final int _gougePx;
	private final Size _imgSize;
	
	
	// input color image
	public BottomLineDetector(Mat inputImage, int gougePx) {
		_gougePx = gougePx;
		_colouredImage = new Mat();
		inputImage.copyTo(_colouredImage);
		_grayImage = new Mat();
		Imgproc.cvtColor(inputImage, _grayImage, Imgproc.COLOR_RGB2GRAY);
		_imgSize = _colouredImage.size();
	}
	
	public int getBottomPx() {
        int topCrop = (int) (_gougePx + _imgSize.height * 0.025);
        int bottomCrop = (int) (_gougePx + _imgSize.height * 0.1);
        int blurKernelSize = 5;
        int cannyMinMax = 126;
        double scaleFactor = 0.25;
        
        Mat cropped = OpenCVHelper.cropImage(_grayImage, topCrop, bottomCrop, (int) _imgSize.width/4, (int) (_imgSize.width/4)*2);
		cropped = OpenCVHelper.scaleImage(cropped, scaleFactor);
		
		Mat blurred = OpenCVHelper.blur(cropped, blurKernelSize);
		Mat edges = OpenCVHelper.applyCanny(blurred, cannyMinMax, cannyMinMax);

		ArrayList<Line> houghLines = OpenCVHelper.getAllHoughLines(edges);
		
		if (houghLines.size() == 0) {
			throw new DetectionException("Could not detect bottom line");
		}
		
		// line with length 0
		Line max = new Line(0,0,0,0);
		for (Line l : houghLines) {
			if (l.getIncline() > 0.3) {
				// incline is too high
				continue;
			}
			if (max.getLength() < l.getLength()) {
				max = l;
			}
		}
		
		if (max.getLength() == 0) {
			throw new DetectionException("No horizontal bottom line found");
		} else {
			return (int) (((max._y0 + max._y1) / 2)/scaleFactor + topCrop);
		
		}	
	}	
}
