package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.internal.org.json.JSONArray;

public class JsonListNode extends JsonNode implements ListNode {

	public final int index;

	public JsonListNode(Object json, int index) {
		super(json);
		this.index = index;
	}

	@Override
	public String toString() {
		return "[" + index + "]";
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public void replaceJson(Object json) {
		super.replaceJson(json);
		((JSONArray) ((JsonNode) parent()).json).put(index, json);
	}

}
