package org.to2mbn.maptranslator.impl.nbt.tree;

import org.to2mbn.maptranslator.impl.nbt.parse.NBT;
import org.to2mbn.maptranslator.tree.ArgumentNode;

public class NBTRootNode extends NBTNode implements ArgumentNode {

	public NBTRootNode(NBT nbt) {
		super(nbt);
	}

	@Override
	public String toString() {
		return "<nbt>";
	}

	@Override
	public String toArgumentString() {
		return nbt.toString();
	}

}
