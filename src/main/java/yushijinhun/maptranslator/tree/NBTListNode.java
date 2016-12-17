package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBT;

public class NBTListNode extends NBTNode {

	public final int index;

	public NBTListNode(NBT nbt, int index) {
		super(nbt);
		this.index = index;
	}

	@Override
	public String toString() {
		return "[" + index + "]";
	}

}
