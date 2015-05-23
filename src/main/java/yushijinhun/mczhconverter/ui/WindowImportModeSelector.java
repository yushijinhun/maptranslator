package yushijinhun.mczhconverter.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class WindowImportModeSelector extends JDialog {

	public static final int MERGE_REPLACE = 0;
	public static final int MERGE_KEEP = 1;
	public static final int NOMERGE_REPLACE = 2;

	private static final long serialVersionUID = 1L;

	private final ButtonGroup buttonGroupMethod = new ButtonGroup();
	private JButton buttonOK;
	private JRadioButton radioNomergeReplace;
	private JRadioButton radioMergeKeep;
	private JRadioButton radioMergeReplace;
	private int selection;

	public WindowImportModeSelector(Frame parent) {
		super(parent, true);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JLabel labelTip = new JLabel("If two rows are the same, you want to");
		GridBagConstraints gbc_labelTip = new GridBagConstraints();
		gbc_labelTip.insets = new Insets(0, 0, 5, 0);
		gbc_labelTip.gridx = 0;
		gbc_labelTip.gridy = 0;
		getContentPane().add(labelTip, gbc_labelTip);

		radioMergeReplace = new JRadioButton("Replace the same row");
		buttonGroupMethod.add(radioMergeReplace);
		GridBagConstraints gbc_radioMergeReplace = new GridBagConstraints();
		gbc_radioMergeReplace.anchor = GridBagConstraints.WEST;
		gbc_radioMergeReplace.insets = new Insets(0, 0, 5, 0);
		gbc_radioMergeReplace.gridx = 0;
		gbc_radioMergeReplace.gridy = 1;
		getContentPane().add(radioMergeReplace, gbc_radioMergeReplace);

		radioMergeKeep = new JRadioButton("Keep the same row");
		buttonGroupMethod.add(radioMergeKeep);
		GridBagConstraints gbc_radioMergeKeep = new GridBagConstraints();
		gbc_radioMergeKeep.anchor = GridBagConstraints.WEST;
		gbc_radioMergeKeep.insets = new Insets(0, 0, 5, 0);
		gbc_radioMergeKeep.gridx = 0;
		gbc_radioMergeKeep.gridy = 2;
		getContentPane().add(radioMergeKeep, gbc_radioMergeKeep);

		radioNomergeReplace = new JRadioButton("Delete all the existing rows");
		buttonGroupMethod.add(radioNomergeReplace);
		GridBagConstraints gbc_radioNomergeReplace = new GridBagConstraints();
		gbc_radioNomergeReplace.insets = new Insets(0, 0, 5, 0);
		gbc_radioNomergeReplace.anchor = GridBagConstraints.WEST;
		gbc_radioNomergeReplace.gridx = 0;
		gbc_radioNomergeReplace.gridy = 3;
		getContentPane().add(radioNomergeReplace, gbc_radioNomergeReplace);

		buttonOK = new JButton("OK");
		GridBagConstraints gbc_buttonOK = new GridBagConstraints();
		gbc_buttonOK.gridx = 0;
		gbc_buttonOK.gridy = 4;
		getContentPane().add(buttonOK, gbc_buttonOK);

		radioMergeReplace.setSelected(true);
		radioMergeKeep.setSelected(false);
		radioNomergeReplace.setSelected(false);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (radioMergeReplace.isSelected()) {
					selection = MERGE_REPLACE;
				} else if (radioMergeKeep.isSelected()) {
					selection = MERGE_KEEP;
				} else if (radioNomergeReplace.isSelected()) {
					selection = NOMERGE_REPLACE;
				}
				setVisible(false);
				dispose();
			}
		});
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				selection = -1;
			}
		});
		pack();
	}

	public int getMode() {
		return selection;
	}
}
