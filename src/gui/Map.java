package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import algorithm.Body;
import application.Configuration;
import application.Controller;

public class Map extends JComponent {
	/**
	 * 
	 */
	private static final long 	serialVersionUID = -8929711985144231275L;
	private Controller 			controller;
	private int					maxCamPos, moveThresholdX, bodyThreshold, camFOVX, maxCamAngle;
	
	public Map(Controller controller) {
		this.controller 			= controller;
		Configuration configuration = this.controller.getConfiguration();
		
		this.maxCamPos 				= configuration.getMaxCamPos();
		this.maxCamAngle			= configuration.getMaxCamAngle();
		this.moveThresholdX			= configuration.getMoveThresholdX();
		this.bodyThreshold			= configuration.getBodyThreshold();
		this.camFOVX				= (int) Math.round(configuration.getCamFOVX());
	}
	
    public void paint(Graphics gg) {
    	Graphics2D g = (Graphics2D) gg;
    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	
    	int width 	= this.getSize().width;
    	int height 	= this.getSize().height;
    	
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
				
		if (this.controller.getCamState() != null) {
			double camPos		= this.controller.getCamState().getCamPos().x;
			double camPosAngle	= camPos * this.maxCamAngle / this.maxCamPos;
			
			g.setColor(Color.DARK_GRAY);
			// 90->start angle (north)
			g.fillArc(-width/2, -height/2, width*2, height*2, 90 + (this.camFOVX/2) - (int) camPosAngle, -this.camFOVX);
			g.setColor(Color.GRAY);
			g.fillArc(-width/2, -height/2, width*2, height*2, 90 + this.moveThresholdX/2 - (int) camPosAngle, -this.moveThresholdX);
			
			g.setColor(Color.YELLOW);
	    	for (Body body : this.controller.getRoomState().getBodyList()) {
	    		if (body.getProbability() >= this.bodyThreshold) g.fillOval((int) (body.getPos().x/14 * width) + width/2, (int) (body.getPos().y/14 * -width) + height/2, width / 40, height / 40);
	    	}
		}
    }
}
