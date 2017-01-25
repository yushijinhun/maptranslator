package org.to2mbn.maptranslator.impl.json.tree;

import java.util.Optional;
import java.util.function.Supplier;
import org.to2mbn.maptranslator.impl.json.parse.JSONObject;
import org.to2mbn.maptranslator.impl.json.parse.JSONString;
import org.to2mbn.maptranslator.tree.Node;
import org.to2mbn.maptranslator.tree.TextNode;

public abstract class JsonNode extends Node implements TextNode {

	public Object json;

	public JsonNode(Object json) {
		this.json = json;
	}

	public void replaceJson(Object json) {
		this.json = json;
	}

	@Override
	public String getStringValue() {
		return json.toString();
	}

	@Override
	public Optional<String> getNodeText() {
		if (json instanceof String)
			return Optional.of((String) json);
		return Optional.empty();
	}

	@Override
	public Node replaceNodeText(Supplier<String> proxyTarget) {
		replaceJson(new JSONString() {

			@Override
			public String toJSONString() {
				return JSONObject.quote(proxyTarget.get());
			}

			@Override
			public String toString() {
				return proxyTarget.get();
			}

		});
		return this;
	}

}
