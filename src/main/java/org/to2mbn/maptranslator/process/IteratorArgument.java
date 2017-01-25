package org.to2mbn.maptranslator.process;

import java.util.LinkedHashSet;
import java.util.Set;

public class IteratorArgument {

	public final Set<NodeReplacer> replacers = new LinkedHashSet<>();
	public final Set<TagMarker> markers = new LinkedHashSet<>();
	public int maxIterations = -1;

}
