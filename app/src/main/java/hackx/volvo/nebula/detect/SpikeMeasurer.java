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
	private final double _factor = 0.25;
    public Mat _rawImage;
    public Mat _smallImage;
    public final int _initialGougePx;
    public int _bottomPx;
	public final double _plateSize;
    public final double _maxSpikeSizeMm;
    public final double _minSpikeSizeMm;
    public ArrayList<Double> _resultValues;
    public ArrayList<Integer> _resultTopPxs;

		
	public SpikeMeasurer(String imagePath, int gougePx, double plateSize, double minSpikeSizeMm, double maxSpikeSizeMm) {
		File imageFile = new File(imagePath); 
		if(imageFile.exists() && !imageFile.isDirectory()) { 
			Mat image = Imgcodecs.imread(imageFile.getPath());
    		_imagePath = imagePath;
			_rawImage = image;
            _smallImage = OpenCVHelper.scaleImage(_rawImage, _factor);
            _initialGougePx = gougePx;
    		_plateSize = plateSize;
    		_minSpikeSizeMm = minSpikeSizeMm;
    		_maxSpikeSizeMm = maxSpikeSizeMm;
            _resultValues = new ArrayList<>();
            _resultTopPxs = new ArrayList<>();
        } else {
        	throw new DetectionException("Could not load image: " + imagePath);
        }
	}

	public synchronized void addTopPx(int topPx) {
	    _resultTopPxs.add(topPx);
    }

    public synchronized void addValue(double value) {
        _resultValues.add(value);
    }

	public double measure() {
        double _oneMmAsPx;
        double _onePxAsMm;
        int gougePx;
        int _topPx;

		BottomLineDetector b = new BottomLineDetector(_smallImage, (int) (_initialGougePx*_factor));
        _bottomPx = b.getBottomPx();

		gougePx = (int) (_initialGougePx*_factor) - 5;

		Thread[] threads = new Thread[11];

		for (int i = 0; i < 11; i++, gougePx++) {
            TopDetectorRunnable r = new TopDetectorRunnable(this, gougePx);
		    Thread t = new Thread(r);
		    threads[i] = t;
		    t.start();
		}

		try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double resultValue = 0;
        for (Double d : _resultValues) {
            resultValue += d;
        }

        int resultTopPx = 0;
        for (Integer i : _resultTopPxs) {
            resultTopPx += i;
        }
        resultValue /= _resultValues.size();
        resultTopPx /= _resultTopPxs.size();

        Imgproc.line(_rawImage, new Point(0, resultTopPx/_factor), new Point(_rawImage.size().width-1, resultTopPx/_factor), new Scalar(0,255,0), 3);
		Imgproc.line(_rawImage, new Point(0, _bottomPx/_factor), new Point(_rawImage.size().width-1, _bottomPx/_factor), new Scalar(0,255,0), 3);
		Imgproc.line(_rawImage, new Point(0, _initialGougePx), new Point(_rawImage.size().width-1, _initialGougePx), new Scalar(0,255,0), 3);

		OpenCVHelper.writeImageToFile(_rawImage, _imagePath);
		
		return resultValue;
	}
}
