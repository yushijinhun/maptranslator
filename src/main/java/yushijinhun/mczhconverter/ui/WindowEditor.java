package yushijinhun.mczhconverter.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.json.JSONObject;
import org.json.JSONTokener;
import yushijinhun.mczhconverter.core.NBTDescriptorSet;
import yushijinhun.mczhconverter.core.NBTStringVisitorApply;
import yushijinhun.mczhconverter.core.NBTStringVisitorLookup;
import yushijinhun.mczhconverter.core.NBTStringVisitorLookupUnicodeChar;
import yushijinhun.mczhconverter.util.ParallelUtil;
import com.spreada.utils.chinese.ZHConverter;

public class WindowEditor extends JFrame {

	private static final long serialVersionUID = 1L;

	public class PatchTableModel implements TableModel {

		@Override
		public int getRowCount() {
			synchronized (editlock) {
				return rows == null ? 0 : rows.size();
			}
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnIndex == 0 ? "Source" : "Target";
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex != 0) && (rows != null);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			synchronized (editlock) {
				if ((rows == null) || (patch == null)) {
					return null;
				}
				if (columnIndex == 0) {
					return rows.get(rowIndex);
				}
				return patch.get(rows.get(rowIndex));
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			synchronized (editlock) {
				if ((rows == null) || (patch == null)) {
					return;
				}
				if (columnIndex == 0) {
					String oldkey = rows.get(rowIndex);
					String target = patch.get(oldkey);
					patch.remove(oldkey);
					patch.put((String) aValue, target);
					rows.put(rowIndex, (String) aValue);
				} else {
					patch.put(rows.get(rowIndex), (String) aValue);
				}
			}
		}

		@Override
		public void addTableModelListener(TableModelListener l) {
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
		}
	}

	protected Logger logger = Logger.getLogger(getClass().getCanonicalName());

	private NBTDescriptorSet descriptorSet;
	private JTable table;
	private JLabel labelProgress;
	private Map<String, String> patch;
	private Map<Integer, String> rows;
	private Object editlock = new Object();
	private ExecutorService pool = Executors.newSingleThreadExecutor();
	private String regex = null;
	private String replace = null;

	public WindowEditor(NBTDescriptorSet descriptorSet) {
		this.descriptorSet = descriptorSet;

		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel bottompanel = new JPanel();
		getContentPane().add(bottompanel, BorderLayout.SOUTH);
		bottompanel.setLayout(new BorderLayout(0, 0));

		JPanel contorlpane = new JPanel();
		bottompanel.add(contorlpane, BorderLayout.WEST);
		GridBagLayout gbl_contorlpane = new GridBagLayout();
		gbl_contorlpane.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_contorlpane.rowHeights = new int[] { 0, 0 };
		gbl_contorlpane.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_contorlpane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		contorlpane.setLayout(gbl_contorlpane);

		JButton buttonApply = new JButton("Apply");
		GridBagConstraints gbc_buttonApply = new GridBagConstraints();
		gbc_buttonApply.insets = new Insets(5, 5, 5, 5);
		gbc_buttonApply.gridx = 0;
		gbc_buttonApply.gridy = 0;
		contorlpane.add(buttonApply, gbc_buttonApply);

		JButton buttonReload = new JButton("Reload");
		GridBagConstraints gbc_buttonReload = new GridBagConstraints();
		gbc_buttonReload.insets = new Insets(5, 5, 5, 5);
		gbc_buttonReload.gridx = 1;
		gbc_buttonReload.gridy = 0;
		contorlpane.add(buttonReload, gbc_buttonReload);

		JButton buttonImport = new JButton("Import");
		GridBagConstraints gbc_buttonImport = new GridBagConstraints();
		gbc_buttonImport.insets = new Insets(5, 5, 5, 5);
		gbc_buttonImport.gridx = 2;
		gbc_buttonImport.gridy = 0;
		contorlpane.add(buttonImport, gbc_buttonImport);

		JButton buttonExport = new JButton("Export");
		GridBagConstraints gbc_buttonExport = new GridBagConstraints();
		gbc_buttonExport.insets = new Insets(5, 5, 5, 5);
		gbc_buttonExport.gridx = 3;
		gbc_buttonExport.gridy = 0;
		contorlpane.add(buttonExport, gbc_buttonExport);

		JButton buttonAutoConvert = new JButton("Auto Convert");
		GridBagConstraints gbc_buttonAutoConvert = new GridBagConstraints();
		gbc_buttonAutoConvert.gridx = 4;
		gbc_buttonAutoConvert.gridy = 0;
		contorlpane.add(buttonAutoConvert, gbc_buttonAutoConvert);

		JPanel progresspanel = new JPanel();
		bottompanel.add(progresspanel, BorderLayout.EAST);
		GridBagLayout gbl_progresspanel = new GridBagLayout();
		gbl_progresspanel.columnWidths = new int[] { 0, 0 };
		gbl_progresspanel.rowHeights = new int[] { 0, 0 };
		gbl_progresspanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_progresspanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		progresspanel.setLayout(gbl_progresspanel);

		labelProgress = new JLabel("");
		GridBagConstraints gbc_labelProgress = new GridBagConstraints();
		gbc_labelProgress.insets = new Insets(5, 5, 5, 5);
		gbc_labelProgress.gridx = 0;
		gbc_labelProgress.gridy = 0;
		progresspanel.add(labelProgress, gbc_labelProgress);

		JPanel tablepanel = new JPanel();
		getContentPane().add(new JScrollPane(tablepanel), BorderLayout.CENTER);
		GridBagLayout gbl_tablepanel = new GridBagLayout();
		gbl_tablepanel.columnWidths = new int[] { 0, 0 };
		gbl_tablepanel.rowHeights = new int[] { 0, 0 };
		gbl_tablepanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_tablepanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		tablepanel.setLayout(gbl_tablepanel);

		table = new JTable(new PatchTableModel());
		GridBagConstraints gbc_table = new GridBagConstraints();
		gbc_table.insets = new Insets(5, 5, 5, 5);
		gbc_table.fill = GridBagConstraints.BOTH;
		gbc_table.gridx = 0;
		gbc_table.gridy = 0;
		tablepanel.add(table, gbc_table);

		setTitle("MCZHConverter");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				WindowEditor.this.descriptorSet.close();
				WindowEditor.this.pool.shutdownNow();
				System.exit(0);
			}
		});
		buttonReload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pool.submit(new Runnable() {

					@Override
					public void run() {
						doLoad();
					}
				});
			}
		});
		buttonExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pool.submit(new Runnable() {

					@Override
					public void run() {
						doExport();
					}
				});
			}
		});
		buttonImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pool.submit(new Runnable() {

					@Override
					public void run() {
						doImport();
					}
				});
			}
		});
		buttonApply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pool.submit(new Runnable() {

					@Override
					public void run() {
						doApply();
					}
				});
			}
		});
		buttonAutoConvert.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pool.submit(new Runnable() {

					@Override
					public void run() {
						doAutoConvert();
					}
				});
			}
		});
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F9) {
					findPerv();
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F8) {
					findNext();
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F)) {
					inputFind();
					findNext();
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_R)) {
					if ((regex == null) || regex.isEmpty()) {
						inputFind();
					}
					inputReplace();
					replaceAndFind();
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F10) {
					replace();
				}
			}
		});

		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F11) {
					replaceAndFind();
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F12) {
					replaceAll();
				}
			}
		});
		setSize(640, 480);
		pool.submit(new Runnable() {

			@Override
			public void run() {
				doLoad();
			}
		});
	}

	public void setProgress(String str) {
		labelProgress.setText((str == null) || str.equals("") ? "Done. " + rows.size() + " rows" : str);
	}

	public void doLoad() {
		synchronized (descriptorSet) {
			setProgress("Loading...");
			NBTStringVisitorLookup visitor = new NBTStringVisitorLookupUnicodeChar();
			try {
				ParallelUtil.waitTasks(descriptorSet.accpetVisitor(visitor, false));
			} catch (InterruptedException e) {
				;
			}
			setProgress("Converting...");
			Map<String, String> patch = new TreeMap<>();
			Map<Integer, String> rows = new LinkedHashMap<>();
			for (String str : visitor.getStrings()) {
				patch.put(str, str);
			}
			int count = 0;
			for (String key : patch.keySet()) {
				rows.put(count, key);
				count++;
			}
			synchronized (editlock) {
				this.patch = patch;
				this.rows = rows;
			}
			table.updateUI();
			setProgress("");
		}
	}

	public void doExport() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Export");
		chooser.showSaveDialog(this);
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		synchronized (editlock) {
			logger.info(String.format("Exporting to %s", file.getPath()));
			setProgress("Exporting...");
			try {
				JSONObject json = new JSONObject();
				for (Entry<String, String> entry : patch.entrySet()) {
					json.put(entry.getKey(), entry.getValue());
				}
				try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
					writer.write(json.toString(4));
				} catch (IOException e) {
					logger.log(Level.SEVERE, String.format("Failed to export to %s", file.getPath()), e);
					JOptionPane.showMessageDialog(this, "Failed to export: " + e.getClass().getCanonicalName() + ":" + e.getMessage(), "Export", JOptionPane.ERROR_MESSAGE);
				}
			} finally {
				setProgress("");
			}
		}
	}

	public void doImport() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Import");
		chooser.showOpenDialog(this);
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		if (!file.exists()) {
			JOptionPane.showMessageDialog(this, "File does not exist", "Import", JOptionPane.ERROR_MESSAGE);
			return;
		}
		WindowImportModeSelector modeSelector = new WindowImportModeSelector(this);
		modeSelector.setTitle("Import");
		modeSelector.setVisible(true);
		int mode = modeSelector.getMode();
		if (mode == -1) {
			return;
		}

		logger.info(String.format("Importing from %s", file.getPath()));
		setProgress("Importing...");
		try {
			JSONObject json;
			try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
				json = (JSONObject) new JSONTokener(reader).nextValue();
			} catch (IOException e) {
				logger.log(Level.SEVERE, String.format("Failed to import from %s", file.getPath()), e);
				JOptionPane.showMessageDialog(this, "Failed from import: " + e.getClass().getCanonicalName() + ":" + e.getMessage(), "Import", JOptionPane.ERROR_MESSAGE);
				return;
			}
			Map<String, String> patch = new TreeMap<>();
			Map<Integer, String> rows = new LinkedHashMap<>();

			switch (mode) {
			case WindowImportModeSelector.MERGE_KEEP:
				for (String key : json.keySet()) {
					patch.put(key, json.getString(key));
				}
				synchronized (editlock) {
					patch.putAll(this.patch);
				}
				break;

			case WindowImportModeSelector.MERGE_REPLACE:
				synchronized (editlock) {
					patch.putAll(this.patch);
				}
				for (String key : json.keySet()) {
					patch.put(key, json.getString(key));
				}
				break;

			case WindowImportModeSelector.NOMERGE_REPLACE:
				for (String key : json.keySet()) {
					patch.put(key, json.getString(key));
				}
				break;
			}

			int count = 0;
			for (String key : patch.keySet()) {
				rows.put(count, key);
				count++;
			}

			synchronized (editlock) {
				this.patch = patch;
				this.rows = rows;
			}
			table.updateUI();
		} finally {
			setProgress("");
		}
	}

	public void doApply() {
		setProgress("Applying...");
		try {
			Map<String, String> copy = new HashMap<>();
			synchronized (editlock) {
				copy.putAll(patch);
			}
			try {
				ParallelUtil.waitTasks(descriptorSet.accpetVisitor(new NBTStringVisitorApply(copy), true));
			} catch (InterruptedException e) {
				;
			}
		} finally {
			setProgress("");
		}
	}

	public void doAutoConvert() {
		setProgress("Converting...");
		try {
			ZHConverter zhconverter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
			synchronized (editlock) {
				for (String key : patch.keySet()) {
					patch.put(key, zhconverter.convert(patch.get(key)));
				}
			}
			table.updateUI();
		} finally {
			setProgress("");
		}
	}

	public void findNext() {
		if ((regex == null) || regex.isEmpty()||(table.getSelectedRow()==(rows.size()-1))) {
			return;
		}
		for (int i = table.getSelectedRow()+1;i < rows.size(); i++) {
			String str = patch.get(rows.get(i));
			if (Pattern.compile(regex).matcher(str).find()) {
				scrollToRow(i);
				break;
			}
		}
	}

	public void findPerv() {
		if ((regex == null) || regex.isEmpty()||(table.getSelectedRow()==0)) {
			return;
		}
		for (int i = table.getSelectedRow()-1; i > -1; i--) {
			String str = patch.get(rows.get(i));
			if (Pattern.compile(regex).matcher(str).find()) {
				scrollToRow(i);
				break;
			}
		}
	}

	public void scrollToRow(int i) {
		table.setRowSelectionInterval(i, i);
		Rectangle rect = table.getCellRect(i, 1, true);
		table.updateUI();
		table.scrollRectToVisible(rect);
	}

	public void replace() {
		if ((regex == null) || regex.isEmpty() || (replace == null)) {
			return;
		}
		int i = table.getSelectedRow();
		patch.put(rows.get(i), patch.get(rows.get(i)).replaceAll(regex, replace));
		table.updateUI();
	}

	public void replaceAll() {
		if ((regex == null) || regex.isEmpty() || (replace == null)) {
			return;
		}
		for (int i = 0; i < rows.size(); i++) {
			patch.put(rows.get(i), patch.get(rows.get(i)).replaceAll(regex, replace));
		}
		table.updateUI();
	}

	public void replaceAndFind() {
		replace();
		findNext();
	}

	private void inputFind() {
		regex = (String) JOptionPane.showInputDialog(this, "<F8> to find next. <F9> to find perv. Support regex. Case sensitive.", "Find", JOptionPane.QUESTION_MESSAGE, null, null, regex);
	}

	private void inputReplace() {
		replace = (String) JOptionPane.showInputDialog(this, "<F8> to find next. <F9> to find perv. <F10> to replace. <F11> to replace/find. <F12> to replace all. Support regex. Case sensitive.", "Replace", JOptionPane.QUESTION_MESSAGE, null, null, replace);
	}
}
