package org.to2mbn.maptranslator.impl.nbt.tree;

import org.to2mbn.maptranslator.impl.nbt.parse.NBT;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTList;
import org.to2mbn.maptranslator.tree.ListNode;

public class NBTListNode extends NBTNode implements ListNode {

	public final int index;

	public NBTListNode(NBT nbt, int index) {
		super(nbt);
		this.index = index;
	}

	@Override
	public String toString() {
		return getPathName();
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

	@Override
	public String getDisplayText() {
		if (nbt instanceof NBTCompound || nbt instanceof NBTList) {
			return toString();
		} else {
			return "[" + index + "] = " + valueToString(nbt);
		}
	}

}
