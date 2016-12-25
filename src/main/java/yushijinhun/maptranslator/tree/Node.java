package yushijinhun.maptranslator.tree;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class Node {

	private Set<String> tags = new TreeSet<>();
	private Set<Node> children = new LinkedHashSet<>();
	private Set<Node> unmodifiableChildren = Collections.unmodifiableSet(children);
	private Node parent;
	private Map<String, Object> properties = new HashMap<>();

	public Set<String> tags() {
		return tags;
	}

	public Set<Node> unmodifiableChildren() {
		return unmodifiableChildren;
	}

	public Node parent() {
		return parent;
	}

	public Map<String, Object> properties() {
		return properties;
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

	public boolean hasTag(String str) {
		if (tags.contains(str)) return true;
		for (String pattern : tags) {
			if (pattern.indexOf('*') != -1) {
				int r = -1;
				int n = 0;
				do {
					int l = r + 1;
					r = pattern.indexOf('*', l);
					if (r == -1) r = pattern.length();
					n = str.indexOf(pattern.substring(l, r), n);
					if (n == -1) break;
					n += r - l;
				} while (r < pattern.length());
				if (n != -1) return true;
			}
		}
		return false;
	}

	// for method-chain
	public Node withTag(String tag) {
		tags().add(tag);
		return this;
	}

	boolean runTagMarking(TagMarker marker) {
		boolean changed = false;
		if (marker.condition.test(this)) {
			for (String tag : marker.tags.apply(this)) {
				if (!hasTag(tag)) {
					tags.add(tag);
					changed = true;
				}
			}
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
				if (newChild != child) {
					child.parent = null;
					newChild.parent = this;

					// re-insert all nodes to ensure the order
					Set<Node> copied = new LinkedHashSet<>(children);
					children.clear();
					for (Node ch : copied) {
						if (ch == child) ch = newChild;
						children.add(ch);
					}
				}
			} else {
				changed |= child.runNodeReplacing(replacer);
			}
		}
		return changed;
	}

}
