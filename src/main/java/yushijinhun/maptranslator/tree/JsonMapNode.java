package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.internal.org.json.JSONArray;
import yushijinhun.maptranslator.internal.org.json.JSONObject;

public class JsonMapNode extends JsonNode implements MapNode {

	public final String key;

	public JsonMapNode(Object json, String key) {
		super(json);
		this.key = key;
	}

	@Override
	public String toString() {
		return key;
	}

	@Override
	public String key() {
		return key;
	}

	@Override
	public void replaceJson(Object json) {
		super.replaceJson(json);
		((JSONObject) ((JsonNode) parent()).json).put(key, json);
	}

	@Override
	public String getDisplayText() {
		if (json instanceof JSONObject || json instanceof JSONArray) {
			return toString();
		} else {
			return key + " = " + json;
		}
	}

}
