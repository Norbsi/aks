package app;

public class State {
	private boolean 	cam;
	private int 		camPosX, camPosY;
	private Controller	controller;
	
	public State(Controller controller) {
		this.controller = controller;
		this.cam 		= false;
		this.camPosX 	= 0;
		this.camPosY 	= 0;
		this.updateView();
	}
	
	private void updateView() {
		this.controller.getGui().updateState(this);
	}
	
	public boolean getCam() {
		return this.cam;
	}
	public void setCam(boolean cam) {
		this.cam = cam;
		this.updateView();
	}
	
	public int getCamPosX() {
		return this.camPosX;
	}
	public void setCamPosX(int camPosX) {
		this.camPosX = camPosX;
		this.updateView();
	}
	
	public int getCamPosY() {
		return this.camPosY;
	}
	public void setCamPosY(int camPosY) {
		this.camPosY = camPosY;
		this.updateView();
	}
	
	public String toString() {
		String on = this.getCam() ? "an" : "aus";
		
		return "Kamera: " + on + "\nX-Position: " + this.getCamPosX() + "\nY-Position: " + this.getCamPosY() + "\n";  
	}
}
