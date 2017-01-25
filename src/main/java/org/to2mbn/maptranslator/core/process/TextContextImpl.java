package org.to2mbn.maptranslator.core.process;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.to2mbn.maptranslator.core.tree.ClauseNode;
import org.to2mbn.maptranslator.core.tree.Node;
import org.to2mbn.maptranslator.core.tree.TextArgumentNode;
import org.to2mbn.maptranslator.json.parse.JSONObject;
import org.to2mbn.maptranslator.json.parse.JSONString;
import org.to2mbn.maptranslator.json.tree.JsonNode;
import org.to2mbn.maptranslator.nbt.parse.NBT;
import org.to2mbn.maptranslator.nbt.parse.NBTString;
import org.to2mbn.maptranslator.nbt.tree.NBTNode;
import org.to2mbn.maptranslator.nbt.tree.NBTStringProxy;

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
