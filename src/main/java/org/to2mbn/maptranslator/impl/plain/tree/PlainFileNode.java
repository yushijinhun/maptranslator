package org.to2mbn.maptranslator.impl.plain.tree;

import org.to2mbn.maptranslator.tree.ArgumentNode;
import org.to2mbn.maptranslator.tree.Node;

public class PlainFileNode extends Node implements ArgumentNode {

	@Override
	public String toString() {
		return "<plain>";
	}

	@Override
	public String getStringValue() {
		return toArgumentString();
	}

	@Override
	public String toArgumentString() {
		StringBuilder result = new StringBuilder();
		unmodifiableChildren().stream().sorted().map(Node::toString).forEach(line -> result.append(line).append('\n'));
		return result.toString();
	}

}
