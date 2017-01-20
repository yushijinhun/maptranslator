package org.to2mbn.maptranslator.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

	public void travel(Consumer<Node> visitor) {
		visitor.accept(this);
		children.forEach(child -> child.travel(visitor));
	}

	public String getDisplayText() {
		return toString();
	}

	public String getPath() {
		List<InPathNode> chain = new ArrayList<>();
		Node node = this;
		while (node != null) {
			if (node instanceof InPathNode)
				chain.add((InPathNode) node);
			node = node.parent();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = chain.size() - 1; i >= 0; i--) {
			sb.append('/');
			sb.append(chain.get(i).getPathName());
		}
		return sb.toString();
	}

	public String[] getPathArray() {
		List<InPathNode> chain = new ArrayList<>();
		Node node = this;
		while (node != null) {
			if (node instanceof InPathNode)
				chain.add((InPathNode) node);
			node = node.parent();
		}
		String[] result = new String[chain.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = chain.get(result.length - i - 1).getPathName();
		}
		return result;
	}

	public Optional<Node> resolve(String[] path, int beginIdx) {
		Node node = this;
		int i = beginIdx;
		loop_node:
		while (i < path.length) {
			String name = path[i];
			for (Node child : node.children) {
				if (child instanceof InPathNode && name.equals(((InPathNode) child).getPathName())) {
					node = child;
					i++;
					continue loop_node;
				}
			}
			if (node.children.size() == 1) {
				Node child = node.children.iterator().next();
				if (!(child instanceof InPathNode)) {
					node = child;
					continue loop_node;
				}
			}
			return Optional.empty();
		}
		return Optional.of(node);
	}

	public abstract String getStringValue();

	boolean runTagMarking(TagMarker marker, BiConsumer<Node, Set<String>> listener) {
		boolean changed = false;
		Set<String> addTags = new LinkedHashSet<>();
		if (marker.condition.test(this)) {
			addTags.clear();
			for (String tag : marker.tags.apply(this)) {
				if (!hasTag(tag)) {
					tags.add(tag);
					changed = true;
					addTags.add(tag);
				}
			}
			if (!addTags.isEmpty())
				listener.accept(this, addTags);
		}
		for (Node child : children) {
			changed |= child.runTagMarking(marker, listener);
		}
		return changed;
	}

	boolean runNodeReplacing(NodeReplacer replacer, BiConsumer<Node, Node> listener) {
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
				listener.accept(child, newChild);
			} else {
				changed |= child.runNodeReplacing(replacer, listener);
			}
		}
		return changed;
	}

}
