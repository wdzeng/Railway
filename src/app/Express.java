package app;

import app.utility.Calculator;
import facility.Station;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import utility.TRADate;

@SuppressWarnings("serial")
public final class Express extends JFrame implements ActionListener, ItemListener {

	private static final int LW = 180;
	private static final int LH = 36;
	private static final int PADDING_WIDTH = 6;
	private static final Border PADDING = BorderFactory.createEmptyBorder(PADDING_WIDTH, PADDING_WIDTH, PADDING_WIDTH, PADDING_WIDTH);

	private class Task {

		private static final int READY = 0, RUNNING = 1, INTERRUPTED = 2, FINISHED = 3;
		private Calculator calculator;
		private int status = READY;
		private boolean callInterrupted = false;
		private Thread task = new Thread(() -> {
			status = RUNNING;
			Object result = calculator.calculate();
			if (callInterrupted) {
				status = INTERRUPTED;
			}
			else {
				status = FINISHED;
				board.print(result);
			}
			end();
		});

		private Task() {
			calculator = Calculator.getInstance(getStation(), rbLocal.isSelected(), rbDay.isSelected(), getDate());
		}

		private void end() {
			txtDate.setEditable(true);
			cbSpcStat.setEnabled(true);
			if (cbSpcStat.isSelected()) {
				txtStat.setEditable(true);
			}
			rbDay.setEnabled(true);
			rbWeek.setEnabled(true);
			rbLocal.setEnabled(true);
			rbExp.setEnabled(true);
			btnSearch.setText("開始計算");
			lbProg.setText(null);
			task = null;
		}

		private void init() {
			txtDate.setEditable(false);
			rbDay.setEnabled(false);
			rbWeek.setEnabled(false);
			rbLocal.setEnabled(false);
			rbExp.setEnabled(false);
			txtStat.setEditable(false);
			cbSpcStat.setEnabled(false);
			btnSearch.setText("終止計算");
			txtStat.setText(txtStat.getText().trim());
			lbProg.setText(null);
		}

		private void interrupt() {
			callInterrupted = true;
			calculator.interrupt();
		}

		private boolean isRunning() {
			return status == RUNNING;
		}

		private boolean start() {
			if (status == READY) {
				init();
				task.start();
				return true;
			}
			return false;
		}
	}

	private static boolean isDateValid(String str) {
		str = str.trim();
		if (!TRADate.isValid(str)) {
			return false;
		}
		return !TRADate.getInstance(str).before(TRADate.now());
	}

	private JLabel lbDate, lbProg;
	private JTextField txtDate, txtStat;
	private JCheckBox cbSpcStat;
	private TextBoard board;
	private JButton btnSearch;
	private JRadioButton rbWeek, rbDay, rbExp, rbLocal;
	private Task task;

	private Express() {
		super("列車停靠率計算機");
		createGUI();
		pack();
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private boolean check() {
		if (!isDateValid(txtDate.getText())) {
			lbProg.setText("日期錯誤");
			return false;
		}

		if (cbSpcStat.isSelected()) {
			String s = txtStat.getText().trim();
			if (s.length() == 0) {
				lbProg.setText("車站為空");
				return false;
			}
			if (Station.getInstance(s) == null) {
				lbProg.setText("車站不存在");
				return false;
			}
		}
		return true;
	}

	private void createGUI() {
		JScrollPane scr;


		ButtonGroup gpWeekOrDay, gpExpOrLocal;
		JPanel pnLeft;

		PaddingPanel pn1, pn2, pn3, pn4, pn2_1, pnRight;

		lbDate = new JLabel("日期:  ");
		txtDate = new JTextField(TRADate.now().monthAndDate(""));
		pn1 = new PaddingPanel(PADDING);
		pn1.setPreferredSize(new Dimension(LW, LH));
		pn1.add(lbDate, BorderLayout.WEST);
		pn1.add(txtDate, BorderLayout.CENTER);

		rbDay = new JRadioButton("單日");
		rbDay.setSelected(true);
		rbWeek = new JRadioButton("一週");
		gpWeekOrDay = new ButtonGroup();
		gpWeekOrDay.add(rbDay);
		gpWeekOrDay.add(rbWeek);
		pn2 = new PaddingPanel(PADDING);
		pn2.setPreferredSize(new Dimension(LW, LH - 9));
		pn2.add(rbDay, BorderLayout.WEST);
		pn2.add(rbWeek, BorderLayout.CENTER);

		rbLocal = new JRadioButton("區間車");
		rbExp = new JRadioButton("對號車");
		gpExpOrLocal = new ButtonGroup();
		gpExpOrLocal.add(rbLocal);
		gpExpOrLocal.add(rbExp);
		rbExp.setSelected(true);
		pn2_1 = new PaddingPanel(PADDING);
		pn2_1.add(rbLocal, BorderLayout.CENTER);
		pn2_1.add(rbExp, BorderLayout.WEST);

		cbSpcStat = new JCheckBox("車站:  ");
		cbSpcStat.addItemListener(this);
		txtStat = new JTextField();
		txtStat.setEditable(false);
		pn3 = new PaddingPanel(PADDING);
		pn3.setPreferredSize(new Dimension(LW, LH));
		pn3.add(cbSpcStat, BorderLayout.WEST);
		pn3.add(txtStat, BorderLayout.CENTER);

		btnSearch = new JButton("開始計算");
		btnSearch.addActionListener(this);
		lbProg = new JLabel();
		lbProg.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
		pn4 = new PaddingPanel(PADDING);
		pn4.setPreferredSize(new Dimension(LW, LH));
		pn4.add(btnSearch, BorderLayout.WEST);
		pn4.add(lbProg, BorderLayout.CENTER);

		pnLeft = new JPanel();
		pnLeft.setLayout(new BoxLayout(pnLeft, BoxLayout.Y_AXIS));
		pnLeft.add(pn1);
		pnLeft.add(pn2);
		pnLeft.add(pn2_1);
		pnLeft.add(pn3);
		pnLeft.add(pn4);

		board = new TextBoard();
		scr = new JScrollPane(board);
		scr.setPreferredSize(new Dimension(280, scr.getPreferredSize().height));
		scr.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		pnRight = new PaddingPanel(PADDING);
		pnRight.add(scr);

		add(pnLeft, BorderLayout.WEST);
		add(pnRight, BorderLayout.CENTER);
	}

	private TRADate getDate() {
		return TRADate.getInstance(txtDate.getText().trim());
	}

	private Station getStation() {
		return Station.getInstance(txtStat.getText().trim());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (task == null || !task.isRunning()) {
			if (check()) {
				task = new Task();
				task.start();
			}
		}
		else {
			task.interrupt();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			txtStat.setEditable(true);
		}
		else {
			txtStat.setEditable(false);
			txtStat.setText(null);
		}
	}

	public static void main(String[] args) {
		new Express();
	}
}