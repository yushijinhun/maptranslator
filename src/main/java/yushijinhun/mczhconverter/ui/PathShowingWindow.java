package yushijinhun.mczhconverter.ui;

import java.awt.Rectangle;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class PathShowingWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	public JList<String> table;
	public Vector<String> data;

	public PathShowingWindow() {
		super("NBT Paths");
		data = new Vector<>();
		table = new JList<>(data);
		add(new JScrollPane(table));
		setSize(300, 600);
	}

	public void scrollToRow(int i) {
		table.setSelectionInterval(i, i);
		Rectangle rect = table.getCellBounds(i, i);
		table.updateUI();
		table.scrollRectToVisible(rect);
	}

}
