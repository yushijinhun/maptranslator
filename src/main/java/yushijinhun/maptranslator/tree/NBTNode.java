package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBTBase;

public abstract class NBTNode extends Node {

	public final NBTBase nbt;

	public NBTNode(NBTBase nbt) {
		this.nbt = nbt;
	}

}
