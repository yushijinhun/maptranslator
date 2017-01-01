package yushijinhun.maptranslator.tree;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

public class TreeIterator {

	private static final Logger LOGGER = Logger.getLogger(TreeIterator.class.getCanonicalName());

	public final Set<TagMarker> markers = new LinkedHashSet<>();
	public final Set<NodeReplacer> replacers = new LinkedHashSet<>();

	private long nodesCount;
	private long markedNodes;
	private long replacedNodes;

	public void iterate(Node node) {
		iterate(node, -1);
	}

	public boolean iterate(Node node, int maxIterations) {
		nodesCount = node.getAllChildrenCount();
		LOGGER.info("iterating, maxIterations=" + maxIterations + ", nodes=" + nodesCount);
		int count = 0;
		do {
			while (tag(node)) {
				count++;
				if (maxIterations != -1 && count >= maxIterations) return false;
			}
		} while (replace(node));
		return true;
	}

	private boolean tag(Node node) {
		markedNodes = 0;
		boolean changed = false;
		for (TagMarker marker : markers) {
			changed |= node.runTagMarking(marker, this::onTagMarked);
		}
		LOGGER.info("tag marked, changedNodes=" + markedNodes);
		return changed;
	}

	private boolean replace(Node node) {
		replacedNodes = 0;
		boolean changed = false;
		for (NodeReplacer replacer : replacers) {
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
