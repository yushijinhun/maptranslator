package yushijinhun.maptranslator.tree;

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

}
