package gui;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import application.Controller;

public class Keyboard implements KeyListener {
	Controller controller;
	
	public Keyboard(Controller controller) {
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
				gui.printConsole("UNTEN gedrückt", 3);
				this.controller.getSerial().send((int) this.controller.getCamState().getCamPos().x, (int) this.controller.getCamState().getCamPos().y-50);
				break;
			case KeyEvent.VK_W: 
				gui.printConsole("OBEN gedrückt", 3);
				this.controller.getSerial().send((int) this.controller.getCamState().getCamPos().x, (int) this.controller.getCamState().getCamPos().y+50);
				break;
			case KeyEvent.VK_A: 
				gui.printConsole("LINKS gedrückt", 3);
				this.controller.getSerial().send((int) this.controller.getCamState().getCamPos().x-50, (int) this.controller.getCamState().getCamPos().y);
				this.controller.getRoomState().reset();
				break;
			case KeyEvent.VK_D: 
				gui.printConsole("RECHTS gedrückt", 3);
				this.controller.getSerial().send((int) this.controller.getCamState().getCamPos().x+50, (int) this.controller.getCamState().getCamPos().y);
				this.controller.getRoomState().reset();
				break;
			case KeyEvent.VK_SPACE: 
				gui.printConsole("ZENTRIEREN gedrückt", 3);
				this.controller.getSerial().send(0,0);
				this.controller.getRoomState().reset();
				break;
			case KeyEvent.VK_E: 
				gui.printConsole("e (100,0) gedrückt", 3);
				this.controller.getSerial().send(100,0);
				break;
			case KeyEvent.VK_Q: 
				gui.printConsole("q (-200,0) gedrückt", 3);
				this.controller.getSerial().send(-200,0);
				break;
			case KeyEvent.VK_DELETE: 
				gui.clearConsole();
				break;
			case KeyEvent.VK_P: 
				gui.pauseConsole();
				break;
			case KeyEvent.VK_1:
				gui.setVerbosity(1);
				break;
			case KeyEvent.VK_2:
				gui.setVerbosity(2);
				break;
			case KeyEvent.VK_3:
				gui.setVerbosity(3);
				break;
			case KeyEvent.VK_4:
				gui.setVerbosity(4);
				break;
			case KeyEvent.VK_5:
				gui.setVerbosity(5);
				break;
			case KeyEvent.VK_6:
				gui.setVerbosity(6);
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
