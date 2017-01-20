package yushijinhun.maptranslator.tree;

public abstract class JsonNode extends Node {

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

}
