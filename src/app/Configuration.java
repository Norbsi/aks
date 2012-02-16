package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class Configuration {
	private 				int		baud, yres, deviceid, detectBonus, decay, decayTime, maxCamPos, maxCamAngle, moveThreshold;
	private 				String	port;
	private					boolean camOn;
	private					float	maxVelocity, minHeight;
	
	private transient final	File 	inifile = new File("config.ini");
	private transient 		Ini 	ini;

	public Configuration() {
		this.loadIniFile();
		
		this.baud 				= Integer.parseInt(this.ini.get("serial", "baud"));
		this.port				= this.ini.get("serial", "port");
		
		this.yres 				= Integer.parseInt(this.ini.get("cam", "yres"));
		this.camOn 				= Boolean.parseBoolean(this.ini.get("cam", "on"));
		this.deviceid			= Integer.parseInt(this.ini.get("cam", "deviceid"));
		this.maxCamPos			= Integer.parseInt(this.ini.get("cam", "maxcampos"));
		this.maxCamAngle		= Integer.parseInt(this.ini.get("cam", "maxcamangle"));
		
		this.detectBonus		= Integer.parseInt(this.ini.get("algorithm", "detectbonus"));
		this.decay				= Integer.parseInt(this.ini.get("algorithm", "decay"));
		this.decayTime			= Integer.parseInt(this.ini.get("algorithm", "decaytime"));
		this.maxVelocity		= Float.parseFloat(this.ini.get("algorithm", "maxvelocity"));
		this.minHeight			= Float.parseFloat(this.ini.get("algorithm", "minheight"));
		this.moveThreshold		= Integer.parseInt(this.ini.get("algorithm", "movethreshold"));
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

	public int getYres() {
		return this.yres;
	}

	public int getDeviceId() {
		return this.deviceid;
	}
	
	public boolean getCamOn() {
		return this.camOn;
	}
	
	public int getDetectBonus() {
		return this.detectBonus;
	}
	public int getDecay() {
		return this.decay;
	}
	public int getDecayTime() {
		return this.decayTime;
	}
	public int getMaxCamPos() {
		return this.maxCamPos;
	}
	public int getMaxCamAngle() {
		return this.maxCamAngle;
	}
	public float getMaxVelocity() {
		return this.maxVelocity;
	}
	public float getMinHeight() {
		return this.minHeight;
	}
	public int getMoveThreshold() {
		return this.moveThreshold;
	}
}
