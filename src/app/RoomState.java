package app;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class RoomState {
	private Controller 		controller;
	private List<Body> 		bodyList;
	private	DecimalFormat 	df = new DecimalFormat("#.##");

	public RoomState(Controller controller) {
		this.controller = controller;
		this.bodyList 	= new LinkedList<Body>();
	}
	
	public List<Body> getBodyList() {
		return this.bodyList;
	}
	
	public void addBody(Body newBody) {
		this.bodyList.add(newBody);
	}
	
	public String toString() {
		String out = "";
		for (Body body : this.bodyList) {
			out += "%:" + body.getProbability() + " x:" + df.format(body.getX()) + "m y:" + df.format(body.getY()) + "m z:" + df.format(body.getZ()) + "m\n";
		}
		
		return out;
	}
	
	public void decay() {
		for (Body body : this.bodyList) {
			body.decay();
		}
	}
}
