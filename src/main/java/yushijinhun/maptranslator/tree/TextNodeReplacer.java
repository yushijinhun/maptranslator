package yushijinhun.maptranslator.tree;

import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import yushijinhun.maptranslator.internal.org.json.JSONObject;
import yushijinhun.maptranslator.internal.org.json.JSONString;
import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTString;

abstract public class TextNodeReplacer {

	public static final Map<Class<?>, TextContext> CTXS = new ConcurrentHashMap<>();

	static {
		CTXS.put(NBTNode.class, new TextContext() {

			@Override
			public String getText(Node node) {
				if (node instanceof NBTNode) {
					NBT nbt = ((NBTNode) node).nbt;
					if (nbt instanceof NBTString) return ((NBTString) nbt).getString();
				}
				return null;
			}

			@Override
			public Node replaceNode(Node node, Supplier<String> proxyTarget) {
				NBTStringProxy proxy = new NBTStringProxy();
				proxy.handler = proxyTarget;
				((NBTNode) node).replaceNBT(proxy);
				return node;
			}
		});
		CTXS.put(TextArgumentNode.class, new TextContext() {

			@Override
			public String getText(Node node) {
				if (node instanceof TextArgumentNode) {
					return ((TextArgumentNode) node).text;
				}
				return null;
			}

			@Override
			public Node replaceNode(Node node, Supplier<String> proxyTarget) {
				ClauseNode clause = new ClauseNode();
				clause.clause = proxyTarget;
				return clause;
			}
		});
		CTXS.put(JsonNode.class, new TextContext() {

			@Override
			public String getText(Node node) {
				if (node instanceof JsonNode) {
					Object obj = ((JsonNode) node).json;
					if (obj instanceof String) return (String) obj;
				}
				return null;
			}

			@Override
			public Node replaceNode(Node node, Supplier<String> proxyTarget) {
				JSONString proxy = () -> JSONObject.quote(proxyTarget.get());
				((JsonNode) node).replaceJson(proxy);
				return node;
			}

		});
		CTXS.put(ClauseNode.class, new TextContext() {

			@Override
			public String getText(Node node) {
				if (node instanceof ClauseNode) {
					return ((ClauseNode) node).toArgumentString();
				}
				return null;
			}

			@Override
			public Node replaceNode(Node node, Supplier<String> proxyTarget) {
				((ClauseNode) node).clause = proxyTarget;
				return node;
			}

		});
	}

	public static interface TextContext {

		String getText(Node node);

		Node replaceNode(Node node, Supplier<String> proxyTarget);

	}

	public static TextContext getContext(Node node) {
		for (Entry<Class<?>, TextContext> ety : CTXS.entrySet()) {
			if (ety.getKey().isInstance(node)) return ety.getValue();
		}
		return null;
	}

	public static Optional<String> getText(Node node) {
		TextContext ctx = getContext(node);
		if (ctx == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(ctx.getText(node));
	}

	abstract public NodeReplacer toNodeReplacer();

}
