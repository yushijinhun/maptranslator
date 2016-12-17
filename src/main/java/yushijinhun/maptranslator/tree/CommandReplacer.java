package yushijinhun.maptranslator.tree;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTString;

public class CommandReplacer {

	private String commandName;
	private String[] argumentsMatching;
	private String[] argumentNames;
	private Function<Map<String, String>, Node>[] subtreeBuilders;

	@SuppressWarnings("unchecked")
	public CommandReplacer(String expression, Map<String, Function<Map<String, String>, Node>> subtrees) {
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
		if (node instanceof NBTNode && node.unmodifiableChildren().isEmpty() && node.tags().contains("command")) {
			NBT nbt = ((NBTNode) node).nbt;
			if (nbt instanceof NBTString) {
				String command = ((NBTString) nbt).getString();
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
		String[] splited = ((NBTString) ((NBTNode) node).nbt).getString().split(" ", argumentNames.length + 1);
		String[] n_argnames = new String[argumentNames.length + 1];
		System.arraycopy(argumentNames, 0, n_argnames, 1, argumentNames.length);
		Map<String, String> arguments = new LinkedHashMap<>();
		for (int i = 0; i < argumentNames.length; i++) {
			if (argumentNames[i] != null) {
				arguments.put(argumentNames[i], splited[i + 1]);
			}
		}
		for (int i = 0; i < subtreeBuilders.length; i++) {
			if (subtreeBuilders[i] != null) {
				splited[i + 1] = null;
				NodeArgument subtree = new NodeArgument(argumentNames[i]);
				subtree.addChild(subtreeBuilders[i].apply(arguments));
				node.addChild(subtree);
			}
		}

		NBTStringProxy proxy = new NBTStringProxy();
		((NBTNode) node).replaceNBT(proxy);
		CommandHandler handler = new CommandHandler((NBTNode) node, splited, n_argnames);
		proxy.handler = handler;

		return node;
	}

	public NodeReplacer toNodeReplacer() {
		return new NodeReplacer(this::matches, this::replace);
	}

}
