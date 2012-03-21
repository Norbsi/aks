package algorithm;

import java.util.Date;

import application.Controller;

public class Body {
	private double 			x, y, z, probability;
	private Date			lastSeen;
	private Controller 		controller;
	
	public Body(double x, double y, double z, Controller controller) {
		this.controller		= controller;
		this.probability 	= this.controller.getConfiguration().getDetectBonus();
		this.x 				= x;
		this.y 				= y;
		this.z 				= z;
		this.lastSeen		= new Date();
	}
	
	public double velocity(double x, double y, double z) {
		double 	distance 	= this.distance(x, y, z);
		long 	time 		= (new Date()).getTime() - this.lastSeen.getTime();
		double	velocity	= distance / time * 1000;
		
		return velocity;
	}
	
	private double distance(double x2, double y2, double z2) {
		return Math.sqrt(Math.pow(x2-this.x, 2) + Math.pow(y2-this.y, 2) + Math.pow(z2-this.z, 2)) / 2;	
	}
	
	public void setPos(double x, double y, double z) {
		this.x 				= x;
		this.y 				= y;
		this.z 				= z;
		this.lastSeen 		= new Date();
		
		this.addProbability(this.controller.getConfiguration().getDetectBonus());
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
	public double getDistance() {
		return Math.sqrt(Math.pow(this.x,2) + Math.pow(this.y, 2));
	}
	public double getProbability() {
		return this.probability;
	}
	
	private void addProbability(double added) {
		this.probability += added;
		
		if (this.probability > 100) this.probability = 100;
		if (this.probability < 0) 	this.probability = 0;
	}
	
	public void decay() {
		this.addProbability(-this.controller.getConfiguration().getDecay());
	}
	
	public void moved(Point2D p) {
		// TODO config
		this.addProbability(0.05);
	}
}
