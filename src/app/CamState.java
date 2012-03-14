package app;

import java.text.DecimalFormat;

public class CamState {
	private boolean 		cam;
	private int 			camPosX, camPosY, maxCamPos, maxCamAngle, camSpeedX, camSpeedY;
	private DecimalFormat 	df = new DecimalFormat("#.##");
	private Controller		controller;
	
	public CamState(Controller controller) {
		this.controller 	= controller;
		this.cam 			= false;
		this.camPosX 		= 0;
		this.camPosY 		= 0;
		this.maxCamPos		= this.controller.getConfiguration().getMaxCamPos();
		this.maxCamAngle	= this.controller.getConfiguration().getMaxCamAngle();
		this.camSpeedX		= 0;		
		this.camSpeedY		= 0;
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
	public void setCamPosX(int newCamPosX) {
		this.camPosX = newCamPosX;
		this.updateView();
	}
	
	public int getCamPosY() {
		return this.camPosY;
	}
	public void setCamPosY(int newCamPosY) {
		this.camPosY = newCamPosY;
		this.updateView();
	}
	
	public int getCamSpeedX() {
		return this.camSpeedX;
	}
	public void setCamSpeedX(int newCamSpeedX) {
		this.camSpeedX = newCamSpeedX;
	}
	public int getCamSpeedY() {
		return this.camSpeedY;
	}
	public void setCamSpeedY(int newCamSpeedY) {
		this.camSpeedY = newCamSpeedY;
	}
	
	public String toString() {
		String on 	= this.getCam() ? "an" : "aus";
		double degX	= (double) this.getCamPosX() / this.maxCamPos * this.maxCamAngle;
		double degY	= (double) this.getCamPosY() / this.maxCamPos * this.maxCamAngle;
		
		return 
			"Kamera : " + on + "\n" +
			"X-Position : " + this.getCamPosX() + " (" + df.format(degX) + "°)\n" +
			"Y-Position : " + this.getCamPosY() + " (" + df.format(degY) + "°)\n" +
			"X-Geschwindigkeit : " + this.getCamSpeedX() + "\n" +
			"Y-Geschwindigkeit : " + this.getCamSpeedY();  
	}
}
