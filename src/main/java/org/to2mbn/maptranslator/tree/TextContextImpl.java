package org.to2mbn.maptranslator.tree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.to2mbn.maptranslator.internal.org.json.JSONObject;
import org.to2mbn.maptranslator.internal.org.json.JSONString;
import org.to2mbn.maptranslator.nbt.NBT;
import org.to2mbn.maptranslator.nbt.NBTString;

class TextContextImpl {

	static Map<Class<?>, TextContext> createRegistry() {
		Map<Class<?>, TextContext> ctxs = new ConcurrentHashMap<>();
		initContexts(ctxs);
		return ctxs;
	}

	private static void initContexts(Map<Class<?>, TextContext> ctxs) {
		ctxs.put(NBTNode.class, new TextContext() {

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
		ctxs.put(TextArgumentNode.class, new TextContext() {

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
		ctxs.put(JsonNode.class, new TextContext() {

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
				JSONString proxy = new JSONString() {

					@Override
					public String toJSONString() {
						return JSONObject.quote(proxyTarget.get());
					}

					@Override
					public String toString() {
						return proxyTarget.get();
					}

				};
				((JsonNode) node).replaceJson(proxy);
				return node;
			}

		});
		ctxs.put(ClauseNode.class, new TextContext() {

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

}
