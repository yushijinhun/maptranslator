package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.core.NBTDescriptor;

public class NBTFileNode extends Node {

	public final NBTDescriptor descriptor;

	public NBTFileNode(NBTDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public String toString() {
		return descriptor.toString();
	}

}
