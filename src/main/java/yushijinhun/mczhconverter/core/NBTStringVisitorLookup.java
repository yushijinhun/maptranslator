package yushijinhun.mczhconverter.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.tree.DefaultMutableTreeNode;
import yushijinhun.mczhconverter.filter.TraceFiltering;
import yushijinhun.mczhconverter.trace.NodeTrace;

public class NBTStringVisitorLookup implements NBTStringVisitor {

	public Map<String, Set<String>> paths = new LinkedHashMap<>();
	public DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	public Map<String, DefaultMutableTreeNode> path2node = new HashMap<>();

	@Override
	public String visit(String str, Stack<NodeTrace> trace) {
		if (TraceFiltering.shouldIgnore(str, trace))
			return null;
		if (!paths.containsKey(str)) paths.put(str, new LinkedHashSet<String>());
		String p = TraceFiltering.toStringTrace(trace);
		paths.get(str).add(p);
		path2node.put(p, addtotree(str, trace));
		return null;
	}

	private DefaultMutableTreeNode addtotree(String str, Stack<NodeTrace> trace) {
		DefaultMutableTreeNode node = root;
		label:
		for (int i = 0; i < trace.size(); i++) {
			NodeTrace t = trace.get(i);
			for (int l = 0; l < node.getChildCount(); l++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(l);
				if (child.getUserObject() == t) {
					node = child;
					continue label;
				}
			}
			DefaultMutableTreeNode newch = new DefaultMutableTreeNode(t);
			node.add(newch);
			node = newch;
		}
		return node;
	}

}
