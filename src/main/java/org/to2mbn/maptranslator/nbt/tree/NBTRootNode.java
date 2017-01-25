package org.to2mbn.maptranslator.nbt.tree;

import org.to2mbn.maptranslator.core.tree.ArgumentNode;
import org.to2mbn.maptranslator.nbt.parse.NBT;

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
