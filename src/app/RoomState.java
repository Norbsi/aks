package app;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class RoomState {
	private List<Body> 		bodyList;
	private	DecimalFormat 	df = new DecimalFormat("#.##");
	private boolean			lock = false;

	public RoomState() {
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
			out += 	"%:" + df.format(body.getProbability()) + 
					" x:" + df.format(body.getX()) +
					"m y:" + df.format(body.getY()) +
					"m z:" + df.format(body.getZ()) + "m\n";
		}
		
		return out;
	}
	
	public void decay() {
		List<Body> decayed = new LinkedList<Body>();
		
		for (Body body : this.bodyList) {
			body.decay();
			if (body.getProbability() == 0) decayed.add(body);
		}
		
		while (this.locked()) {};
		this.bodyList.removeAll(decayed);
	}
	
	public void lock(boolean lock) {
		this.lock = lock;
	}
	
	public boolean locked() {
		return this.lock;
	}
	
	public void reset() {
		while (this.locked()) {};
		this.bodyList = new LinkedList<Body>();
	}
}
