package org.to2mbn.maptranslator.impl.plain.tree;

import org.to2mbn.maptranslator.tree.ListNode;
import org.to2mbn.maptranslator.tree.TextArgumentNode;

public class TextLineNode extends TextArgumentNode implements ListNode {

	private int lineNumber;

	public TextLineNode(String text, int lineNumber) {
		super(text);
		this.lineNumber = lineNumber;
	}

	@Override
	public int index() {
		return lineNumber;
	}

}
