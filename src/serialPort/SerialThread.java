package serialPort;

import app.Controller;

public class SerialThread implements Runnable {
	private Controller controller;
	private	ComMapper comMapper;
	
	public SerialThread(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		this.comMapper = new ComMapper(this.controller);
	}
	
	public ComMapper getComMapper() {
		return this.comMapper;
	}
}
