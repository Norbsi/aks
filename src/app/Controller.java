package app;

import java.util.Timer;

import opencv.CamThread;
import serialPort.ComMapper;
import serialPort.SerialThread;
import gui.Gui;

public class Controller {
	private Gui 			gui;
	private Configuration 	configuration;
	private CamState		camState;
	private RoomState		roomState;
	private CamController	camController;
	private Timer			timer;
	private SerialThread	serialThread;
	private CamThread		camThread;
	private Thread			serialThreadInstance, camThreadInstance;
	
    public static void main(String[] args) {
    	new Controller();
    }
    
    public Controller() {
    	this.configuration 			= new Configuration();
    	this.gui 					= new Gui(this);
    	
    	this.serialThread			= new SerialThread(this);
    	this.serialThreadInstance	= new Thread(this.serialThread);
    	this.serialThreadInstance.start();

    	this.camController			= new CamController(this);
    	this.camState				= new CamState(this);
    	this.roomState				= new RoomState();
		
    	if (this.configuration.getCamOn()) {
    		this.camThread			= new CamThread(this);
    		this.camThreadInstance 	= new Thread(this.camThread);
    		this.camThreadInstance.start();
    		this.camState.setCam(true);
    	}
    	
    	this.timer = new Timer();
		this.timer.schedule(new StateTask(this), 0, 500);
    }
    
    public Gui getGui() {
    	return this.gui;
    }
    
    public Configuration getConfiguration() {
    	return this.configuration;
    }
    
    public ComMapper getSerial() {
    	return this.serialThread.getComMapper();
    }
    
    public CamState getCamState() {
    	return this.camState;
    }
    
    public CamController getCamController() {
    	return this.camController;
    }
    
    public RoomState getRoomState() {
    	return this.roomState;
    }
}
