package org.to2mbn.maptranslator.tree;

public interface MapNode extends InPathNode {

	String key();

	@Override
	default String getPathName() {
		return key();
	}

}
