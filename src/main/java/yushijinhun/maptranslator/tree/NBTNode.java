package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBT;

public abstract class NBTNode extends Node {

	public final NBT nbt;

	public NBTNode(NBT nbt) {
		this.nbt = nbt;
	}

}
