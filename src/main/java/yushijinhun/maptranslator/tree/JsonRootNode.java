package yushijinhun.maptranslator.tree;

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
		return json.toString();
	}

}
