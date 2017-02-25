package org.to2mbn.maptranslator.tree;

import org.to2mbn.maptranslator.data.DataDescriptor;

public class DataStoreNode extends Node implements InPathNode {

	protected final DataDescriptor store;
	private final String pathName;

	public DataStoreNode(DataDescriptor store) {
		this.store = store;
		pathName = store.toString().intern();
	}

	@Override
	public String toString() {
		return pathName;
	}

	public void read() {
		unmodifiableChildren().forEach(this::removeChild);
		Node node = store.read();
		store.getTags().forEach(tag -> node.addTag(tag.intern()));
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
