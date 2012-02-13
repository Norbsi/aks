package app;

import java.text.DecimalFormat;

public class CamController {
	private Controller controller;
	
	public CamController(Controller controller) {
		this.controller = controller;
	}
	
	public void bodyFound(double x, double y, double width, double height, double dist) {
        double cX 		= x + width/2;
        double cY 		= y + height/2;
        double relCX 	= (cX - 0.5) * 1.29;
        double relCY 	= (cY - 0.5) * -0.7;
        
        DecimalFormat df = new DecimalFormat("#.##");
        
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
    		
    		// TODO slow
    		if (velocity <= 0.83) {
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
	}
}
