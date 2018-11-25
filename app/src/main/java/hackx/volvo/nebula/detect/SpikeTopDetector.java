package hackx.volvo.nebula.detect;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class SpikeTopDetector {
	private Mat _colouredImage;
	private final int _topCropPx;
	private final int _bottomCropPx;
	private final Size _imgSize;
	private final double _factor = 0.25;
	
	
	// input color image
	public SpikeTopDetector(Mat inputImage, int topCropPx, int bottomCropPx) {
		_topCropPx = topCropPx;
		_bottomCropPx = bottomCropPx;
		_imgSize = inputImage.size();

		_colouredImage = new Mat(); 
		inputImage.copyTo(_colouredImage);
	}
		
	int getTopPx() {
		int leftPx = (int) (_imgSize.width/5)*2;
		int rightPx = (int) (_imgSize.width/5)*3;
		Mat cropped = OpenCVHelper.cropImage(_colouredImage, _topCropPx, _bottomCropPx, leftPx, rightPx);
		int cropHeight = (int) cropped.size().height;
		int cropWidth = (int) cropped.size().width;
		
		Mat brightnessCore = OpenCVHelper.cropImage(cropped, (cropHeight/5)*4, cropHeight, cropWidth/4, (cropWidth/4)*3);
		int coreBrightness = (int) OpenCVHelper.getAverageBrightness(brightnessCore);
		int invertedCroppedBrightness = 255 - (int) OpenCVHelper.getAverageBrightness(cropped);
		
		
		double edgeBrightness = (coreBrightness/1.7) - (invertedCroppedBrightness/20);
		
		for (int enforcement = 0; enforcement < 100; enforcement += 15) {
			for (int heightIterator = 2; heightIterator < cropHeight - 3; heightIterator++) {

				Mat currentLine = OpenCVHelper.cropImage(cropped, cropHeight-heightIterator-1, cropHeight-heightIterator+1, 0, cropWidth-1);
				
				double tmpBrightness = OpenCVHelper.getAverageBrightness(currentLine);
				if (tmpBrightness-enforcement < edgeBrightness) {
					return _topCropPx + (int) (cropHeight-heightIterator);
				}
			}
		}
		throw new DetectionException("Could not detect the upper edge of the spike");
	}
}
