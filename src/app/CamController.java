package app;

import java.text.DecimalFormat;

public class CamController {
	private Controller 		controller;
	private double 			maxCamPos;
	private double			maxVelocity;
	private DecimalFormat 	df 			= new DecimalFormat("#.##");
	private float			minHeight;
	
	public CamController(Controller controller) {
		this.controller = controller;
		
		this.maxCamPos 		= this.controller.getConfiguration().getMaxCamPos();
		this.maxVelocity	= this.controller.getConfiguration().getMaxVelocity();
		this.minHeight		= this.controller.getConfiguration().getMinHeight();
	}
	
	public void bodyFound(double x, double y, double width, double height, double dist) {
		double camPos		= this.controller.getCamState().getCamPosX();
		// / 2 -> 90° max
		double camPosRad	= camPos * Math.PI / this.maxCamPos / 2;
		
        double cX 		= x + width/2;
        double cY 		= y + height/2;
        // TODO explain
        double relCX 	= (cX - 0.5) * 1.29;
        double relCY 	= camPosRad + (cY - 0.5) * -0.7;

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
				// TODO config
				&& body.getProbability() >= 30
			) {
				masterBody = body;
			}
		}
		
		if (masterBody != null) {
			double bodyRad 		= Math.atan(masterBody.getX() / masterBody.getY());
			double camPos		= this.controller.getCamState().getCamPosX();
			double camPosRad	= camPos * Math.PI / this.maxCamPos;
			
			// TODO tweak, config...
			if (Math.abs(bodyRad - camPosRad) > 0.5) {
				
				double newCamPos	= bodyRad / (Math.PI / 2 / this.maxCamPos);
				this.controller.getSerial().send((int) newCamPos);
			}
		}
	}
}
