package app;

import java.util.Timer;

import opencv.CamThread;
import serial.ComMapper;
import serial.Parser;
import gui.Gui;

public class Controller {
	private Gui 			gui;
	private ComMapper 		serial;
	private Configuration 	configuration;
	private CamState		camState;
	private RoomState		roomState;
	private Parser			parser;
	private CamController	camController;
	private Timer			timer;
	
    public static void main(String[] args) {
    	new Controller();
    }
    
    public Controller() {
    	this.configuration 	= new Configuration();
    	this.gui 			= new Gui(this);
    	this.serial 		= new ComMapper(this);
    	this.parser			= new Parser(this);
    	this.camController	= new CamController(this);
    	this.camState		= new CamState(this);
    	this.roomState		= new RoomState();
		
    	if (this.configuration.getCamOn()) {
    		final Thread thread = new Thread(new CamThread(this));
    		thread.start();
    		this.camState.setCam(true);
    	}
    	
    	 this.timer = new Timer();
		 this.timer.schedule(new StateTask(this), 0, 500);
		 this.timer.schedule(new HackTask(this), 0, 10);
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
    
    public CamState getCamState() {
    	return this.camState;
    }
    
    public Parser getParser() {
    	return this.parser;
    }
    
    public CamController getCamController() {
    	return this.camController;
    }
    
    public RoomState getRoomState() {
    	return this.roomState;
    }
}
