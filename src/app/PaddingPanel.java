package app;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class PaddingPanel extends JPanel {

	public static final int TOP = 0, BOTTOM = 2, LEFT = 1, RIGHT = 3;

	public static PaddingPanel pad(int location, int padding, JComponent comp) {
		switch (location) {
		case TOP:
			return new PaddingPanel(padding, 0, 0, 0, comp);
		case LEFT:
			return new PaddingPanel(0, padding, 0, 0, comp);
		case BOTTOM:
			return new PaddingPanel(0, 0, padding, 0, comp);
		case RIGHT:
			return new PaddingPanel(0, 0, 0, padding, comp);
		default:
			throw new IllegalArgumentException();
		}
	}

	public static PaddingPanel padExcept(int location, int padding, JComponent comp) {
		switch (location) {
		case TOP:
			return new PaddingPanel(0, padding, padding, padding, comp);
		case LEFT:
			return new PaddingPanel(padding, 0, padding, padding, comp);
		case BOTTOM:
			return new PaddingPanel(padding, padding, 0, padding, comp);
		case RIGHT:
			return new PaddingPanel(padding, padding, padding, 0, comp);
		default:
			throw new IllegalArgumentException();
		}
	}

	public PaddingPanel(int padding) {
		this(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
	}

	public PaddingPanel(Border padding) {
		super();
		setBorder(padding);
		setLayout(new BorderLayout());
	}

	public PaddingPanel(int padding, JComponent comp) {
		this(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
		add(comp, BorderLayout.CENTER);
	}

	public PaddingPanel(int top, int left, int bottom, int right, JComponent comp) {
		this(BorderFactory.createEmptyBorder(top, left, bottom, right));
		add(comp, BorderLayout.CENTER);
	}
}
