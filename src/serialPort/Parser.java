package serialPort;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import algorithm.Point2D;
import application.Controller;

public class Parser {
	private Controller controller;
	
	public Parser(Controller controller) {
		this.controller = controller;
	}
	
	public void process(String in) {	
		String pos = "";
		Pattern pat = Pattern.compile("PosX: ([-]?\\d+)");
		Matcher match = pat.matcher(in);
		while (match.find()) {
			pos = match.group(1);
			this.controller.getCamState().setCamPos(new Point2D(Integer.parseInt(pos), this.controller.getCamState().getCamPos().y));
		}
		pat = Pattern.compile("PosY: ([-]?\\d+)");
		match = pat.matcher(in);
		while (match.find()) {
			pos = match.group(1);
			System.out.println(pos);
			this.controller.getCamState().setCamPos(new Point2D(this.controller.getCamState().getCamPos().x, Integer.parseInt(pos)));
		}
		pat = Pattern.compile("SpeedX: ([-]?\\d+)");
		match = pat.matcher(in);
		while (match.find()) {
			pos = match.group(1);
			System.out.println(pos);
			this.controller.getCamState().setCamSpeedX(Integer.parseInt(pos));
		}
		pat = Pattern.compile("SpeedY: ([-]?\\d+)");
		match = pat.matcher(in);
		while (match.find()) {
			pos = match.group(1);
			System.out.println(pos);
			this.controller.getCamState().setCamSpeedY(Integer.parseInt(pos));
		}
	}
}
