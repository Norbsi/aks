package app;

import opencv.CamThread;
import serial.ComMapper;
import gui.Gui;

public class Controller {
	private Gui 			gui;
	private ComMapper 		serial;
	private Configuration 	configutration;
	
    public static void main(String[] args) {
    	new Controller();
    }
    
    public Controller() {
    	this.configutration = new Configuration();
    	this.gui 			= new Gui(this);
    	this.serial 		= new ComMapper(this);
		final Thread thread = new Thread(new CamThread(this));
		thread.start();
    }
    
    public Gui getGui() {
    	return this.gui;
    }
    
    public Configuration getConfiguration() {
    	return this.configutration;
    }
    
    public ComMapper getSerial() {
    	return this.serial;
    }
}
