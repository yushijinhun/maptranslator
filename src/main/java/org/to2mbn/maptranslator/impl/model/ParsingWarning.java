package org.to2mbn.maptranslator.impl.model;

import java.util.LinkedHashSet;
import java.util.Set;
import org.to2mbn.maptranslator.tree.Node;

public abstract class ParsingWarning {

	public final String[] pathArray;
	public final String path;
	public final Set<String> tags;

	public ParsingWarning(Node node) {
		pathArray = node.getPathArray();
		path = node.getPath();
		tags = new LinkedHashSet<>(node.tags());
	}

}
