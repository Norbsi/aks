package serialPort;

import gnu.io.CommPortIdentifier;
import gnu.io.CommPortOwnershipListener;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import gui.Gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import application.Controller;


/**
A class that handles the details of a serial connection. Reads from one 
TextArea and writes to a second TextArea. 
Holds the state of the connection.
*/
public class CamSerialConnection implements SerialPortEventListener, CommPortOwnershipListener {
    private SerialParameters 	parameters;
    private OutputStream 		os;
    private InputStream 		is;
    private Controller 			controller;
    private Gui					gui;
    private CommPortIdentifier 	portId;
    private SerialPort 			sPort;
    private boolean 			open;
    int posX1 = 0, posX2 = 0, posY1 = 0, posY2 = 0, negX1 = 0, negX2 = 0, negY1 = 0, negY2 = 0;

    /**
    Creates a SerialConnection object and initilizes variables passed in
    as params.

    @param parent A SerialDemo object.
    @param parameters A SerialParameters object.
    @param messageAreaOut The TextArea that messages that are to be sent out
    of the serial port are entered into.
    @param messageAreaIn The TextArea that messages comming into the serial
    port are displayed on.
    */
    public CamSerialConnection(
	    SerialParameters parameters,
	    Controller controller
    ) {
		this.parameters = parameters;
		this.controller = controller;
		this.gui		= this.controller.getGui();
		open 			= false;
    }

    /**
	Attempts to open a serial connection and streams using the parameters
	in the SerialParameters object. If it is unsuccesfull at any step it
	returns the port to a closed state, throws a 
	<code>SerialConnectionException</code>, and returns.
	
	Gives a timeout of 30 seconds on the portOpen to allow other applications
	to reliquish the port if have it open and no longer need it.
	*/
    public void openConnection() throws SerialConnectionException {
	    // Obtain a CommPortIdentifier object for the port you want to open.
		try {
		    portId = CommPortIdentifier.getPortIdentifier(parameters.getPortName());
		} catch (NoSuchPortException e) {
		    throw new SerialConnectionException(e.getMessage());
		}
	
		// Open the port represented by the CommPortIdentifier object. Give
		// the open call a relatively long timeout of 30 seconds to allow
		// a different application to reliquish the port if the user 
		// wants to.
		try {
		    sPort = (SerialPort)portId.open("SerialDemo", 30000);
		} catch (PortInUseException e) {
		    throw new SerialConnectionException(e.getMessage());
		}
	
		// Set the parameters of the connection. If they won't set, close the
		// port before throwing an exception.
		try {
		    setConnectionParameters();
		} catch (SerialConnectionException e) {	
		    sPort.close();
		    throw e;
		}
	
		// Open the input and output streams for the connection. If they won't
		// open, close the port before throwing an exception.
		try {
		    os = sPort.getOutputStream();
		    is = sPort.getInputStream();
		} catch (IOException e) {
		    sPort.close();
		    throw new SerialConnectionException("Error opening i/o streams");
		}
	
		// Add this object as an event listener for the serial port.
		try {
		    sPort.addEventListener(this);
		} catch (TooManyListenersException e) {
		    sPort.close();
		    throw new SerialConnectionException("too many listeners added");
		}
	
		// Set notifyOnDataAvailable to true to allow event driven input.
		sPort.notifyOnDataAvailable(true);
		
		// Set notifyOnBreakInterrup to allow event driven break handling.
		sPort.notifyOnBreakInterrupt(true);
	
		// Set receive timeout to allow breaking out of polling loop during
		// input handling.
		try {
		    sPort.enableReceiveTimeout(30);
		} catch (UnsupportedCommOperationException e) {}
	
		// Add ownership listener to allow ownership event handling.
		portId.addPortOwnershipListener(this);
	
		open = true;
    }

    /**
    Sets the connection parameters to the setting in the parameters object.
    If set fails return the parameters object to origional settings and
    throw exception.
    */
    public void setConnectionParameters() throws SerialConnectionException {

	// Save state of parameters before trying a set.
	int oldBaudRate = sPort.getBaudRate();
	int oldDatabits = sPort.getDataBits();
	int oldStopbits = sPort.getStopBits();
	int oldParity   = sPort.getParity();
	//int oldFlowControl = sPort.getFlowControlMode();

	// Set connection parameters, if set fails return parameters object
	// to original state.
	try {
	    sPort.setSerialPortParams(
	    	parameters.getBaudRate(),
			parameters.getDatabits(),
			parameters.getStopbits(),
			parameters.getParity()
		);
	} catch (UnsupportedCommOperationException e) {
	    parameters.setBaudRate(oldBaudRate);
	    parameters.setDatabits(oldDatabits);
	    parameters.setStopbits(oldStopbits);
	    parameters.setParity(oldParity);
	    throw new SerialConnectionException("Unsupported parameter");
	}

	// Set flow control.
	try {
	    sPort.setFlowControlMode(parameters.getFlowControlIn() | parameters.getFlowControlOut());
		} catch (UnsupportedCommOperationException e) {
		    throw new SerialConnectionException("Unsupported flow control");
		}
    }

    /**
    Close the port and clean up associated elements.
    */
    public void closeConnection() {
		// If port is alread closed just return.
		if (!open) {
		    return;
		}

		// Check to make sure sPort has reference to avoid a NPE.
		if (sPort != null) {
		    try {
			// close the i/o streams.
		    	os.close();
		    	is.close();
		    } catch (IOException e) {
		    	System.err.println(e);
		    }
		    // Close the port.
		    sPort.close();
	
		    // Remove the ownership listener.
		    portId.removePortOwnershipListener(this);
		}
		open = false;
    }

    /**
    Send a one second break signal.
    */
    public void sendBreak() {
    	sPort.sendBreak(1000);
    }

    /**
    Reports the open status of the port.
    @return true if port is open, false if port is closed.
    */
    public boolean isOpen() {
    	return open;
    }

    /**
    Handles SerialPortEvents. The two types of SerialPortEvents that this
    program is registered to listen for are DATA_AVAILABLE and BI. During 
    DATA_AVAILABLE the port buffer is read until it is drained, when no more
    data is availble and 30ms has passed the method returns. When a BI
    event occurs the words BREAK RECEIVED are written to the messageAreaIn.
    */

    public void serialEvent(SerialPortEvent e) {
	 	// Create a StringBuffer and int to receive input data.
		StringBuffer inputBuffer = new StringBuffer();
		int newData = 0;
	
		// Determine type of event.
		switch (e.getEventType()) {
		    // Read data until -1 is returned. If \r is received substitute
		    // \n for correct newline handling.
		    case SerialPortEvent.DATA_AVAILABLE:
			    while (newData != -1) {
			    	try {
			    	    newData = is.read();
					    if (newData == -1) {
					    	break;
					    }
					    if ('\r' == (char)newData) {
					    	inputBuffer.append('\n');
					    } else {
					    	inputBuffer.append((char)newData);
					    }
			    	} catch (IOException ex) {
			    	    System.err.println(ex);
			    	    return;
			      	}
	   		    }
	
				// Append received data to messageAreaIn.
			   	this.gui.printReceive(new String(inputBuffer));
			  	this.controller.getSerial().getParser().process(new String(inputBuffer));
			break;
	
		    // If break event append BREAK RECEIVED message.
		    case SerialPortEvent.BI:
		    this.gui.printReceive("\n--- BREAK RECEIVED ---\n");
		}
    }   

    /**
    Handles ownership events. If a PORT_OWNERSHIP_REQUESTED event is
    received a dialog box is created asking the user if they are 
    willing to give up the port. No action is taken on other types
    of ownership events.
    */
    public void ownershipChange(int type) {
		/* TODO something
    	if (type == CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED) {
		    PortRequestedDialog prd = new PortRequestedDialog(parent);
		}
		*/
    }
    
    public int setPosWert(int wert){
    	if(wert > 128){
    		wert = wert - 128;
    		wert = wert * -1;
    	}
    	return wert;
    }
    
    public int setNegWert(int wert){
    	if(wert < -128){
    		wert = wert + 128;
    		wert = wert * -1;
    	}
    	return wert;
    }

	public void sendWertBerechnenX(int inputX, int inputY){
		if(inputX >= 0){ //Positiv
			if(inputX <= 256){
				posX1 = setPosWert(inputX);
				posX2 = 0;
			}else{
				posX1 = 256;
				posX2 = setPosWert(inputX - 256);
			}
		}
		if(inputX < 0){ //Negativ
			if(inputX > -255){
				negX1 = setNegWert(inputX);
				negX2 = 0;
			}else{
				negX1 = -255;
				negX2 = setNegWert(inputX - 255);
			}
		}
		if(inputY >= 0){ //Positiv
			if(inputY <= 256){
				posY1 = setPosWert(inputY);
				posY2 = 0;
			}else{
				posY1 = 256;
				posY2 = setPosWert(inputY - 256);
			}
		}
		if(inputY < 0){ //Negativ
			if(inputY > -255){
				negY1 = setNegWert(inputY);
				negY2 = 0;
			}else{
				negY1 = -255;
				negY2 = setNegWert(inputY - 255);
			}
		}
	}
	    
    public void send(int inputX, int inputY) {
//		input = input / 4;
//		input += 128;	
		int input = 0;
    	sendWertBerechnenX(inputX, inputY);
    	
		for(int i=0; i<10; i++){
			switch (i) {
			case 0:
				input = -126;
				break;
			case 1:
				input = posX1;
				break;
			case 2:
				input = posX2;
				break;
			case 3:
				input = negX1;
				break;
			case 4:
				input = negX2;
				break;
			case 5:
				input = posY1;
				break;
			case 6:
				input = posY2;
				break;
			case 7:
				input = negY1;
				break;
			case 8:
				input = negY2;
				break;
			case 9:
				input = 127;
				posX1 = 0;
				posX2 = 0;
				posY1 = 0;
				posY2 = 0;
				negX1 = 0;
				negX2 = 0;
				negY1 = 0;
				negY2 = 0;
				break;
			}
			if (os != null) {
			    try {
			    	os.write(input);
			    	this.gui.printSend(String.valueOf(input));
			    } catch (IOException e) {
			    	System.err.println("OutputStream write error: " + e);
			    }
			} else {
				this.gui.printSend("Keine Verbindung");
			}
		}
    }
}
