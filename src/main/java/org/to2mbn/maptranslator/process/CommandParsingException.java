package org.to2mbn.maptranslator.process;

import java.util.Map;
import org.to2mbn.maptranslator.tree.Node;

public class CommandParsingException extends TextParsingException {

	private static final long serialVersionUID = 1L;

	private final Map<String, String> arguments;

	public CommandParsingException(String message, Node node, Throwable cause, String text, Map<String, String> arguments) {
		super("arguments: " + arguments + (message == null ? "" : "\n" + message), node, cause, text);
		this.arguments = arguments;
	}

	public CommandParsingException(Node node, Throwable cause, String text, Map<String, String> arguments) {
		this(null, node, cause, text, arguments);
	}

	public Map<String, String> getArguments() {
		return arguments;
	}

}
