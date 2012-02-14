package app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class Map extends JComponent {
	private Controller controller;
	
	public Map(Controller controller) {
		this.controller = controller;
	}
	
    public void paint(Graphics gg) {
    	Graphics2D g = (Graphics2D) gg;
    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	
    	int width 	= this.getSize().width;
    	int height 	= this.getSize().height;
    	
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.WHITE);
		
		
		g.drawRect(width / 2, height / 2, width / 50, height / 50);
		
		g.drawLine(width / 2, height / 2, width / 3, -100);
		
		g.setColor(Color.YELLOW);
    	for (Body body : this.controller.getRoomState().getBodyList()) {
    		g.fillOval((int) (body.getX()/14 * width) + width/2, (int) (body.getY()/14 * -width) + height/2, width / 40, height / 40);
    		System.out.println((int) (body.getX()/14 * -width));
    	}
     }
}
