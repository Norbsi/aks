package algorithm;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import application.Controller;

public class Body {
	private double 			probability;
	private Point3D			pos;
	private Date			lastSeen;
	private Controller 		controller;
	private Queue<Point3D>	motionQueue;
	
	public Body(Point3D pos, Controller controller) {
		this.controller		= controller;
		this.probability 	= this.controller.getConfiguration().getDetectBonus();
		this.pos			= pos;
		this.lastSeen		= new Date();
		this.motionQueue	= new LinkedList<Point3D>();
	}
	
	public double calcVelocity(double x, double y, double z) {
		double 	distance 	= this.distance(x, y, z);
		long 	time 		= (new Date()).getTime() - this.lastSeen.getTime();
		double	velocity	= distance / time * 1000;
		
		return velocity;
	}
	
	private double distance(double x2, double y2, double z2) {
		return Math.sqrt(Math.pow(x2-this.pos.x, 2) + Math.pow(y2-this.pos.y, 2) + Math.pow(z2-this.pos.z, 2)) / 2;	
	}
	
	public void setPos(Point3D pos) {
		this.pos 		= pos;
		this.lastSeen 	= new Date();
		
		this.addProbability(this.controller.getConfiguration().getDetectBonus());
	}
	
	public Point3D getPos() {
		return this.pos;
	}

	public double getDistance() {
		// TODO 3D!!!!!
		return Math.sqrt(Math.pow(this.pos.x, 2) + Math.pow(this.pos.y, 2));
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
	
	public void moved(Point3D motion) {
		// TODO config
		if (this.motionQueue.size() > 10) this.motionQueue.poll();
		this.motionQueue.add(motion);
		
		long lastSeenDelta = (new Date()).getTime() - this.lastSeen.getTime();
		
		// TODO config
		if (lastSeenDelta > 0.2) {
			this.pos = this.calcMotionCenter();
		}
		
		// TODO config
		this.addProbability(0.05);
	}
	
	private Point3D calcMotionCenter() {
		Point3D center = new Point3D();
		
		for (Point3D point : this.motionQueue) {
			center.x += point.x;
			center.y += point.y;
			center.z += point.z;
		}
		
		center.x /= this.motionQueue.size();
		center.y /= this.motionQueue.size();
		center.z /= this.motionQueue.size();
		
		return center;
	}
}
