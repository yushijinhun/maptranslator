package yushijinhun.maptranslator.tree;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class Node {

	private Set<String> tags = new TreeSet<>();
	private Set<Node> children = new LinkedHashSet<>();
	private Set<Node> unmodifiableChildren = Collections.unmodifiableSet(children);
	private Node parent;

	public Set<String> tags() {
		return tags;
	}

	public Set<Node> unmodifiableChildren() {
		return unmodifiableChildren;
	}

	public Node parent() {
		return parent;
	}

	public void addChild(Node child) {
		if (child.parent != null) throw new IllegalArgumentException("parent node already exists");
		child.parent = this;
		children.add(child);
	}

	public void removeChild(Node child) {
		if (child.parent != this) throw new IllegalArgumentException("not this node's child");
		child.parent = null;
		children.remove(child);
	}

	boolean runTagMarking(TagMarker marker) {
		boolean changed = false;
		if (marker.condition.test(this)) {
			changed |= tags.addAll(marker.tags);
		}
		for (Node child : children) {
			changed |= child.runTagMarking(marker);
		}
		return changed;
	}

	boolean runNodeReplacing(NodeReplacer replacer) {
		boolean changed = false;
		for (Node child : children) {
			if (replacer.condition.test(child)) {
				changed = true;
				Node newChild = replacer.replacer.apply(child);
				child.parent = null;
				newChild.parent = this;

				// re-insert all nodes to ensure the order
				Set<Node> copied = new LinkedHashSet<>(children);
				children.clear();
				for (Node ch : copied) {
					if (ch == child) ch = newChild;
					children.add(ch);
				}
			} else {
				changed |= child.runNodeReplacing(replacer);
			}
		}
		return changed;
	}

}
