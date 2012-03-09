package app;

import java.text.DecimalFormat;

public class CamController {
	private Controller 		controller;
	private double 			maxVelocity, minHeight, moveThreshold;
	private DecimalFormat 	df = new DecimalFormat("#.##");
	private int				maxCamPos, maxCamAngle, bodyThreshold;
	
	public CamController(Controller controller) {
		this.controller 	= controller;
		
		this.maxCamPos 		= this.controller.getConfiguration().getMaxCamPos();
		this.maxCamAngle	= this.controller.getConfiguration().getMaxCamAngle();
		this.maxVelocity	= this.controller.getConfiguration().getMaxVelocity();
		this.minHeight		= this.controller.getConfiguration().getMinHeight();
		// convert to radians
		this.moveThreshold	= this.controller.getConfiguration().getMoveThreshold() / 57.295779513;
		this.bodyThreshold	= this.controller.getConfiguration().getBodyThreshold();
	}
	
	private double getCamPosRad() {
		double camPos = this.controller.getCamState().getCamPosX();
		return (camPos / this.maxCamPos) * (this.maxCamAngle / 57.295779513);
	}
	
	public void bodyFound(double x, double y, double width, double height, double dist) {		
        double cX 		= x + width/2;
        double cY 		= y + height/2;
        // TODO explain
        double relCX 	= (cX - 0.5) * 1.021315143;
        double relCY 	= this.getCamPosRad() + (cY - 0.5) * -1.140895649;

    	double absCX = dist * Math.sin(relCX);
    	double absCY = dist * Math.cos(relCX);
    	double absCZ = dist * Math.sin(relCY);
    	
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
				double newCamPos = bodyRad / ((this.maxCamAngle / 57.295779513) / this.maxCamPos);
				this.controller.getSerial().send((int) newCamPos);
			}
		}
	}
}
