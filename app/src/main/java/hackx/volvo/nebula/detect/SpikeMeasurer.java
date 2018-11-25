package hackx.volvo.nebula.detect;

import java.io.File;
import java.util.ArrayList;
import java.util.OptionalDouble;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class SpikeMeasurer {
	private String _imagePath;
	private Mat _rawImage;
	private int _gougePx;
	private int _bottomPx;
	private int _topPx;
	private final double _plateSize;
	private final double _maxSpikeSizeMm;
	private final double _minSpikeSizeMm;
	private double _oneMmAsPx;
	private double _onePxAsMm;
		
	public SpikeMeasurer(String imagePath, int gougePx, double plateSize, double minSpikeSizeMm, double maxSpikeSizeMm) {
		File imageFile = new File(imagePath); 
		if(imageFile.exists() && !imageFile.isDirectory()) { 
			Mat image = Imgcodecs.imread(imageFile.getPath());
    		_imagePath = imagePath;
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
		ArrayList<Double> resultValues = new ArrayList<>();
		ArrayList<Integer> resultTopPxs = new ArrayList<>();

		_gougePx -= 5;
		for (int i = 0; i <= 10; i++, _gougePx++) {
			calculatePxMmValues();

			int spikeTopCrop = (int) (_gougePx - ((_maxSpikeSizeMm * 1.25) * _oneMmAsPx));
			int spikeBottomCrop = (int) (_gougePx - ((_minSpikeSizeMm * 0.5) * _oneMmAsPx));

			SpikeTopDetector s = new SpikeTopDetector(_rawImage, spikeTopCrop, spikeBottomCrop);
			_topPx = s.getTopPx();
			resultTopPxs.add(_topPx);
			double val = (_gougePx-_topPx) * _onePxAsMm;
            resultValues.add(val);

		}

		double resultValue = 0;
        for (Double d : resultValues) {
            resultValue += d;
        }

        int resultTopPx = 0;
        for (Integer i : resultTopPxs) {
            resultTopPx += i;
        }
        resultValue /= resultValues.size();
        resultTopPx /= resultTopPxs.size();


        Imgproc.line(_rawImage, new Point(0, resultTopPx), new Point(_rawImage.size().width-1, resultTopPx), new Scalar(0,255,0), 3);
		Imgproc.line(_rawImage, new Point(0, _bottomPx), new Point(_rawImage.size().width-1, _bottomPx), new Scalar(0,255,0), 3);
		Imgproc.line(_rawImage, new Point(0, _gougePx+5), new Point(_rawImage.size().width-1, _gougePx+5), new Scalar(0,255,0), 3);

		OpenCVHelper.writeImageToFile(_rawImage, _imagePath);
		
		return resultValue;
	}
}
