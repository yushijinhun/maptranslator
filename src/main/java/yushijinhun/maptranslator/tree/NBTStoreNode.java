package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.core.NBTDescriptor;
import yushijinhun.maptranslator.nbt.NBTCompound;
import yushijinhun.maptranslator.tree.NBTRootNode;
import yushijinhun.maptranslator.tree.TreeConstructor;
import yushijinhun.maptranslator.tree.Node;

public class NBTStoreNode extends Node implements InPathNode {

	public final NBTDescriptor descriptor;

	public NBTStoreNode(NBTDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public String toString() {
		return descriptor.toString();
	}

	public void read() {
		unmodifiableChildren().forEach(this::removeChild);
		NBTRootNode node = TreeConstructor.construct(descriptor.read());
		node.tags().addAll(descriptor.getTags());
		addChild(node);
	}

	public void write() {
		if (unmodifiableChildren().size() == 1) {
			Node node = unmodifiableChildren().iterator().next();
			if (node instanceof NBTRootNode) {
				descriptor.write((NBTCompound) ((NBTRootNode) node).nbt.clone());
				return;
			}
		}
		throw new IllegalStateException("No NBT data found in the node");

	}

	public void close() {
		unmodifiableChildren().forEach(this::removeChild);
	}

}
