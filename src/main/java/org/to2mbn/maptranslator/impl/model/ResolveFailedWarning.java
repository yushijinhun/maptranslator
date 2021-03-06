package org.to2mbn.maptranslator.impl.model;

import java.util.Map;
import org.to2mbn.maptranslator.tree.Node;

public class ResolveFailedWarning extends ParsingWarning {

	public final String text;
	public final Map<String, String> arguments;
	public final ExceptionSnapshot exception;

	public ResolveFailedWarning(Node node, String text, Map<String, String> arguments, Throwable exception) {
		super(node);
		this.text = text;
		this.arguments = arguments;
		this.exception = new ExceptionSnapshot(exception);
	}

}
