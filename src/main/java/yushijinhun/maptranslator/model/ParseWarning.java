package yushijinhun.maptranslator.model;

import yushijinhun.maptranslator.tree.Node;

public class ParseWarning {

	public final Node node;
	public final String origin;
	public final String current;

	public ParseWarning(Node node, String origin, String current) {
		this.node = node;
		this.origin = origin;
		this.current = current;
	}

}
