package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBT;

public class NBTRootNode extends NBTNode {

	public NBTRootNode(NBT nbt) {
		super(nbt);
	}

	@Override
	public String toString() {
		return "<nbt_root>";
	}

}
