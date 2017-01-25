package org.to2mbn.maptranslator.tree;

import org.to2mbn.maptranslator.core.DataDescriptor;

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
			if (node instanceof NBTRootNode) {
				store.write(node);
				return;
			}
		}
		throw new IllegalStateException("No NBT data found in the node");

	}

	public void close() {
		unmodifiableChildren().forEach(this::removeChild);
	}

	@Override
	public String getStringValue() {
		return toString();
	}

}
