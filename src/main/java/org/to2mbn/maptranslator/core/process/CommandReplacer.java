package org.to2mbn.maptranslator.core.process;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.to2mbn.maptranslator.core.tree.Node;
import org.to2mbn.maptranslator.core.tree.NodeArgument;
import org.to2mbn.maptranslator.model.ResolveFailedWarning;

public class CommandReplacer extends AbstractReplacer {

	private static class CommandHandler implements Supplier<String> {

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

	public static NodeReplacer of(String expression, String arg, Function<Map<String, String>, Node> subtree) {
		return of("command", expression, arg, subtree);
	}

	public static NodeReplacer of(String tag, String expression, String arg, Function<Map<String, String>, Node> subtree) {
		Map<String, Function<Map<String, String>, Node>> subtrees = new HashMap<>();
		subtrees.put(arg, subtree);
		return new CommandReplacer(tag, expression, subtrees).toNodeReplacer();
	}

	private String commandName;
	private String[] argumentsMatching;
	private String[] argumentNames;
	private Function<Map<String, String>, Node>[] subtreeBuilders;
	private String tag;

	@SuppressWarnings("unchecked")
	public CommandReplacer(String tag, String expression, Map<String, Function<Map<String, String>, Node>> subtrees) {
		this.tag = tag;
		String[] splited = expression.split(" ");
		commandName = splited[0];
		int len = splited.length - 1;
		argumentsMatching = new String[len];
		argumentNames = new String[len];
		subtreeBuilders = new Function[len];
		for (int i = 0; i < len; i++) {
			String s = splited[i + 1];
			if (s.startsWith("<") && s.endsWith(">")) {
				String name = s.substring(1, s.length() - 1);
				argumentNames[i] = name;
				subtreeBuilders[i] = subtrees.get(name);
			} else {
				argumentsMatching[i] = s;
			}
		}
	}

	private boolean matches(Node node) {
		if (node.unmodifiableChildren().isEmpty() && node.hasTag(tag)) {
			TextContext ctx = TextContext.getContext(node);
			if (ctx == null) return false;
			String command = ctx.getText(node);
			if (command != null && !command.trim().isEmpty()) {
				command = command.trim();
				String[] splited = command.split(" ", argumentNames.length + 1);
				if (splited.length == argumentNames.length + 1) {
					if (splited[0].startsWith("/") ? commandName.equals(splited[0].substring(1)) : commandName.equals(splited[0])) {
						Map<String, String> arguments = new LinkedHashMap<>();
						for (int i = 0; i < argumentsMatching.length; i++) {
							if (argumentsMatching[i] != null && !argumentsMatching[i].equals(splited[i + 1])) {
								return false;
							}
							if (argumentNames[i] != null) {
								arguments.put(argumentNames[i], splited[i + 1]);
							}
						}
						for (Function<Map<String, String>, Node> func : subtreeBuilders) {
							if (func != null) {
								try {
									func.apply(arguments);
								} catch (ArgumentParseException e) {
									postResolveFailedWarning(new ResolveFailedWarning(node, command, arguments, e));
									return false;
								}
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	private Node replace(Node node) {
		TextContext ctx = TextContext.getContext(node);
		String cmd = ctx.getText(node).trim();
		String[] splited = cmd.split(" ", argumentNames.length + 1);
		String[] n_argnames = new String[argumentNames.length + 1];
		System.arraycopy(argumentNames, 0, n_argnames, 1, argumentNames.length);
		Map<String, String> arguments = new LinkedHashMap<>();
		for (int i = 0; i < argumentNames.length; i++) {
			if (argumentNames[i] != null) {
				arguments.put(argumentNames[i], splited[i + 1]);
			}
		}

		CommandHandler handler = new CommandHandler(splited, n_argnames);
		Node replaced = ctx.replaceNode(node, handler);
		replaced.properties().put("origin", cmd);
		for (int i = 0; i < subtreeBuilders.length; i++) {
			if (subtreeBuilders[i] != null) {
				splited[i + 1] = null;
				NodeArgument subtree = new NodeArgument(argumentNames[i]);
				subtree.addChild(subtreeBuilders[i].apply(arguments));
				replaced.addChild(subtree);
			}
		}
		handler.commandNode = replaced;
		return replaced;
	}

	public NodeReplacer toNodeReplacer() {
		return new NodeReplacer(this::matches, this::replace);
	}

}
