package yushijinhun.maptranslator.tree;

public class NodeArgument extends Node {

	public final String argumentName;

	public NodeArgument(String argumentName) {
		this.argumentName = argumentName;
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
		return "<" + argumentName + ">";
	}

}
