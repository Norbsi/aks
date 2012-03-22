package serialPort;

import gnu.io.SerialPort;
import application.Configuration;
import application.Controller;

public class ComMapper {
	Controller 			controller;
	SerialParameters 	params;
	CamSerialConnection sconn;
	Configuration		configuration;
	Parser				parser;
	
	public ComMapper(Controller controller) {
		this.controller 	= controller;
		this.configuration 	= this.controller.getConfiguration();
		this.parser			= new Parser(this.controller);
		
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
			this.controller.getGui().printConsole("Keine Verbindung zum Serial Port", 1);
		}
	}
	
	public void send(int input) {
		// TODO FIX
		this.sconn.send(input, 0);
	}
	
	public Parser getParser() {
		return this.parser;
	}
}
