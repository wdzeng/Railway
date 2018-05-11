package app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class TextAndFieldPanel extends PaddingPanel {

	private JLabel text;
	private JTextField field;

	public TextAndFieldPanel(Border border, String labelText) {
		this(border, labelText, null);
	}

	public TextAndFieldPanel(Border border, String labelText, String areaText) {
		super(border);
		text = new JLabel(labelText);
		text.setPreferredSize(new Dimension(80, 24));
		field = new JTextField(areaText, 10);
		add(text, BorderLayout.WEST);
		add(field, BorderLayout.CENTER);
	}

	public String getText() {
		return field.getText();
	}

	public void setText(String text) {
		field.setText(text);
	}

	@Override
	public void setEnabled(boolean b) {
		field.setEditable(b);
	}

}
