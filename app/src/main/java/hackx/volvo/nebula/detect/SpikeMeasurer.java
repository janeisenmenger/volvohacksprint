package hackx.volvo.nebula.detect;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class SpikeMeasurer {
	private Mat _rawImage;
	private final int _gougePx;
	private int _bottomPx;
	private int _topPx;
	private final double _plateSize;
	private final double _maxSpikeSizeMm;
	private final double _minSpikeSizeMm;
	private double _oneMmAsPx;
	private double _onePxAsMm;
	
	public SpikeMeasurer(Mat image, int gougePx, double plateSize, double minSpikeSizeMm, double maxSpikeSizeMm) {
		_rawImage = image;
		_gougePx = gougePx;	
		_plateSize = plateSize;
		_minSpikeSizeMm = minSpikeSizeMm;
		_maxSpikeSizeMm = maxSpikeSizeMm;
	}
	
	public SpikeMeasurer(String imagePath, int gougePx, double plateSize, double minSpikeSizeMm, double maxSpikeSizeMm) {
		File imageFile = new File(imagePath); 
		if(imageFile.exists() && !imageFile.isDirectory()) { 
			Mat image = Imgcodecs.imread(imageFile.getPath());
    		_rawImage = image;
    		_gougePx = gougePx;
    		_plateSize = plateSize;
    		_minSpikeSizeMm = minSpikeSizeMm;
    		_maxSpikeSizeMm = maxSpikeSizeMm;
        } else {
        	throw new DetectionException("Could not load image: " + imagePath);
        }
	}
	
	private void calculatePxMmValues() {
		_oneMmAsPx = (_bottomPx - _gougePx)/ _plateSize;
		_onePxAsMm = _plateSize/((_bottomPx - _gougePx)*1.0);
	}
	
	public double measure() {
		BottomLineDetector b = new BottomLineDetector(_rawImage, _gougePx);
		_bottomPx = b.getBottomPx();
		calculatePxMmValues();
		
        int spikeTopCrop = (int) (_gougePx - ((_maxSpikeSizeMm * 1.25) * _oneMmAsPx));
        int spikeBottomCrop = (int) (_gougePx - ((_minSpikeSizeMm * 0.5) * _oneMmAsPx));	
		
		SpikeTopDetector s = new SpikeTopDetector(_rawImage, spikeTopCrop, spikeBottomCrop);
		_topPx = s.getTopPx();
		
		Imgproc.line(_rawImage, new Point(0, _topPx), new Point(_rawImage.size().width-1, _topPx), new Scalar(0,255,0), 3);		
		Imgproc.line(_rawImage, new Point(0, _bottomPx), new Point(_rawImage.size().width-1, _bottomPx), new Scalar(0,255,0), 3);
		Imgproc.line(_rawImage, new Point(0, _gougePx), new Point(_rawImage.size().width-1, _gougePx), new Scalar(0,255,0), 3);
		
		OpenCVHelper.writeImageToFile(_rawImage, "out.jpg");
		
		return  (double) ((_gougePx-_topPx) * _onePxAsMm);
	}
}
