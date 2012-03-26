package algorithm;

public class Point3D extends Point2D {
	public double z;
	
	public Point3D() {
		super();
		this.z = 0;
	}
	
	public Point3D(double x, double y, double z) {
		super(x, y);
		this.z = z;
	}
}
