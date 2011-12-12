package serial;

import gnu.io.SerialPort;
import app.Configuration;
import app.Controller;

public class ComMapper {
	Controller 			controller;
	SerialParameters 	params;
	CamSerialConnection sconn;
	Configuration		configuration;
	
	public ComMapper(Controller controller) {
		this.controller 	= controller;
		this.configuration 	= this.controller.getConfiguration();
		
		this.params = new SerialParameters(
			this.configuration.getPort(),
			this.configuration.getBaud(),
		    SerialPort.FLOWCONTROL_NONE,
		    SerialPort.FLOWCONTROL_NONE,
		    SerialPort.DATABITS_8,
		    SerialPort.STOPBITS_1,
		    SerialPort.PARITY_NONE
		);
		
		this.sconn = new CamSerialConnection(
			this.params,
			this.controller
		);
		
		try {
			this.sconn.openConnection();
		} catch (SerialConnectionException e) {
			this.controller.getGui().printConsole("Keine Verbindung zum Serial Port");
		}
	}
	
	public void send(int input) {
		this.sconn.send(input);
	}
}
