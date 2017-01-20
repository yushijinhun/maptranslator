package yushijinhun.maptranslator.tree;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class TextReplacer extends TextNodeReplacer {

	public static NodeReplacer of(Predicate<Node> nodeMatcher, Function<String, Node> subtreeBuilder) {
		return of(nodeMatcher, (node, string) -> subtreeBuilder.apply(string));
	}

	public static NodeReplacer of(Predicate<Node> nodeMatcher, BiFunction<Node, String, Node> subtreeBuilder) {
		return new TextReplacer(nodeMatcher, subtreeBuilder).toNodeReplacer();
	}

	private Predicate<Node> nodeMatcher;
	private BiFunction<Node, String, Node> subtreeBuilder;

	public TextReplacer(Predicate<Node> nodeMatcher, BiFunction<Node, String, Node> subtreeBuilder) {
		this.nodeMatcher = nodeMatcher;
		this.subtreeBuilder = subtreeBuilder;
	}

	private Node replace(Node node) {
		TextContext ctx = getContext(node);
		String json = ctx.getText(node);
		Node replacedNode = ctx.replaceNode(node, () -> {
			if (node.unmodifiableChildren().size() == 1) {
				Node child = node.unmodifiableChildren().iterator().next();
				if (child instanceof ArgumentNode) {
					return ((ArgumentNode) child).toArgumentString();
				}
			}
			throw new IllegalStateException("Child node is missing");
		});
		replacedNode.properties().put("origin", json);
		replacedNode.addChild(subtreeBuilder.apply(node, json));
		return replacedNode;
	}

	@Override
	public NodeReplacer toNodeReplacer() {
		return new NodeReplacer(nodeMatcher.and(node -> {
			TextContext ctx = getContext(node);
			if (ctx != null && node.unmodifiableChildren().isEmpty()) {
				String text = ctx.getText(node);
				if (text != null) {
					try {
						subtreeBuilder.apply(node, text);
					} catch (ArgumentParseException e) {
						return false;
					}
					return true;
				}
			}
			return false;
		}), this::replace);
	}

}
