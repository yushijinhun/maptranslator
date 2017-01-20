package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.internal.org.json.JSONArray;
import yushijinhun.maptranslator.internal.org.json.JSONException;
import yushijinhun.maptranslator.internal.org.json.JSONObject;

public class JsonRootNode extends JsonNode implements ArgumentNode {

	public JsonRootNode(Object json) {
		super(json);
	}

	@Override
	public String toString() {
		return "<json>";
	}

	@Override
	public String toArgumentString() {
		if ("gson".equals(properties().get("json.to_string.algorithm"))) {
			return gsonToString();
		}
		return json.toString();
	}

	private String gsonToString() {
		String parsed = json.toString();
		Object resolved;
		try {
			resolved = new JSONObject(parsed);
		} catch (JSONException e) {
			try {
				resolved = new JSONArray(parsed);
			} catch (JSONException e1) {
				return parsed;
			}
		}
		return JSONObject._use_gson_toString(resolved::toString);
	}

	@Override
	public String getStringValue() {
		return toArgumentString();
	}

}
