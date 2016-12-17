package yushijinhun.maptranslator.tree;

public class TextArgumentNode extends Node implements ArgumentNode {

	public String text;

	public TextArgumentNode(String text) {
		this.text = text;
	}

	@Override
	public String toArgumentString() {
		return text;
	}

}
