package org.to2mbn.maptranslator.core.tree;

import java.util.function.Supplier;

public class ClauseNode extends Node implements ArgumentNode {

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

}
