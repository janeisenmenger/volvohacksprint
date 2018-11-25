package hackx.volvo.nebula.detect;

public class Line {
	public double _x0;
	public double _x1;
	public double _y0;
	public double _y1;
	
	public Line(double x0, double x1, double y0, double y1) {
		_x0 = x0;
		_x1 = x1;
		_y0 = y0;
		_y1 = y1;
	}
	
	public double getLength() {
		return Math.abs(_x0-_x1);
	}
	
	public double getIncline() {
		return  Math.abs(_y0-_y1)/(Math.abs(_x0-_x1)*1.0);
	}
}
