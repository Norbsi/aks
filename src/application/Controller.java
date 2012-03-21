package application;

import java.util.Timer;

import algorithm.CamController;
import algorithm.CamState;
import algorithm.RoomState;
import algorithm.StateTask;

import openCV.TrackerThread;
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
	private TrackerThread	trackerThread;
	private Thread			serialThreadInstance, trackerThreadInstance;
	
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
    		this.trackerThread			= new TrackerThread(this);
    		this.trackerThreadInstance 	= new Thread(this.trackerThread);
    		this.trackerThreadInstance.start();
    		this.camState.setCam(true);
    	}
    	
    	this.timer = new Timer();
    	// TODO config?
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
