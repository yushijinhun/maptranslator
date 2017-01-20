package yushijinhun.maptranslator.tree;

import java.util.Objects;
import java.util.function.Supplier;

class CommandHandler implements Supplier<String> {

	Node commandNode;
	String[] arguments;
	String[] argumentNames;

	public CommandHandler(String[] arguments, String[] argumentNames) {
		this.arguments = arguments;
		this.argumentNames = argumentNames;
	}

	@Override
	public String get() {
		if (commandNode == null) {
			throw new IllegalStateException("Command handler hasn't been initialized");
		}
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arguments.length; i++) {
			if (first) {
				first = false;
			} else {
				sb.append(' ');
			}
			if (arguments[i] == null) {
				String key = argumentNames[i];
				Objects.requireNonNull(key);
				String value = null;
				for (Node child : commandNode.unmodifiableChildren()) {
					if (child instanceof NodeArgument) {
						NodeArgument casted = (NodeArgument) child;
						if (key.equals(casted.argumentName)) {
							value = casted.toArgumentString();
							break;
						}
					}
				}
				if (value == null)
					throw new IllegalStateException("Argument node " + key + " not found");
				sb.append(value);
			} else {
				sb.append(arguments[i]);
			}
		}
		return sb.toString();
	}
}
