package openCV;

import application.Controller;

public class TrackerThread implements Runnable {
	private Controller controller;
	
	public TrackerThread(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		Tracker cam = new Tracker(this.controller);	
		try {
			cam.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
