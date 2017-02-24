package org.to2mbn.maptranslator.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.to2mbn.maptranslator.process.NodeReplacer;
import org.to2mbn.maptranslator.process.TagMarker;

public abstract class Node {

	private Set<String> tags = new TreeSet<>();
	private Set<Node> children = new LinkedHashSet<>(0);
	private Set<Node> unmodifiableChildren = Collections.unmodifiableSet(children);
	private Node parent;
	private Map<String, Object> properties = new HashMap<>(0);

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
				int segmentBegin = 0;
				int segmentEnd;
				int strIdx = 0;
				String segment;
				boolean match = true;
				int lenPattern = pattern.length();
				int appearanceIdx;
				do {
					segmentEnd = pattern.indexOf('*', segmentBegin);
					if (segmentEnd == -1) segmentEnd = lenPattern;
					segment = pattern.substring(segmentBegin, segmentEnd);
					if (segmentBegin == 0) {
						if (!str.startsWith(segment)) {
							match = false;
							break;
						}
						strIdx = segmentEnd;
					} else if (segmentEnd == lenPattern) {
						if (segment.length() + strIdx > str.length() || !str.endsWith(segment)) {
							match = false;
							break;
						}
					} else {
						appearanceIdx = str.indexOf(segment, strIdx);
						if (appearanceIdx == -1) {
							match = false;
							break;
						}
						strIdx = appearanceIdx + segment.length();
					}
					segmentBegin = segmentEnd + 1;
				} while (segmentEnd < lenPattern);
				if (match) return true;
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

	public Node root() {
		Node root = this;
		while (root.parent != null)
			root = root.parent;
		return root;
	}

	public Optional<Node> relatedTextNode() {
		Node node = this;
		do {
			if (node.getText().isPresent()) return Optional.ofNullable(node);
			if (node.children.size() == 1) {
				node = node.children.iterator().next();
			} else {
				break;
			}
		} while (!(node instanceof InPathNode));
		return Optional.empty();
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

	public Optional<String> getText() {
		if (this instanceof TextNode) {
			return ((TextNode) this).getNodeText();
		}
		return Optional.empty();
	}

	public abstract String getStringValue();

	@Deprecated
	public boolean impl_runTagMarking(Iterable<TagMarker> markers, BiConsumer<Node, Set<String>> listener) {
		boolean changed = false;
		Set<String> addTags = new LinkedHashSet<>();

		// bfs
		Queue<Node> queue = new LinkedList<>();
		queue.offer(this);
		while (!queue.isEmpty()) {
			Node node = queue.poll();

			for (TagMarker marker : markers) {
				if (marker.condition.test(node)) {
					addTags.clear();
					for (String tag : marker.tags.apply(node)) {
						if (!node.hasTag(tag)) {
							node.tags.add(tag);
							changed = true;
							addTags.add(tag);
						}
					}
					if (!addTags.isEmpty()) listener.accept(node, addTags);
				}
			}

			queue.addAll(node.children);
		}

		return changed;
	}

	@Deprecated
	public void impl_runNodeReplacing(Iterable<NodeReplacer> replacers, Consumer<Node> tagMarker, BiConsumer<Node, Node> listener) {
		Node[] childrenArray = children.toArray(new Node[children.size()]);
		boolean changed = false;
		for (int i = 0; i < childrenArray.length; i++) {
			Node child = childrenArray[i];
			boolean replaced = false;
			for (NodeReplacer replacer : replacers) {
				if (replacer.condition.test(child)) {
					Node newChild = replacer.replacer.apply(child);
					if (newChild != null) {
						replaced = true;
						changed = true;
						for (String tag : child.tags)
							if (!newChild.hasTag(tag))
								newChild.tags.add(tag);
						child.properties.forEach(
								(k, v) -> newChild.properties.putIfAbsent(k, v));
						child.parent = null;
						newChild.parent = this;
						childrenArray[i] = newChild;

						tagMarker.accept(newChild);
						listener.accept(child, newChild);
						break;
					}
				}
			}
			if (!replaced) {
				child.impl_runNodeReplacing(replacers, tagMarker, listener);
			}
		}
		if (changed) {
			children.clear();
			for (Node child : childrenArray)
				children.add(child);
		}
	}

}
