package org.to2mbn.maptranslator.core;

import java.io.UncheckedIOException;
import java.util.Set;
import org.to2mbn.maptranslator.tree.DataStoreNode;
import org.to2mbn.maptranslator.tree.Node;

public interface DataDescriptor {

	Node read() throws UncheckedIOException;

	void write(Node node) throws UncheckedIOException;

	Set<String> getTags();

	default DataStoreNode createNode() {
		return new DataStoreNode(this);
	}

}
