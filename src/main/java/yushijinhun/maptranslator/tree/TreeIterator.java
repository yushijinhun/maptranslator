package yushijinhun.maptranslator.tree;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

public class TreeIterator {

	private static final Logger LOGGER = Logger.getLogger(TreeIterator.class.getCanonicalName());

	public final Set<TagMarker> markers = new LinkedHashSet<>();
	public final Set<NodeReplacer> replacers = new LinkedHashSet<>();

	public void iterate(Node node) {
		iterate(node, -1);
	}

	public boolean iterate(Node node, int maxIterations) {
		int count = 0;
		do {
			while (tag(node)) {
				LOGGER.info("iterate #" + count);
				count++;
				if (maxIterations != -1 && count >= maxIterations) return false;
			}
		} while (replace(node));
		return true;
	}

	private boolean tag(Node node) {
		boolean changed = false;
		for (TagMarker marker : markers) {
			changed |= node.runTagMarking(marker);
		}
		return changed;
	}

	private boolean replace(Node node) {
		boolean changed = false;
		for (NodeReplacer replacer : replacers) {
			changed |= node.runNodeReplacing(replacer);
		}
		return changed;
	}

}
