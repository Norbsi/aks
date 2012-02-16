package app;

import java.text.DecimalFormat;

public class CamState {
	private boolean 		cam;
	private int 			camPosX, camPosY, maxCamPos, maxCamAngle;
	private DecimalFormat 	df = new DecimalFormat("#.##");
	private Controller		controller;
	
	public CamState(Controller controller) {
		this.controller 	= controller;
		this.cam 			= false;
		this.camPosX 		= 0;
		this.camPosY 		= 0;
		this.maxCamPos		= this.controller.getConfiguration().getMaxCamPos();
		this.maxCamAngle	= this.controller.getConfiguration().getMaxCamAngle();
		this.updateView();
	}
	
	private void updateView() {
		this.controller.getGui().updateCamState(this);
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
		String on 	= this.getCam() ? "an" : "aus";
		double deg 	= (double) this.getCamPosX() / this.maxCamPos * this.maxCamAngle;
		
		return "Kamera: " + on + "\n" +
			"X-Position: " + this.getCamPosX() + " (" + df.format(deg) + "°)\n" +
			"Y-Position: " + this.getCamPosY() + "\n";  
	}
}
