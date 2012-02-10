package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class Configuration {
	private 				int		baud, border, yres, deviceid;
	private 				String	port;
	private					boolean camOn;
	
	private transient final	File 	inifile = new File("config.ini");
	private transient 		Ini 	ini;

	public Configuration() {
		this.loadIniFile();
		
		this.baud 				= Integer.parseInt(this.ini.get("serial", "baud"));
		this.port				= this.ini.get("serial", "port");
		
		this.border				= Integer.parseInt(this.ini.get("cam", "border"));
		this.yres 				= Integer.parseInt(this.ini.get("cam", "yres"));
		
		this.camOn 				= Boolean.parseBoolean(this.ini.get("cam", "on"));
		
		this.deviceid			= Integer.parseInt(this.ini.get("cam", "deviceid"));
	}
	
	private void loadIniFile() {
		if (this.inifile.exists()) {
			try {
				this.ini = new Ini(this.inifile);
			} catch (InvalidFileFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// TODO log
			try {
				this.createIniFile();
			} catch (IOException e) {
				throw new NoConfigFileException(e);
			}
			
			try {
				this.ini = new Ini(this.inifile);
			} catch (InvalidFileFormatException e) {
				throw new NoConfigFileException(e);
			} catch (IOException e) {
				throw new NoConfigFileException(e);
			}
		}			
	}
	
	private void createIniFile() throws IOException {
		this.inifile.createNewFile();
		
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final InputStream configStream = classLoader.getResourceAsStream("resources/config/config.ini");
		
		final OutputStream newConfig = new FileOutputStream(this.inifile);
		final byte[] buf = new byte[1024];
		int len;
		while ((len = configStream.read(buf)) > 0) {
			newConfig.write(buf, 0, len);
		}
		configStream.close();
		newConfig.close();
	}
	
	public int getBaud() {
		return this.baud;
	}
	
	public String getPort() {
		return this.port;
	}

	public int getBorder() {
		return this.border;
	}

	public int getYres() {
		return this.yres;
	}

	public int getDeviceId() {
		return this.deviceid;
	}
	
	public boolean getCamOn() {
		return this.camOn;
	}
}
