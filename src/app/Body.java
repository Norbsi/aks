package app;

import java.util.Date;

public class Body {
	private double 	x, y, z;
	private int		probability;
	private Date	lastSeen;
	
	public Body(double x, double y, double z) {
		this.probability 	= 10;
		this.x 				= x;
		this.y 				= y;
		this.z 				= z;
		this.lastSeen		= new Date();
	}
	
	public double velocity(double x, double y, double z) {
		double 	distance 	= this.distance(x, y, z);
		long 	time 		= (new Date()).getTime() - this.lastSeen.getTime() / 1000;
		double	velocity	= distance / time;
		
		return velocity;
	}
	
	private double distance(double x2, double y2, double z2) {
		return Math.sqrt(Math.pow(x2-this.x, 2) + Math.pow(y2-this.y, 2) + Math.pow(z2-this.z, 2)) / 2;	
	}
	
	public void setPos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.lastSeen = new Date();
		this.probability += 10;
	}
	
	public double getX() {
		return this.x;
	}
	public double getY() {
		return this.y;
	}
	public double getZ() {
		return this.z;
	}
	
	public int getProbability() {
		return this.probability;
	}
	
	public void decay() {
		this.probability -= 2;
		if (this.probability < 0) this.probability = 0;
	}
}
