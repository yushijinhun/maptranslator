package yushijinhun.maptranslator;

import java.util.LinkedHashSet;
import java.util.Set;
import yushijinhun.maptranslator.tree.NodeReplacer;
import yushijinhun.maptranslator.tree.TagMarker;

public class IteratorArgument {

	public final Set<NodeReplacer> replacers = new LinkedHashSet<>();
	public final Set<TagMarker> markers = new LinkedHashSet<>();
	public int maxIterations = -1;

}
