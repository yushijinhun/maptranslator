package org.to2mbn.maptranslator.tree;

import java.util.Optional;
import java.util.function.Supplier;

public class ClauseNode extends Node implements ArgumentNode, TextNode {

	public Supplier<String> clause;

	@Override
	public String toArgumentString() {
		return clause.get();
	}

	@Override
	public String toString() {
		return toArgumentString();
	}

	@Override
	public String getStringValue() {
		return toArgumentString();
	}

	@Override
	public Optional<String> getNodeText() {
		return Optional.of(toArgumentString());
	}

	@Override
	public Node replaceNodeText(Supplier<String> proxyTarget) {
		clause = proxyTarget;
		return this;
	}

}
