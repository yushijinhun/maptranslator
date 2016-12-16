package yushijinhun.maptranslator.tree;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class Node {

	private Set<String> tags = Collections.synchronizedSet(new TreeSet<>());
	private Set<Node> children = Collections.synchronizedSet(new LinkedHashSet<>());
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

}
