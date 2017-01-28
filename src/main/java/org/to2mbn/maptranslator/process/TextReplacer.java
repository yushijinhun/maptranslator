package org.to2mbn.maptranslator.process;

import java.util.Collections;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.to2mbn.maptranslator.model.ResolveFailedWarning;
import org.to2mbn.maptranslator.tree.ArgumentNode;
import org.to2mbn.maptranslator.tree.Node;
import org.to2mbn.maptranslator.tree.TextNode;

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
		String text = getNodeText(node).get();
		TextHandler handler = new TextHandler(reverseMapper);
		Node replacedNode = ((TextNode) node).replaceNodeText(handler);
		handler.node = replacedNode;
		replacedNode.properties().put("origin", text);
		replacedNode.addChild(subtreeBuilder.apply(node, text));
		return replacedNode;
	}

	public NodeReplacer toNodeReplacer() {
		return new NodeReplacer(nodeMatcher.and(node -> {
			Optional<String> optional = getNodeText(node);
			if (optional.isPresent()) {
				if (node.unmodifiableChildren().isEmpty()) {
					String text = optional.get();
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
