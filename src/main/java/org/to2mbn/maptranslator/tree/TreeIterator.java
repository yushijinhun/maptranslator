package org.to2mbn.maptranslator.tree;

import java.util.Set;

public class TreeIterator {

	private IteratorArgument argument;

	public TreeIterator(IteratorArgument argument) {
		this.argument = argument;
	}

	public boolean iterate(Node node) {
		int count = 0;
		do {
			while (tag(node)) {
				count++;
				if (argument.maxIterations != -1 && count >= argument.maxIterations) return false;
			}
		} while (replace(node));
		return true;
	}

	private boolean tag(Node node) {
		boolean changed = false;
		for (TagMarker marker : argument.markers) {
			changed |= node.runTagMarking(marker, this::onTagMarked);
		}
		return changed;
	}

	private boolean replace(Node node) {
		boolean changed = false;
		for (NodeReplacer replacer : argument.replacers) {
			changed |= node.runNodeReplacing(replacer, this::onNodeReplaced);
		}
		return changed;
	}

	private void onTagMarked(Node node, Set<String> addTags) {
	}

	private void onNodeReplaced(Node from, Node to) {
	}

}
