package org.to2mbn.maptranslator.tree;

public interface ListNode extends InPathNode {

	int index();

	@Override
	default String getPathName() {
		return "[" + index() + "]";
	}

}
