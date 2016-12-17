package yushijinhun.maptranslator.tree;

public abstract class JsonNode extends Node {

	public Object json;

	public JsonNode(Object json) {
		this.json = json;
	}

}
