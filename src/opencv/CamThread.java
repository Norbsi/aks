package opencv;

import app.Controller;

public class CamThread implements Runnable {
	private Controller controller;
	
	public CamThread(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		Cam cam = new Cam(this.controller);	
		try {
			cam.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
