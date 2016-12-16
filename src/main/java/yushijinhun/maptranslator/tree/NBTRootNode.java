package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBTBase;

public class NBTRootNode extends NBTNode {

	public NBTRootNode(NBTBase nbt) {
		super(nbt);
	}

	@Override
	public String toString() {
		return "<nbt_root>";
	}

}
