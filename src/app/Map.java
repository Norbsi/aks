package app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class Map extends JComponent {
	private Controller 	controller;
	private int			maxCamPos;
	
	public Map(Controller controller) {
		this.controller = controller;
		this.maxCamPos 	= this.controller.getConfiguration().getMaxCamPos();
	}
	
    public void paint(Graphics gg) {
    	Graphics2D g = (Graphics2D) gg;
    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	
    	int width 	= this.getSize().width;
    	int height 	= this.getSize().height;
    	
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.DARK_GRAY);
				
		double camPos		= this.controller.getCamState().getCamPosX();
		double camPosAngle	= camPos * 180 / this.maxCamPos;
		
		g.fillArc(-width/2, -height / 2, width*2, height*2, 90 + 30 + (int) camPosAngle, -60);
		
		g.setColor(Color.YELLOW);
    	for (Body body : this.controller.getRoomState().getBodyList()) {
    		g.fillOval((int) (body.getX()/14 * width) + width/2, (int) (body.getY()/14 * -width) + height/2, width / 40, height / 40);
    	}
     }
}
