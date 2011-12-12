package gui;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Console extends JScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6054449663806829875L;
	private JTextArea textArea;
	
	public static Console Factory(Gui gui) {
		JTextArea textArea = new JTextArea();
		return new Console(textArea, gui);
	}

	public Console(JTextArea textArea, Gui gui) {
		super(textArea);
		
		this.textArea = textArea;
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.textArea.setEditable(false);
		this.textArea.addKeyListener(gui.getKL());
	}
	
	public void append(String text) {
		this.textArea.append(text + "\n");
		this.textArea.setCaretPosition(this.textArea.getText().length());
	}
}
