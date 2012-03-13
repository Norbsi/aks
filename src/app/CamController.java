package app;

import java.text.DecimalFormat;

public class CamController {
	private Controller 		controller;
	private double 			maxVelocity, minHeight, moveThreshold, camFOVX, camFOVY;
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
	}
	
	private double getCamPosRad() {
		double camPos = this.controller.getCamState().getCamPosX();
		return (camPos / this.maxCamPos) * Math.toRadians(this.maxCamAngle);
	}
	
	public void bodyDetected(double x, double y, double width, double height, double dist) {
		// calculate body-center pos (image) x,y 0..1
        double cX 		= x + width/2;
        double cY 		= y + height/2;
        
        // DIFFERENT X&Y AXIS !!!!!!!!!
        // convert to cam specific angles
        double relCX 	= this.getCamPosRad() + (cX - 0.5) * Math.toRadians(this.camFOVX);
        double relCY 	= (cY - 0.5) * Math.toRadians(-this.camFOVY);

        // convert to absolute pos
    	double absCX 	= dist * Math.sin(relCX);
    	double absCY 	= dist * Math.cos(relCX);
    	double absCZ 	= dist * Math.sin(relCY);
    	
    	this.controller.getGui().printConsole(
			"Körper gefunden: x:" +
			df.format(absCX) + "m y:" +
			df.format(absCY) + "m z:" +
			df.format(absCZ) + "m"
    	);
    	
    	if (absCZ > this.minHeight) {
	    	Body closest = null;
	    	
	    	this.controller.getRoomState().lock(true);
	    	for (Body candidate : this.controller.getRoomState().getBodyList()) {
	    		double velocity = candidate.velocity(absCX, absCY, absCZ);
	    		
	    		if (velocity <= this.maxVelocity) {
	    			if (
	    				closest == null
	    				|| closest.velocity(absCX, absCY, absCZ) < velocity
	    			) {
	    				closest = candidate;
	    			}
	    		}
	    	}
	    	this.controller.getRoomState().lock(false);
	    	
	    	if (closest == null) {
	    		this.controller.getRoomState().addBody(new Body(absCX, absCY, absCZ, this.controller));
	    	} else {
	    		closest.setPos(absCX, absCY, absCZ);
	    	}
	    	
	    	this.focus();
    	}
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
