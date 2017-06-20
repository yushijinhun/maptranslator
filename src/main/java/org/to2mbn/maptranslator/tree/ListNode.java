package org.to2mbn.maptranslator.tree;

public interface ListNode extends InPathNode, Comparable<ListNode> {

	int index();

	@Override
	default String getPathName() {
		return ("[" + index() + "]").intern();
	}

	@Override
	default int compareTo(ListNode o) {
		return Integer.compare(index(), o.index());
	}

}
