package org.to2mbn.maptranslator.process;

import java.util.function.Predicate;
import org.to2mbn.maptranslator.tree.ListNode;
import org.to2mbn.maptranslator.tree.MapNode;
import org.to2mbn.maptranslator.tree.Node;

public class NodeMatcher implements Predicate<Node> {

	public static NodeMatcher of(String exp) {
		return new NodeMatcher(exp);
	}

	private String[] paths;
	private Integer[] ints;
	private String[][] tags;

	public NodeMatcher(String exp) {
		String[] splited = exp.split("/");
		paths = new String[splited.length];
		tags = new String[splited.length][];
		ints = new Integer[splited.length];
		for (int i = 0; i < splited.length; i++) {
			String path = splited[i];
			int kuohao = path.indexOf('(');
			if (kuohao == -1) {
				if (path.equals("*")) {
					path = null;
				}
			} else {
				tags[i] = path.substring(kuohao + 1, path.length() - 1).split(",");
				path = path.substring(0, kuohao);
				if (path.length() == 0) {
					path = null;
				}
			}
			paths[i] = path;
			if (path != null) {
				try {
					ints[i] = Integer.valueOf(path);
				} catch (NumberFormatException e) {
					ints[i] = null;
				}
			}
		}
	}

	@Override
	public boolean test(Node node) {
		for (int i = paths.length - 1; i >= 0; i--) {
			if (node == null)
				return false;
			if (paths[i] != null)
				if ((node instanceof MapNode && !paths[i].equals(((MapNode) node).key())) ||
						(node instanceof ListNode && (ints[i] == null || ints[i] != ((ListNode) node).index())))
					return false;
			if (tags[i] != null)
				for (String tag : tags[i])
				if (!node.hasTag(tag))
					return false;
			node = node.parent();
		}
		return true;
	}

}
