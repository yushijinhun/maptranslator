package org.to2mbn.maptranslator.tree;

import java.util.Optional;
import java.util.function.Supplier;

public class TextArgumentNode extends Node implements ArgumentNode, TextNode {

	private Supplier<String> text;

	public TextArgumentNode(String text) {
		this.text = () -> text;
	}

	@Override
	public String toArgumentString() {
		return text.get();
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
		text = proxyTarget;
		return this;
	}

}
