package app;

import java.text.DecimalFormat;

public class CamController {
	private Controller 		controller;
	private double 			maxVelocity, minHeight, moveThreshold, camFOVX, camFOVY, r90;
	private DecimalFormat 	df = new DecimalFormat("#.##");
	private int				maxCamPos, maxCamAngle, bodyThreshold;
	
	public CamController(Controller controller) {
		this.controller 			= controller;
		
		Configuration configuration	= this.controller.getConfiguration();
		
		this.maxCamPos 				= configuration.getMaxCamPos();
		this.maxCamAngle			= configuration.getMaxCamAngle();
		this.maxVelocity			= configuration.getMaxVelocity();
		this.minHeight				= configuration.getMinHeight();
		this.moveThreshold			= Math.toRadians(configuration.getMoveThreshold());
		this.bodyThreshold			= configuration.getBodyThreshold();
		this.camFOVX				= configuration.getCamFOVX();
		this.camFOVY				= configuration.getCamFOVY();
		
		this.r90					= Math.toRadians(90);
	}
	
	private double getCamPosRad() {
		double camPos = this.controller.getCamState().getCamPosX();
		return (camPos / this.maxCamPos) * Math.toRadians(this.maxCamAngle);
	}
	
	public void bodyDetected(double x, double y, double width, double height, double dist) {
		// calculate body-center pos (image) x,y 0..1
        double cX 		= x + width/2;
        double cY 		= y + height/2;
        
        Point2D absPos 	= this.camPosToAbsPos(new Point2D(cX, cY));
        Point3D cartPos	= this.absPosToCartesian(absPos, dist);
    	
    	this.controller.getGui().printConsole(
			"Körper gefunden: x:" +
			df.format(cartPos.x) + "m y:" +
			df.format(cartPos.y) + "m z:" +
			df.format(cartPos.z) + "m"
    	);
    	
    	if (cartPos.z > this.minHeight) {
	    	Body closest = null;
	    	
	    	this.controller.getRoomState().lock(true);
	    	for (Body candidate : this.controller.getRoomState().getBodyList()) {
	    		double velocity = candidate.velocity(cartPos.x, cartPos.y, cartPos.z);
	    		
	    		if (velocity <= this.maxVelocity) {
	    			if (
	    				closest == null
	    				|| closest.velocity(cartPos.x, cartPos.y, cartPos.z) < velocity
	    			) {
	    				closest = candidate;
	    			}
	    		}
	    	}
	    	this.controller.getRoomState().lock(false);
	    	
	    	if (closest == null) {
	    		this.controller.getRoomState().addBody(new Body(cartPos.x, cartPos.y, cartPos.z, this.controller));
	    	} else {
	    		closest.setPos(cartPos.x, cartPos.y, cartPos.z);
	    	}
	    	
	    	this.focus();
    	}
	}
	
	public void motionDetected(double cX, double cY, double area) {
		Point2D motion = this.camPosToAbsPos(new Point2D(cX, cY));
        
		this.controller.getRoomState().lock(true);
        for (Body candidate : this.controller.getRoomState().getBodyList()) {
        	Point2D bodyAng = new Point2D();
        	bodyAng.x		= Math.atan(candidate.getX()/candidate.getY());
        	bodyAng.y		= Math.atan(candidate.getZ()/candidate.getY());
        	   	
        	double angDist 	= this.angularDistance(motion, bodyAng);
       
        	double bDist	= candidate.getDistance();
        	
        	// TODO proper size calculation
        	if ( (angDist / Math.pow(bDist, 0.45)) < 0.2 ) {
        		candidate.moved(motion);
        		this.controller.getGui().printConsole("Bewegung erkannt (" + candidate.getX() + ", " + candidate.getY() + ", " +  candidate.getZ() + ")");
        	}
        }
        this.controller.getRoomState().lock(false);
	}
	
	private Point2D camPosToAbsPos(Point2D camPos) {
		Point2D absPos = new Point2D();
		
		absPos.x = this.getCamPosRad() + (camPos.x - 0.5) * Math.toRadians(this.camFOVX);
		// TODO fix for cam yaxis
		absPos.y = (camPos.y - 0.5) * Math.toRadians(-this.camFOVY);
		
		return absPos;
	}
	
	private Point3D absPosToCartesian(Point2D absPos, double dist) {
	   	Point3D cartesian = new Point3D();
	   	
		cartesian.x = dist * Math.sin(absPos.x);
		cartesian.y	= dist * Math.cos(absPos.x);
		cartesian.z	= dist * Math.sin(absPos.y);
		
		return cartesian;
	}
	
	private double angularDistance(Point2D p1, Point2D p2) {
		return Math.acos(Math.cos(this.r90-p1.y) * Math.cos(this.r90-p2.y) + Math.sin(this.r90-p1.y) * Math.sin(this.r90-p2.y) * Math.cos(p1.x-p2.x));
	}
	
	private void focus() {
		Body masterBody = null;
		
		for (Body body : this.controller.getRoomState().getBodyList()) {
			if (
				(masterBody == null || masterBody.getProbability() < body.getProbability())
				&& body.getProbability() >= this.bodyThreshold
			) {
				masterBody = body;
			}
		}
		
		if (masterBody != null) {
			double bodyRad = Math.atan(masterBody.getX() / masterBody.getY());

			if (Math.abs(bodyRad - this.getCamPosRad()) > this.moveThreshold) {
				double newCamPos = bodyRad / (Math.toRadians(this.maxCamAngle) / this.maxCamPos);
				this.controller.getSerial().send((int) newCamPos);
			}
		}
	}
}
