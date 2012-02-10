package app;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class CamController {
	private Controller controller;
	private List<Body> bodyList;
	
	public CamController(Controller controller) {
		this.controller = controller;
		this.bodyList 	= new LinkedList<Body>();
	}
	
	public void bodyFound(double x, double y, double width, double height, double dist) {
        double cX = x + width/2;
        double cY = y + height/2;
        
        DecimalFormat df = new DecimalFormat("#.##");
        
    	this.controller.getGui().printConsole(
			"Körper gefunden: " +
			df.format(cX) + ":" + df.format(cY) + " " +
			df.format(dist) + "m"
    	);
	}
}
