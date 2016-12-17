package yushijinhun.maptranslator.tree;

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

}
