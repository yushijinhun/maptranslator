package org.to2mbn.maptranslator.process;

import java.util.Objects;
import org.to2mbn.maptranslator.tree.Node;

public class NodeParsingException extends Exception {

	private static final long serialVersionUID = 1L;

	private Node node;

	protected NodeParsingException(String message, Node node, Throwable cause) {
		super(message == null ? node.getPath() : node.getPath() + "\n" + message, Objects.requireNonNull(cause), false, false);
		this.node = node;
	}

	public NodeParsingException(Node node, Throwable cause) {
		this(null, node, cause);
	}

	public Node getNode() {
		return node;
	}

}
