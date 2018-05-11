package app;

import java.awt.Dimension;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class Button extends JButton {

	public Button(String text) {
		super(text);
		setPreferredSize(new Dimension(60, 30));
	}

}
