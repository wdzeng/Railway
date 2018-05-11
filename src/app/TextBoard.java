package app;

import java.awt.Font;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class TextBoard extends JTextArea {

	public TextBoard() {
		super();
		setEditable(false);
		setLineWrap(true);
		setWrapStyleWord(true);
		setFont(new Font("�ө���", Font.PLAIN, 12));
	}

	public void clear() {
		setText("");
	}

	public void print(Object obj) {
		if (obj != null)
			synchronized (this) {
				append(obj.toString());
				setCaretPosition(getDocument().getLength());
			}
	}

}
