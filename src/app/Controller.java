package app;

import opencv.CamThread;
import serial.ComMapper;
import gui.Gui;

public class Controller {
	private Gui 			gui;
	private ComMapper 		serial;
	private Configuration 	configuration;
	
    public static void main(String[] args) {
    	new Controller();
    }
    
    public Controller() {
    	this.configuration = new Configuration();
    	this.gui 			= new Gui(this);
    	this.serial 		= new ComMapper(this);
		
    	if (this.configuration.getCamOn()) {
    		final Thread thread = new Thread(new CamThread(this));
    		thread.start();
    	}
    }
    
    public Gui getGui() {
    	return this.gui;
    }
    
    public Configuration getConfiguration() {
    	return this.configuration;
    }
    
    public ComMapper getSerial() {
    	return this.serial;
    }
}
