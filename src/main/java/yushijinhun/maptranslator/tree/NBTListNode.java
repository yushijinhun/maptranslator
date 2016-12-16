package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBTBase;

public class NBTListNode extends NBTNode {

	public final int index;

	public NBTListNode(NBTBase nbt, int index) {
		super(nbt);
		this.index = index;
	}

	@Override
	public String toString() {
		return "[" + index + "]";
	}

}
