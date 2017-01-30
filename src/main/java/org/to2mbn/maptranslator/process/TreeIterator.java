package org.to2mbn.maptranslator.process;

import java.util.Set;
import org.to2mbn.maptranslator.tree.Node;

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

	@SuppressWarnings("deprecation")
	private boolean tag(Node node) {
		return node.impl_runTagMarking(argument.markers, this::onTagMarked);
	}

	@SuppressWarnings("deprecation")
	private boolean replace(Node node) {
		boolean changed = false;
		for (NodeReplacer replacer : argument.replacers) {
			changed |= node.impl_runNodeReplacing(replacer, this::onNodeReplaced);
		}
		return changed;
	}

	private void onTagMarked(Node node, Set<String> addTags) {
	}

	private void onNodeReplaced(Node from, Node to) {
	}

}
