package yushijinhun.maptranslator.tree;

import java.util.Set;
import java.util.logging.Logger;

public class TreeIterator {

	private static final Logger LOGGER = Logger.getLogger(TreeIterator.class.getCanonicalName());

	private IteratorArgument argument;

	public TreeIterator(IteratorArgument argument) {
		this.argument = argument;
	}

	private long nodesCount;
	private long markedNodes;
	private long replacedNodes;

	public boolean iterate(Node node) {
		nodesCount = node.getAllChildrenCount();
		LOGGER.info("iterating, maxIterations=" + argument.maxIterations + ", nodes=" + nodesCount);
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
		markedNodes = 0;
		boolean changed = false;
		for (TagMarker marker : argument.markers) {
			changed |= node.runTagMarking(marker, this::onTagMarked);
		}
		LOGGER.info("tag marked, changedNodes=" + markedNodes);
		return changed;
	}

	private boolean replace(Node node) {
		replacedNodes = 0;
		boolean changed = false;
		for (NodeReplacer replacer : argument.replacers) {
			changed |= node.runNodeReplacing(replacer, this::onNodeReplaced);
		}
		long oldNodesCount = nodesCount;
		nodesCount = node.getAllChildrenCount();
		LOGGER.info("node replaced, replacedNodes=" + replacedNodes + ", nodes=" + nodesCount + "(delta=" + (nodesCount - oldNodesCount) + ")");
		return changed;
	}

	private void onTagMarked(Node node, Set<String> addTags) {
		markedNodes++;
	}

	private void onNodeReplaced(Node from, Node to) {
		replacedNodes++;
	}

}
