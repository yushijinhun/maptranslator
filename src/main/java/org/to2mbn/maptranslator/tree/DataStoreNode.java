package org.to2mbn.maptranslator.tree;

import org.to2mbn.maptranslator.data.DataDescriptor;

public class DataStoreNode extends Node implements InPathNode {

	protected final DataDescriptor store;

	public DataStoreNode(DataDescriptor store) {
		this.store = store;
	}

	@Override
	public String toString() {
		return store.toString();
	}

	public void read() {
		unmodifiableChildren().forEach(this::removeChild);
		Node node = store.read();
		node.tags().addAll(store.getTags());
		addChild(node);
	}

	public void write() {
		if (unmodifiableChildren().size() == 1) {
			Node node = unmodifiableChildren().iterator().next();
			store.write(node);
			return;
		}
		throw new IllegalStateException("No child is found");

	}

	public void close() {
		unmodifiableChildren().forEach(this::removeChild);
	}

	@Override
	public String getStringValue() {
		return toString();
	}

}
