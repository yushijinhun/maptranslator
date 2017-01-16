package yushijinhun.maptranslator.tree;

public interface ListNode extends InPathNode {

	int index();

	@Override
	default String getPathName() {
		return "[" + index() + "]";
	}

}
