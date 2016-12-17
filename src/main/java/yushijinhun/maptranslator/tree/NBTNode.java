package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBT;

public abstract class NBTNode extends Node {

	public NBT nbt;

	public NBTNode(NBT nbt) {
		this.nbt = nbt;
	}

	public void replaceNBT(NBT newnbt) {
		nbt = newnbt;
	}

}
