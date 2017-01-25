package org.to2mbn.maptranslator.nbt.tree;

import org.to2mbn.maptranslator.core.tree.ListNode;
import org.to2mbn.maptranslator.nbt.parse.NBT;
import org.to2mbn.maptranslator.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.nbt.parse.NBTList;

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

	@Override
	public String getDisplayText() {
		if (nbt instanceof NBTCompound || nbt instanceof NBTList) {
			return toString();
		} else {
			return "[" + index + "] = " + valueToString(nbt);
		}
	}

}
