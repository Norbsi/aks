package app;

import java.util.LinkedList;
import java.util.List;

public class RoomState {
	private Controller controller;
	private List<Body> bodyList;

	public RoomState(Controller controller) {
		this.controller = controller;
		this.bodyList 	= new LinkedList<Body>();
	}
	
	public List<Body> getBodyList() {
		return this.bodyList;
	}
}
