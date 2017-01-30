package org.to2mbn.maptranslator.process;

import java.util.Set;
import java.util.function.Consumer;
import org.to2mbn.maptranslator.tree.Node;

public class TreeIterator {

	public static void iterate(IteratorArgument arg, Node node) {
		new TreeIterator(arg).iterate(node);
	}

	private IteratorArgument argument;

	private TreeIterator(IteratorArgument argument) {
		this.argument = argument;
	}

	private void iterate(Node node) {
		while (tag(node));
		replace(node, this::iterate);
	}

	@SuppressWarnings("deprecation")
	private boolean tag(Node node) {
		return node.impl_runTagMarking(argument.markers, this::onTagMarked);
	}

	@SuppressWarnings("deprecation")
	private void replace(Node node, Consumer<Node> tagMarker) {
		node.impl_runNodeReplacing(argument.replacers, tagMarker, this::onNodeReplaced);
	}

	private void onTagMarked(Node node, Set<String> addTags) {
	}

	private void onNodeReplaced(Node from, Node to) {
	}

}
