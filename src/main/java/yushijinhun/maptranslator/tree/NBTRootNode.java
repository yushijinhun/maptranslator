package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBT;

public class NBTRootNode extends NBTNode implements ArgumentNode {

	public NBTRootNode(NBT nbt) {
		super(nbt);
	}

	@Override
	public String toString() {
		return "<nbt_root>";
	}

	@Override
	public String toArgumentString() {
		return nbt.toString();
	}

}
