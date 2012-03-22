package algorithm;

import java.text.DecimalFormat;

import application.Controller;

public class CamState {
	private boolean 		cam;
	private int 			maxCamPos, maxCamAngle, camSpeedX, camSpeedY;
	private DecimalFormat 	df = new DecimalFormat("#.##");
	private Controller		controller;
	private Point2D			camPos;
	
	public CamState(Controller controller) {
		this.controller 	= controller;
		this.cam 			= false;

		this.camPos			= new Point2D(0,0);
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
	
	public Point2D getCamPos() {
		return this.camPos;
	}
	public void setCamPos(Point2D newCamPos) {
		this.camPos = newCamPos;
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
		double degX	= (double) this.camPos.x / this.maxCamPos * this.maxCamAngle;
		double degY	= (double) this.camPos.y / this.maxCamPos * this.maxCamAngle;
		
		return 
			"Kamera : " + on + "\n" +
			"X-Position : " + this.camPos.x + " (" + df.format(degX) + "°)\n" +
			"Y-Position : " + this.camPos.y + " (" + df.format(degY) + "°)\n" +
			"X-Geschwindigkeit : " + this.getCamSpeedX() + "\n" +
			"Y-Geschwindigkeit : " + this.getCamSpeedY();  
	}
}
