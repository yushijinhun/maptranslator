package yushijinhun.mczhconverter.ui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

public class TreeShowingWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	public JTree tree;

	public TreeShowingWindow() {
		super("NBT Tree");
		tree = new JTree();
		add(new JScrollPane(tree));
		setSize(300, 600);
		tree.setExpandsSelectedPaths(true);
	}

}
