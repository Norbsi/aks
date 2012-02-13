package app;

import java.util.TimerTask;

public class StateTask extends TimerTask {
	private Controller controller;
	
	public StateTask(Controller controller) {
		this.controller = controller;
	}
	
	@Override
	public void run() {
		if (this.controller.getGui() != null) {			
			this.controller.getGui().updateRoomState(this.controller.getRoomState());
			this.controller.getRoomState().decay();
		}
	}
}