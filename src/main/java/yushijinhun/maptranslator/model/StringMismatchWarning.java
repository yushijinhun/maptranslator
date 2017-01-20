package yushijinhun.maptranslator.model;

import yushijinhun.maptranslator.tree.Node;

public class StringMismatchWarning extends ParsingWarning {

	public final String origin;
	public final String current;

	public StringMismatchWarning(Node node, String origin, String current) {
		super(node);
		this.origin = origin;
		this.current = current;
	}

}
