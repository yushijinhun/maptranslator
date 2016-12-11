package yushijinhun.mczhconverter.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import yushijinhun.mczhconverter.core.NBTDescriptorFactory;
import yushijinhun.mczhconverter.core.NBTDescriptorSet;

public class WindowOpenLevel extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField textThreads;
	private JTextField textLevel;
	private JButton buttonOpen;
	private JButton buttonOpenLevel;

	protected Logger logger = Logger.getLogger(getClass().getCanonicalName());

	public WindowOpenLevel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel labelThreads = new JLabel("Threads:");
		GridBagConstraints gbc_labelThreads = new GridBagConstraints();
		gbc_labelThreads.insets = new Insets(5, 5, 5, 5);
		gbc_labelThreads.gridx = 0;
		gbc_labelThreads.gridy = 0;
		getContentPane().add(labelThreads, gbc_labelThreads);

		textThreads = new JTextField("32");
		GridBagConstraints gbc_textThreads = new GridBagConstraints();
		gbc_textThreads.insets = new Insets(5, 5, 5, 5);
		gbc_textThreads.fill = GridBagConstraints.HORIZONTAL;
		gbc_textThreads.gridx = 1;
		gbc_textThreads.gridy = 0;
		getContentPane().add(textThreads, gbc_textThreads);
		textThreads.setColumns(10);

		JLabel labelLevel = new JLabel("Level:");
		GridBagConstraints gbc_labelLevel = new GridBagConstraints();
		gbc_labelLevel.anchor = GridBagConstraints.EAST;
		gbc_labelLevel.insets = new Insets(5, 5, 5, 5);
		gbc_labelLevel.gridx = 0;
		gbc_labelLevel.gridy = 1;
		getContentPane().add(labelLevel, gbc_labelLevel);

		textLevel = new JTextField();
		GridBagConstraints gbc_textLevel = new GridBagConstraints();
		gbc_textLevel.insets = new Insets(5, 5, 5, 5);
		gbc_textLevel.fill = GridBagConstraints.HORIZONTAL;
		gbc_textLevel.gridx = 1;
		gbc_textLevel.gridy = 1;
		getContentPane().add(textLevel, gbc_textLevel);
		textLevel.setColumns(10);

		buttonOpen = new JButton("...");
		GridBagConstraints gbc_buttonOpen = new GridBagConstraints();
		gbc_buttonOpen.insets = new Insets(5, 5, 5, 5);
		gbc_buttonOpen.anchor = GridBagConstraints.NORTH;
		gbc_buttonOpen.gridx = 2;
		gbc_buttonOpen.gridy = 1;
		getContentPane().add(buttonOpen, gbc_buttonOpen);

		buttonOpenLevel = new JButton("Open Level");
		GridBagConstraints gbc_buttonOpenLevel = new GridBagConstraints();
		gbc_buttonOpenLevel.insets = new Insets(5, 5, 5, 5);
		gbc_buttonOpenLevel.gridx = 1;
		gbc_buttonOpenLevel.gridy = 2;
		getContentPane().add(buttonOpenLevel, gbc_buttonOpenLevel);

		setTitle("Open Level");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		buttonOpen.addActionListener(new ActionListener() {

			private JFileChooser chooser = new JFileChooser();

			{
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				chooser.showOpenDialog(WindowOpenLevel.this);
				if (chooser.getSelectedFile() != null) {
					textLevel.setText(chooser.getSelectedFile().getPath());
				}
			}
		});
		buttonOpenLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int threads;
				try {
					threads = Integer.parseInt(textThreads.getText());
				} catch (NumberFormatException e1) {
					showNonNumberDialog();
					return;
				}
				if (threads < 1) {
					showNonNumberDialog();
				}

				File file = new File(textLevel.getText());
				if (!file.exists()) {
					JOptionPane.showMessageDialog(WindowOpenLevel.this, "File does not exist", "Open Level", JOptionPane.ERROR_MESSAGE);
					return;
				}

				openLevel(threads, file);
			}

			private void showNonNumberDialog() {
				JOptionPane.showMessageDialog(WindowOpenLevel.this, textThreads.getText() + " is not an available number", "Open Level", JOptionPane.ERROR_MESSAGE);
			}
		});
		pack();
	}

	protected void openLevel(int threads, File file) {
		NBTDescriptorSet descriptorSet = NBTDescriptorFactory.createDescriptorSet(file, threads);
		logger.info(String.format("DescriptorSet created %s", descriptorSet));
		setVisible(false);
		dispose();
		new WindowEditor(descriptorSet).setVisible(true);
	}
}
