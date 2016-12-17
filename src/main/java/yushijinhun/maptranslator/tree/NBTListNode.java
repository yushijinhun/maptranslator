package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTList;

public class NBTListNode extends NBTNode implements ListNode {

	public final int index;

	public NBTListNode(NBT nbt, int index) {
		super(nbt);
		this.index = index;
	}

	@Override
	public String toString() {
		return "[" + index + "]";
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public void replaceNBT(NBT newnbt) {
		super.replaceNBT(newnbt);
		((NBTList) ((NBTNode) parent()).nbt).set(index, newnbt);
	}

}
