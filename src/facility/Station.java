package facility;

import app.PaddingPanel;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * This class contains the data of all stations in Taiwan.
 * @author Parabola
 */
public final class Station implements Serializable, Comparable<Station> {

	private static final long serialVersionUID = 1137273059519100179L;

	/**
	 * Singleton guaranteed.
	 */
	private static class Init {

		/**
		 * A list containing <tt>Station</tt> instaces. This list is singleton.
		 */
		private static final List<Station> instances;

		static {
			try {
				instances = Loader.initFromTxt();
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new Error("Stations initialization fails.");
			}
		}
	}

	/**
	 * Get stations startTime file.
	 */
	public static class Loader {

		public static List<Station> initFromTxt() {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("data/stations.txt")))) {
				in.readLine(); //Skip a line;
				List<Station> list = new ArrayList<>(150);
				int a = 0;
				String readed;
				Set<String> names = new HashSet<>();
				Station s = new Station();
				while ((readed = in.readLine()) != null) {
					if (readed.length() == 0) {
						s.addNames(names);
						if (s.name != null) {
							list.add(s);
						}
						names.clear();
						a = 0;
						s = new Station();
						continue;
					}

					switch (a) {
					case 0:
						s.name = readed;
						break;
					case 1:
						s.enName = readed;
						break;
					case 2:
						s.idSearch = Integer.parseInt(readed);
						break;
					case 3:
						s.idBook = Integer.parseInt(readed);
						break;
					case 4:
						s.county = County.getInstance(readed);
						break;
					case 5:
						s.level = StationLevel.getInstance(readed);
						break;
					case 6:
						s.location = Double.parseDouble(readed);
						break;
					default:
						s.otherNames.add(readed);
						break;
					}
					a++;
				}
				Collections.sort(list);
				return list;
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new Error("Initialization failed.");
			}
		}

		public static boolean saveAsTxt(Collection<Station> stations) {
			try (PrintWriter out = new PrintWriter(new FileWriter("data/stations.txt"))) {
				out.println(); // Skip a line;
				for (Station s : stations) {
					out.println(s.name);
					out.println(s.enName);
					out.println(s.idSearch);
					out.println(s.idBook);
					if (s.county == null) {
						out.println(" ");
					}
					else {
						out.println(s.county.toString());
					}
					if (s.level == null) {
						out.println(" ");
					}
					else {
						out.println(s.level.toString());
					}
					out.println(s.location);
					for (String n : s.otherNames) {
						out.println(n);
					}
					out.println();
				}
				out.println();
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * An application used to create, delete and modify station instance.
	 */
	public static class Manager extends JFrame {

		@FunctionalInterface
		interface KeyPressedListener extends KeyListener {

			@Override
			default void keyReleased(KeyEvent e) {
			}

			@Override
			default void keyTyped(KeyEvent e) {
			}
		}

		@FunctionalInterface
		interface ValueChangedListener extends DocumentListener {

			@Override
			default void changedUpdate(DocumentEvent e) {
			}

			@Override
			default void insertUpdate(DocumentEvent e) {
				valueChanged(e);
			}

			@Override
			default void removeUpdate(DocumentEvent e) {
				valueChanged(e);
			}

			void valueChanged(DocumentEvent e);
		}

		private static class NameComparator implements Comparator<String> {

			@Override
			public int compare(String o1, String o2) {
				int l1 = o1.length(), l2 = o2.length();
				if (l1 == l2) {
					return o1.compareTo(o2);
				}
				return l1 - l2;
			}
		}

		public class KeyValueList extends JList<KeyValueString> {

			public final KeyValueString book = new KeyValueString("Booking ID");
			public final KeyValueString ch = new KeyValueString("Chinese");
			public final KeyValueString en = new KeyValueString("English");
			public final KeyValueString location = new KeyValueString("Location ID");
			public final KeyValueString others = new KeyValueString("Other names");
			public final KeyValueString search = new KeyValueString("Searching ID");

			private KeyValueList() {
				super();
				DefaultListModel<KeyValueString> kvss = new DefaultListModel<>();
				kvss.addElement(ch);
				kvss.addElement(en);
				kvss.addElement(others);
				kvss.addElement(book);
				kvss.addElement(search);
				kvss.addElement(location);
				setModel(kvss);
				setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				setFont(new Font("Simsun", Font.PLAIN, 16));
			}

			public void show(Station station) {
				cbbCounty.removeItemListener(itemListener);
				cbbLevel.removeItemListener(itemListener);
				if (station == null) {
					ch.setValue("");
					en.setValue("");
					others.setValue("");
					search.setValue("");
					book.setValue("");
					location.setValue("");
					cbbCounty.setSelectedItem(null);
					cbbLevel.setSelectedItem(null);
					remove.setEnabled(false);
				}
				else {
					ch.setValue(station.getName());
					en.setValue(station.getEnName());
					if (station.getOtherNames().isEmpty()) {
						others.setValue("");
					}
					else {
						others.setValue(listToStr(station.getOtherNames()));
					}
					search.setValue(Integer.toString(station.getIdSearch()));
					book.setValue(Integer.toString(station.getIdBook()));
					location.setValue(Double.toString(station.location));
					cbbCounty.setSelectedItem(station.getCounty());
					cbbLevel.setSelectedItem(station.getLevel());
					remove.setEnabled(true);
				}
				repaint();
				cbbCounty.addItemListener(itemListener);
				cbbLevel.addItemListener(itemListener);
			}
		}

		class KeyValueString {

			private String key;
			private String value;

			public KeyValueString(String key) {
				this(key, null);
			}

			public KeyValueString(String key, String value) {
				setKey(key);
				setValue(value);
			}

			public String getValue() {
				return value;
			}

			public void setKey(String key) {
				this.key = key == null ? "" : key;
			}

			public void setValue(String value) {
				this.value = value == null ? "" : value;
			}

			@Override
			public String toString() {
				return String.format("%-18s", key + ":") + value;
			}
		}

		private class Saving {

			private boolean isSaving;
			private Runnable run = () -> {
				isSaving = true;
				Station.Loader.saveAsTxt(listSta.getAllStations());
				message.setText(isSaving ? "Saved" : "Interrupted");
				isSaving = false;
			};
			private Thread thread;

			public Saving() {
			}

			public void save() {
				if (isSaving) {
					try {
						thread.join();
					}
					catch (InterruptedException ignored) {
					}
				}
				thread = new Thread(run);
				thread.start();
			}
		}

		private class StationList extends JPanel {

			public final JList<Station> list;
			private final DefaultListModel<Station> model = new DefaultListModel<>();

			public StationList() {
				super(new BorderLayout());
				for (Station s : initStations()) {
					model.addElement(s);
				}
				list = new JList<>(model);
				add(new JScrollPane(list), BorderLayout.CENTER);
			}

			public boolean addStation(Station added) {
				if (model.contains(added) || added == null) {
					return false;
				}
				int index = list.getSelectedIndex();
				model.add(Math.min(index, model.size()), added);
				list.setSelectedIndex(index);
				return true;
			}

			public List<Station> getAllStations() {
				int size = model.size();
				List<Station> stations = new ArrayList<>(size);
				for (int i = 0 ; i < size ; i++) {
					stations.add(model.get(i));
				}
				return stations;
			}

			public Station getStation(String name) {
				int size = model.size();
				for (int i = 0 ; i < size ; i++) {
					Station s = model.get(i);
					if (s.is(name)) {
						return s;
					}
				}
				return null;
			}

			public boolean remove() {
				Station removed = list.getSelectedValue();
				if (removed != null) {
					int option = JOptionPane.showConfirmDialog(Manager.this,
					                                           "Sure " + "to " + "delete?",
					                                           "Confirm Window",
					                                           JOptionPane.YES_NO_OPTION,
					                                           JOptionPane.WARNING_MESSAGE);
					if (option == JOptionPane.YES_OPTION) {
						model.removeElement(removed);
						return true;
					}
				}
				return false;
			}
		}

		private static String listToStr(List<String> added) {
			List<String> list = new ArrayList<>(added);
			list.sort(new NameComparator());
			return String.join(" ", list);
		}

		private static Set<String> strToSet(String str) {
			return new HashSet<>(Arrays.asList(str.split("\\s+")));
		}

		private final Saving task = new Saving();
		private Button add = new Button("Add");
		private JComboBox<County> cbbCounty;
		private JComboBox<StationLevel> cbbLevel;
		private StationList listSta;
		private final ItemListener itemListener = e -> {
			Station station = listSta.list.getSelectedValue();
			if (station != null) {
				if (e.getSource() == cbbCounty) {
					station.county = ((County) e.getItem());
				}
				else {
					station.level = ((StationLevel) e.getItem());
				}
			}
		};
		private KeyValueList listVal;
		private JLabel message = new JLabel();
		private Button remove = new Button("Remove");
		private JTextField searchBox = new JTextField();
		private final ValueChangedListener typedListener = e -> {
			Station s = listSta.getStation(searchBox.getText().trim());
			if (s != null) {
				listSta.list.setSelectedValue(s, true);
			}
		};
		private Button submit = new Button("Submit");
		private final ActionListener buttonListener = e -> {
			if (e.getSource() == submit) {
				task.save();
			}
			else if (e.getSource() == add) {
				listSta.addStation(new Station("???"));
				message.setText("Added.");
			}
			else {
				listSta.remove();
			}
		};
		private JTextField textBox = new JTextField();
		private final KeyPressedListener keyListener = e -> {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				Station station = listSta.list.getSelectedValue();
				KeyValueString kvs = listVal.getSelectedValue();
				if (station == null || kvs == null) {
					return;
				}
				String val = textBox.getText();
				if (kvs == listVal.en) {
					if (setName(station, false, val)) {
						listSta.repaint();
					}
				}
				else if (kvs == listVal.others) {
					Set<String> set = strToSet(val);
					if (setNames(station, set)) {
						listSta.repaint();
					}
				}
				else if (kvs == listVal.book) {
					station.idBook = val.matches("[0-9]+") ? Integer.parseInt(val) : -1;
				}
				else if (kvs == listVal.search) {
					station.idSearch = val.matches("[0-9]+") ? Integer.parseInt(val) : -1;
				}
				else if (kvs == listVal.ch) {
					if (setName(station, true, val)) {
						listSta.repaint();
					}
				}
				else if (kvs == listVal.location) {
					station.location = val.matches("[0-9]+\\.[0-9]+") || val.matches("[0-9]+") ? Double.parseDouble(val) : 0.0;
				}
				listVal.show(station);
				listVal.getListSelectionListeners()[0].valueChanged(null);
			}
		};
		private final ListSelectionListener listListener = e -> {
			if (e == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == listSta.list) {
				listVal.show(listSta.list.getSelectedValue());
				int index = listSta.list.getSelectedIndex();
				listVal.getListSelectionListeners()[0].valueChanged(new ListSelectionEvent(listVal, index, index, false));
			}
			else {
				KeyValueString kvs = listVal.getSelectedValue();
				if (kvs == null) {
					textBox.setText(null);
					textBox.setEditable(false);
				}
				else {
					textBox.setText(kvs.getValue());
					textBox.setEditable(true);
				}
			}
		};

		private Manager() {
			super("Stations Loader");
			initSwings();
			initListeners();
			pack();
			setVisible(true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}

		private List<String> duplicate(Station old, Collection<String> list) {
			return list.stream().filter(name -> {
				Station tar = Station.getInstance(name);
				return tar != null && tar != old;
			}).collect(Collectors.toList());
		}

		private void initListeners() {
			listSta.list.addListSelectionListener(listListener);
			listVal.addListSelectionListener(listListener);
			textBox.addKeyListener(keyListener);
			searchBox.getDocument().addDocumentListener(typedListener);
			cbbCounty.addItemListener(itemListener);
			cbbLevel.addItemListener(itemListener);
			submit.addActionListener(buttonListener);
			add.addActionListener(buttonListener);
			remove.addActionListener(buttonListener);
			ListSelectionEvent e = new ListSelectionEvent(listSta, 0, 0, false);
			listSta.list.getListSelectionListeners()[0].valueChanged(e);
		}

		private List<Station> initStations() {
			try {
				return Init.instances;
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new Error("Initialization fails.");
			}
		}

		private void initSwings() {
			listSta = new StationList();
			listSta.setPreferredSize(new Dimension(150, listSta.getPreferredSize().height));
			listVal = new KeyValueList();
			listVal.setPreferredSize(new Dimension(300, listVal.getPreferredSize().height));
			message.setPreferredSize(new Dimension(75, message.getPreferredSize().height));
			searchBox.setPreferredSize(new Dimension(150, searchBox.getPreferredSize().height));

			cbbCounty = new JComboBox<>(new County[]{null, County.TAIPEI, County.TAOYUAN, County.HSINCHU, County.MIAOLI, County.TAICHUNG, County.CHANGHUA, County.YUNLIN,
					County.JIAYI, County.TAINAN, County.KAOSHIUNG, County.PINGTUNG, County.YILAN, County.HUALIEN, County.TAITUNG, County.NANTAO});
			StationLevel[] loaded = StationLevel.list();
			StationLevel[] boxed = new StationLevel[loaded.length + 1];
			System.arraycopy(loaded, 0, boxed, 1, loaded.length);
			boxed[0] = null;
			cbbLevel = new JComboBox<>(boxed);

			JPanel pnBtns = new JPanel(new GridLayout(1, 3));
			pnBtns.add(add);
			pnBtns.add(remove);
			pnBtns.add(submit);

			JPanel pnStaAndBtns = new JPanel(new BorderLayout());
			pnStaAndBtns.add(new PaddingPanel(10, listSta), BorderLayout.CENTER);
			pnStaAndBtns.add(new PaddingPanel(0, 10, 10, 10, pnBtns), BorderLayout.SOUTH);

			JPanel pnCbbs = new JPanel(new GridLayout(1, 2));
			pnCbbs.add(cbbCounty);
			pnCbbs.add(cbbLevel);

			JPanel pnCbbsAndSearch = new JPanel(new BorderLayout());
			pnCbbsAndSearch.add(pnCbbs, BorderLayout.CENTER);
			pnCbbsAndSearch.add(new PaddingPanel(0, 0, 0, 10, searchBox), BorderLayout.WEST);

			JPanel pnCbbsSearchAndVal = new JPanel(new BorderLayout());
			pnCbbsSearchAndVal.add(new PaddingPanel(10, listVal), BorderLayout.CENTER);
			pnCbbsSearchAndVal.add(new PaddingPanel(0, 10, 10, 10, pnCbbsAndSearch), BorderLayout.SOUTH);

			JPanel pnCenter = new JPanel(new BorderLayout());
			pnCenter.add(pnStaAndBtns, BorderLayout.WEST);
			pnCenter.add(pnCbbsSearchAndVal, BorderLayout.CENTER);

			JPanel pnSouth = new JPanel(new BorderLayout());
			pnSouth.add(textBox, BorderLayout.CENTER);
			pnSouth.add(new PaddingPanel(0, 10, 0, 0, message), BorderLayout.EAST);

			add(pnCenter, BorderLayout.CENTER);
			add(new PaddingPanel(0, 10, 10, 10, pnSouth), BorderLayout.SOUTH);
		}

		private boolean setName(Station station, boolean ch, String name) {
			Station target = Station.getInstance(name);
			if (target == null) {
				if (ch) {
					station.name = name;
				}
				else {
					station.enName = name;
				}
				return false;
			}
			if (target != station) {
				if (!warnOverwrite(name)) {
					return false;
				}
				boolean b = clearName(name, target);
				if (ch) {
					station.name = name;
				}
				else {
					station.enName = name;
				}
				return b;
			}
			return false;
		}

		private boolean setNames(Station station, Collection<String> names) {
			Set<String> set = new HashSet<>(names);
			List<String> dup = duplicate(station, set);
			System.out.println(dup);
			boolean e = dup.isEmpty();
			if (e) {
				station.otherNames.clear();
				station.otherNames.addAll(names);
				return false;
			}
			if (!warnOverwrite(dup)) {
				return false;
			}

			station.otherNames.retainAll(set);
			boolean b = false;
			for (String name : dup) {
				b = clearName(name, Station.getInstance(name)) || b; // Must calc the method.
				station.otherNames.add(name);
			}
			return b;
		}

		private boolean warnOverwrite(String name) {
			String mess = "Name " + name + " already exists. Sure to overwrite?";
			return JOptionPane.showConfirmDialog(this, mess, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
		}

		private boolean warnOverwrite(List<String> list) {
			String term = list.size() == 1 ? "exists" : "exist";
			String names = "\"" + String.join("\", \"", list) + "\"";
			String mess = "Name " + names + " already " + term + ". Sure to overwrite?";
			return JOptionPane.showConfirmDialog(this, mess, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
		}

		public static void main(String[] args) {
			new Manager();
		}
	}

	private static boolean clearName(String name, Station s) {
		if (name.equals(s.name)) {
			s.name = null;
			return true;
		}
		if (name.equals(s.enName)) {
			s.enName = null;
		}
		else {
			s.otherNames.remove(name);
		}
		return false;
	}

	/**
	 * Get all stations.
	 * @return a list contains all stations.
	 */
	public static List<Station> all() {
		return new ArrayList<>(Init.instances);
	}

	/**
	 * Know if a station getDuplicatedStations.
	 * @param name the Chinese or English name of a station. Note that letter '?x' and '?O' are generic.
	 *             Name ended with string "????","??", "station" or "Station" is allowed.
	 * @return true if a station getDuplicatedStations.
	 */
	public static boolean exists(String name) {
		final String stdName = toStdName(name);
		return Init.instances.stream().anyMatch(s -> stdName.equals(s.name) || stdName.equals(s.enName) || s.otherNames.contains(stdName));
	}

	/**
	 * Get a <code>Station</code> object by the name of station.
	 * @param name the Chinese or English name of a station. Note that Chinese letter '?' and '?' are
	 *             generic. Name ended with string "??","?", "station" or "Station" is allowed.
	 * @return a <tt>Station</tt> object or null if that station does not exist.
	 * @see #toStdName(String)
	 */
	public static Station getInstance(String name) {
		final String stdName = toStdName(name);
		return Init.instances.stream().filter(s -> stdName.equals(s.name) || stdName.equals(s.enName) || s.otherNames.contains(stdName)).findFirst().orElse(null);
	}

	/**
	 * Get the standardized name of a given string. If the string ends with "??", "?", "Station" or
	 * "station", the suffix is removed. The initial will be changed to upper case. Here are
	 * some examples demonstrates how a string is standardize. <ul> <li>???? -> ??</li> <li>??? -> ??</li>
	 * <li>Taitung Station -> Taitung</li> <li>keelung station -> Keelung</li> </ul>
	 * @param str a string to be standardize.
	 * @return a standardized name.
	 */
	public static String toStdName(String str) {
		if (str.length() == 0) {
			return str;
		}
		str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
		if (str.endsWith("??")) {
			return str.substring(0, str.length() - 2);
		}
		else if (str.endsWith("?")) {
			return str.substring(0, str.length() - 1);
		}
		else if (str.endsWith(" station") || str.endsWith(" Station")) {
			return str.substring(0, str.length() - 8);
		}
		return str;
	}

	private County county;
	private String enName;
	private int idBook = -1;
	private int idSearch = -1;
	private StationLevel level;
	private double location;
	private String name;
	private Set<String> otherNames = new HashSet<>();

	private Station() {
	}

	private Station(String name) {
		this.name = name;
	}

	boolean addName(String name) {
		if (name == null) {
			return false;
		}
		Station s = getInstance(name);
		if (s != this && s.otherNames.remove(name)) {
			otherNames.add(name);
			return true;
		}
		return false;
	}

	boolean addNames(Collection<String> names) {
		boolean b = false;
		for (String name : names) {
			if (addName(name)) {
				b = true;
			}
		}
		return b;
	}

	boolean removeName(String name) {
		return otherNames.remove(name);
	}

	void setNames(Collection<String> otherNames) {
		otherNames.forEach(this::addName);
	}



	/**
	 * To check if this station is at a route.
	 * @param route argument route.
	 * @return true if this station is at a route.
	 */
	public boolean at(Route route) {
		if (route == null) {
			throw new NullPointerException();
		}
		return route.contains(this);
	}

	/**
	 * Get the route where both this station and another station are.
	 * @param station another station.
	 * @return the route where both this station and another station are, or null if these two station
	 * are not at the same route.
	 */
	public boolean atSameRoute(Station station) {
		Route r = getRoute();
		return r != null && r == station.getRoute();
	}

	@Override
	public int compareTo(Station anotherStation) {
		if (anotherStation.location == 0.0) {
			if (name == null) {
				return 1;
			}
			if (anotherStation.name == null) {
				return -1;
			}
			return name.compareTo(anotherStation.name);
		}
		return location < anotherStation.location ? -1 : 1;
	}

	/**
	 * Get the county of this station.
	 * @return the county of this station, or null if county is unknown.
	 * @see County
	 */
	public County getCounty() {
		return county;
	}

	/**
	 * Get the official English name of this station.
	 * @return the official English name of this station.
	 */
	public String getEnName() {
		return enName;
	}

	/**
	 * Get the station id used to book tickets.
	 * @return the station id used to book tickets.
	 */
	public int getIdBook() {
		return idBook;
	}

	/**
	 * Get the id used to search timetables.
	 * @return the id used to search timetables.
	 */
	public int getIdSearch() {
		return idSearch;
	}

	/**
	 * Get the level of this station.
	 * @return the level of this station, or null if level is unknown.
	 * @see StationLevel
	 */
	public StationLevel getLevel() {
		return level;
	}

	public double getLocation() {
		return location;
	}

	/**
	 * Get the official Chinese name of this station.
	 * @return the the official Chinese name of this station.
	 */
	public String getName() {
		return name;
	}

	/**
	 * To verify a <tt>Set</tt> that contains all the names of this station, including its official
	 * Chinese name and official English name.
	 * @return a <tt>Set</tt> that contains all the names of this station, including its official Chinese
	 * name and official English name, or an empty list if this station has no other names.
	 */
	public List<String> getNames() {
		List<String> names = new ArrayList<>();
		if (name != null) {
			names.add(name);
		}
		if (enName != null) {
			names.add(enName);
		}
		names.addAll(otherNames);
		return names;
	}

	/**
	 * To verify a <tt>Set</tt> that contains all the names of this station, except its official Chinese
	 * name and official English name.
	 * @return a <tt>Set</tt> that contains all the names of this station, except its official Chinese
	 * name and official English name, or an empty list if this station has no other names.
	 */
	public List<String> getOtherNames() {
		return new ArrayList<>(otherNames);
	}

	/**
	 * To verify the route where both this station and another station are.
	 * @param anotherStation argument station.
	 * @return a <tt>Route</tt> object indicates the route where both this station and another station
	 * are, or null if these stations are not at the same route.
	 */
	public Route getRoute(Station anotherStation) {
		Route found = null;
		for (Route r : Route.all()) {
			if (r.contains(this) && r.contains(anotherStation)) {
				if (found != null) {
					return null; // Two or more routes are found;
				}
				found = r;
			}
		}
		return found;
	}

	/**
	 * <p> Get the route where this station is located. If this station is the junction of two routes or
	 * more,then returns the larger route. Main routes are larger than branch routes. Main routes
	 * startTime large to small are </p> <p> <ol> <li>Western Line</li> <li>Mountain Line,
	 * Coast Line and Taitung Line</li> <li>Northern Line and Southern Line</li> <li>Pingtung Line, YiLan
	 * Line and ChenZhui Line</li> </ol> </p> <p> Neiwan-Liujia Line is larger than Neiwan Line and
	 * Liujia Line. </p>
	 * @return the route where this station is located.
	 */
	public Route getRoute() {
		for (Route r : Route.all()) {
			if (r.contains(this)) {
				return r;
			}
		}
		return null;
	}

	/**
	 * To check if this station has a given name.
	 * @param name a name to be chekced.
	 * @return true if this station has a given name.
	 */
	public boolean is(String name) {
		return name.equals(this.name) || name.equals(enName) || otherNames.contains(name);
	}

	@Override
	public String toString() {
		if (name != null) {
			return name;
		}
		if (enName != null) {
			return enName;
		}
		if (!otherNames.isEmpty()) {
			return otherNames.iterator().next();
		}
		return "<???>";
	}
}

