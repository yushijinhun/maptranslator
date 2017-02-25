package org.to2mbn.maptranslator.tree;

public class CommandArgumentNode extends Node implements InPathNode {

	public final String argumentName;
	private final String pathName;

	public CommandArgumentNode(String argumentName) {
		this.argumentName = argumentName;
		pathName = ("<" + argumentName + ">").intern();
	}

	public String toArgumentString() {
		if (unmodifiableChildren().size() == 1) {
			Node child = unmodifiableChildren().iterator().next();
			if (child instanceof ArgumentNode) {
				return ((ArgumentNode) child).toArgumentString();
			}
		}
		throw new IllegalStateException("Child node is missing");
	}

	@Override
	public String toString() {
		return pathName;
	}

	@Override
	public String getStringValue() {
		return toArgumentString();
	}

}
