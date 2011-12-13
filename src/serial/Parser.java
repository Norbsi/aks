package serial;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.Controller;

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
			System.out.println(pos);
			this.controller.getState().setCamPosX(Integer.parseInt(pos));
		}
		pat = Pattern.compile("PosY: ([-]?\\d+)");
		match = pat.matcher(in);
		while (match.find()) {
			pos = match.group(1);
			System.out.println(pos);
			this.controller.getState().setCamPosY(Integer.parseInt(pos));
		}
	}
}
