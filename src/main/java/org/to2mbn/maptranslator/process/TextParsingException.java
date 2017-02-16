package org.to2mbn.maptranslator.process;

import org.to2mbn.maptranslator.tree.Node;

public class TextParsingException extends NodeParsingException {

	private static final long serialVersionUID = 1L;

	private String text;

	public TextParsingException(String message, Node node, Throwable cause, String text) {
		super("text: " + text + (message == null ? "" : "\n" + message), node, cause);
		this.text = text;
	}

	public TextParsingException(Node node, Throwable cause, String text) {
		this(null, node, cause, text);
	}

	public String getText() {
		return text;
	}

}
