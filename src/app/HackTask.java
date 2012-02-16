package app;

import java.util.TimerTask;

public class HackTask extends TimerTask {
	private Controller controller;
	
	public HackTask(Controller controller) {
		this.controller = controller;
	}
	
	@Override
	public void run() {
		this.controller.getSerial().knock();
	}
}