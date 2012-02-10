package app;

import opencv.CamThread;
import serial.ComMapper;
import serial.Parser;
import gui.Gui;

public class Controller {
	private Gui 			gui;
	private ComMapper 		serial;
	private Configuration 	configuration;
	private CamState			state;
	private Parser			parser;
	private CamController	camController;
	
    public static void main(String[] args) {
    	new Controller();
    }
    
    public Controller() {
    	this.configuration 	= new Configuration();
    	this.gui 			= new Gui(this);
    	this.serial 		= new ComMapper(this);
    	this.parser			= new Parser(this);
    	this.camController	= new CamController(this);
    	this.state			= new CamState(this);
		
    	if (this.configuration.getCamOn()) {
    		final Thread thread = new Thread(new CamThread(this));
    		thread.start();
    		this.state.setCam(true);
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
    
    public CamState getState() {
    	return this.state;
    }
    
    public Parser getParser() {
    	return this.parser;
    }
    
    public CamController getCamController() {
    	return this.camController;
    }
}
