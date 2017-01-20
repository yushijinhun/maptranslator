package yushijinhun.maptranslator.model;

import yushijinhun.maptranslator.tree.Node;

public abstract class ParsingWarning {

	public final String[] pathArray;
	public final String path;

	public ParsingWarning(Node node) {
		pathArray = node.getPathArray();
		path = node.getPath();
	}

}
