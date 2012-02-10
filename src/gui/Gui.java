package gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JTextArea;

import app.Controller;
import app.KL;
import app.CamState;

public class Gui {
	private MainFrame 	mainFrame;
	private Controller 	controller;
	private Console		console, send, receive;
	private KL 			kl;
	private JTextArea	state;
	
	/**
	 * Create the application.
	 */
	public Gui(Controller controller) {
		this.controller = controller;
		this.kl 		= new KL(this.controller);
		this.initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.mainFrame = new MainFrame();
		
		this.mainFrame.getContentPane().setLayout(new GridLayout(0, 3));
		
		this.console 	= Console.Factory(this);
		this.send 		= Console.Factory(this);
		this.receive 	= Console.Factory(this);

		this.mainFrame.getContentPane().add(this.console);
		this.mainFrame.getContentPane().add(this.receive);
		this.mainFrame.getContentPane().add(this.send);
		this.mainFrame.getContentPane().add(this.state = new JTextArea());
		this.state.setEditable(false);
		
		this.mainFrame.setMinimumSize(new Dimension(800,400));
		this.mainFrame.pack();
		this.mainFrame.setVisible(true);
	}	
	
	public void printConsole(String text) {
		this.console.append(text);
	}
	
	public void printSend(String text) {
		this.send.append(text);
	}
	
	public void printReceive(String text) {
		this.receive.append(text);
	}
	
	public void updateState(CamState state) {
		this.state.setText(state.toString());
	}
	
	public KL getKL() {
		return this.kl;
	}
}
