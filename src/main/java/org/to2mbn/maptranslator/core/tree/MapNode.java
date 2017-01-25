package org.to2mbn.maptranslator.core.tree;

public interface MapNode extends InPathNode {

	String key();

	@Override
	default String getPathName() {
		return key();
	}

}
