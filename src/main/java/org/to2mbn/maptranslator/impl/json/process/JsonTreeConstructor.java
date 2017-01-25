package org.to2mbn.maptranslator.impl.json.process;

import static org.to2mbn.maptranslator.process.TreeConstructorUtils.checkedParse;
import org.to2mbn.maptranslator.impl.json.parse.JSONArray;
import org.to2mbn.maptranslator.impl.json.parse.JSONException;
import org.to2mbn.maptranslator.impl.json.parse.JSONObject;
import org.to2mbn.maptranslator.impl.json.tree.JsonListNode;
import org.to2mbn.maptranslator.impl.json.tree.JsonMapNode;
import org.to2mbn.maptranslator.impl.json.tree.JsonNode;
import org.to2mbn.maptranslator.impl.json.tree.JsonRootNode;
import org.to2mbn.maptranslator.process.ArgumentParseException;

public final class JsonTreeConstructor {

	public static JSONArray parseJsonArray(String json) {
		return checkedParse(JSONArray::new, json);
	}

	public static JSONObject parseJsonObject(String json) {
		return checkedParse(JSONObject::new, json);
	}

	public static JsonRootNode constructJson(String json) {
		try {
			return construct(parseJsonObject(json));
		} catch (JSONException e) {
			try {
				return construct(parseJsonArray(json));
			} catch (JSONException e1) {
				e.addSuppressed(e1);
				throw new ArgumentParseException(json, e);
			}

		}
	}

	public static JsonRootNode construct(JSONObject json) {
		JsonRootNode root = new JsonRootNode(json);
		constructSubtree(root);
		return root;
	}

	public static JsonRootNode construct(JSONArray json) {
		JsonRootNode root = new JsonRootNode(json);
		constructSubtree(root);
		return root;
	}

	public static void constructSubtree(JsonNode node) {
		Object json = node.json;
		if (json instanceof JSONObject) {
			JSONObject casted = (JSONObject) json;
			casted.keySet().stream()
					.sorted()
					.forEach(key -> {
						JsonMapNode child = new JsonMapNode(casted.get(key), key);
						constructSubtree(child);
						node.addChild(child);
					});
		} else if (json instanceof JSONArray) {
			JSONArray casted = (JSONArray) json;
			for (int i = 0; i < casted.length(); i++) {
				JsonListNode child = new JsonListNode(casted.get(i), i);
				constructSubtree(child);
				node.addChild(child);
			}
		}
	}

	private JsonTreeConstructor() {}
}
