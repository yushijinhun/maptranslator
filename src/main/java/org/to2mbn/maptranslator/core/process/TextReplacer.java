package org.to2mbn.maptranslator.core.process;

import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.to2mbn.maptranslator.core.tree.ArgumentNode;
import org.to2mbn.maptranslator.core.tree.Node;
import org.to2mbn.maptranslator.model.ResolveFailedWarning;

public class TextReplacer extends AbstractReplacer {

	private static class TextHandler implements Supplier<String> {

		Node node;
		Function<Node, String> reverseMapper;

		TextHandler(Function<Node, String> reverseMapper) {
			this.reverseMapper = reverseMapper;
		}

		@Override
		public String get() {
			if (node.unmodifiableChildren().size() == 1) {
				Node child = node.unmodifiableChildren().iterator().next();
				return reverseMapper.apply(child);
			}
			throw new IllegalStateException("Child node is missing");
		}

	}

	public static NodeReplacer of(Predicate<Node> nodeMatcher, Function<String, Node> subtreeBuilder) {
		return of(nodeMatcher, (node, string) -> subtreeBuilder.apply(string));
	}

	public static NodeReplacer of(Predicate<Node> nodeMatcher, BiFunction<Node, String, Node> subtreeBuilder) {
		return of(nodeMatcher, subtreeBuilder, node -> ((ArgumentNode) node).toArgumentString());
	}

	public static NodeReplacer of(Predicate<Node> nodeMatcher, BiFunction<Node, String, Node> subtreeBuilder, Function<Node, String> reverseMapper) {
		return new TextReplacer(nodeMatcher, subtreeBuilder, reverseMapper).toNodeReplacer();
	}

	private Predicate<Node> nodeMatcher;
	private BiFunction<Node, String, Node> subtreeBuilder;
	private Function<Node, String> reverseMapper;

	public TextReplacer(Predicate<Node> nodeMatcher, BiFunction<Node, String, Node> subtreeBuilder, Function<Node, String> reverseMapper) {
		this.nodeMatcher = nodeMatcher;
		this.subtreeBuilder = subtreeBuilder;
		this.reverseMapper = reverseMapper;
	}

	private Node replace(Node node) {
		TextContext ctx = TextContext.getContext(node);
		String json = ctx.getText(node);
		TextHandler handler = new TextHandler(reverseMapper);
		Node replacedNode = ctx.replaceNode(node, handler);
		handler.node = replacedNode;
		replacedNode.properties().put("origin", json);
		replacedNode.addChild(subtreeBuilder.apply(node, json));
		return replacedNode;
	}

	public NodeReplacer toNodeReplacer() {
		return new NodeReplacer(nodeMatcher.and(node -> {
			TextContext ctx = TextContext.getContext(node);
			if (ctx != null && node.unmodifiableChildren().isEmpty()) {
				String text = ctx.getText(node);
				if (text != null) {
					try {
						subtreeBuilder.apply(node, text);
					} catch (ArgumentParseException e) {
						postResolveFailedWarning(new ResolveFailedWarning(node, text, Collections.emptyMap(), e));
						return false;
					}
					return true;
				}
			}
			return false;
		}), this::replace);
	}

}
