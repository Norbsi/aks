package app;

import java.text.DecimalFormat;

public class CamController {
	private Controller 		controller;
	// TODO config
	private double 			maxCamPos 	= 570;
	private double			maxVelocity	= 0.6;
	private DecimalFormat 	df 			= new DecimalFormat("#.##");
	
	public CamController(Controller controller) {
		this.controller = controller;
	}
	
	public void bodyFound(double x, double y, double width, double height, double dist) {
		double camPos		= this.controller.getCamState().getCamPosX();
		double camPosRad	= camPos * Math.PI / this.maxCamPos;
		
        double cX 		= x + width/2;
        double cY 		= y + height/2;
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
    	
    	Body closest = null;
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
    	
    	if (closest == null) {
    		this.controller.getRoomState().addBody(new Body(absCX, absCY, absCZ));
    	} else {
    		closest.setPos(absCX, absCY, absCZ);
    	}
    	
    	this.focus();
	}
	
	private void focus() {
		Body masterBody = null;
		
		for (Body body : this.controller.getRoomState().getBodyList()) {
			if (masterBody == null || masterBody.getProbability() < body.getProbability()) {
				masterBody = body;
			}
		}
		
		double rad 		= Math.atan(masterBody.getX() / masterBody.getY());
		double camPos	= rad / (Math.PI / this.maxCamPos);
		
		this.controller.getSerial().send((int) camPos);
	}
}
