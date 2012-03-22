package gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JTextArea;

import algorithm.CamState;
import algorithm.RoomState;
import application.Controller;
import application.Keyboard;

public class Gui {
	private MainFrame 		mainFrame;
	private Controller 		controller;
	private Console			console, send, receive;
	private Keyboard 		keyboard;
	private JTextArea		camState, roomState;
	private Map				map;
	private boolean			paused;
	
	/**
	 * Create the application.
	 */
	public Gui(Controller controller) {
		this.controller = controller;
		this.keyboard 		= new Keyboard(this.controller);
		this.initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.mainFrame = new MainFrame();
		
		this.mainFrame.getContentPane().setLayout(new GridLayout(0, 3));
		
		this.console 	= Console.factory(this);
		this.send 		= Console.factory(this);
		this.receive 	= Console.factory(this);

		this.mainFrame.getContentPane().add(this.console);
		this.mainFrame.getContentPane().add(this.receive);
		this.mainFrame.getContentPane().add(this.send);
		this.mainFrame.getContentPane().add(this.camState = new JTextArea());
		this.mainFrame.getContentPane().add(this.roomState = new JTextArea());
		this.mainFrame.getContentPane().add(this.map = new Map(this.controller));
		
		this.camState.setEditable(false);
		this.roomState.setEditable(false);
		
		this.mainFrame.setMinimumSize(new Dimension(800,400));
		this.mainFrame.pack();
		this.mainFrame.setVisible(true);
	}
	
	public void clearConsole() {
		this.console.clear();
		this.send.clear();
		this.receive.clear();
	}
	
	public void pauseConsole() {
		if (this.paused) {
			this.paused = false;
			this.printConsole("weiter");
		} else {
			this.printConsole("pausiert");
			this.paused = true;
		}
	}
	
	public void printConsole(String text) {
		if (!this.paused) this.console.append(text);
	}
	
	public void printSend(String text) {
		if (!this.paused) this.send.append(text);
	}
	
	public void printReceive(String text) {
		if (!this.paused) this.receive.append(text);
	}
	
	public void updateCamState(CamState camState) {
		this.camState.setText(camState.toString());
	}
	
	public void updateRoomState(RoomState roomState) {
		this.roomState.setText(roomState.toString());
	}
	
	public void updateMap() {
		this.map.repaint();
	}
	
	public Keyboard getKeyboard() {
		return this.keyboard;
	}
}
