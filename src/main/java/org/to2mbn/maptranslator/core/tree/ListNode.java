package org.to2mbn.maptranslator.core.tree;

public interface ListNode extends InPathNode {

	int index();

	@Override
	default String getPathName() {
		return "[" + index() + "]";
	}

}
