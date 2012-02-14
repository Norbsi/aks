package app;

import gui.Gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KL implements KeyListener {
	Controller controller;
	
	public KL(Controller controller) {
		this.controller = controller;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		Gui gui = this.controller.getGui();
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE: 
				System.exit(0);
				break;
			case KeyEvent.VK_S: 
				gui.printConsole("UNTEN gedrückt");
				this.controller.getSerial().send(1);
				break;
			case KeyEvent.VK_W: 
				gui.printConsole("OBEN gedrückt");
				this.controller.getSerial().send(2);
				break;
			case KeyEvent.VK_A: 
				gui.printConsole("LINKS gedrückt");
				this.controller.getSerial().send(this.controller.getCamState().getCamPosX()-20);
				this.controller.getRoomState().reset();
				break;
			case KeyEvent.VK_D: 
				gui.printConsole("RECHTS gedrückt");
				this.controller.getSerial().send(this.controller.getCamState().getCamPosX()+20);
				this.controller.getRoomState().reset();
				break;
			case KeyEvent.VK_SPACE: 
				gui.printConsole("ZENTRIEREN gedrückt");
				this.controller.getSerial().send(0);
				this.controller.getRoomState().reset();
				break;
			case KeyEvent.VK_E: 
				gui.printConsole("e (50) gedrückt");
				this.controller.getSerial().send(-512);
				break;
			case KeyEvent.VK_Q: 
				gui.printConsole("q (-200) gedrückt");
				this.controller.getSerial().send(508);
				break;
			default:
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// nothing	
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// nothing
	}
}
