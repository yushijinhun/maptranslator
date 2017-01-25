package org.to2mbn.maptranslator.nbt.tree;

import org.to2mbn.maptranslator.core.tree.Node;
import org.to2mbn.maptranslator.nbt.parse.NBT;
import org.to2mbn.maptranslator.nbt.parse.NBTString;

public abstract class NBTNode extends Node {

	public NBT nbt;

	public NBTNode(NBT nbt) {
		this.nbt = nbt;
	}

	public void replaceNBT(NBT newnbt) {
		nbt = newnbt;
	}

	public static String valueToString(NBT nbt) {
		if (nbt instanceof NBTString) {
			return ((NBTString) nbt).getString();
		} else {
			return nbt.toString();
		}
	}

	@Override
	public String getStringValue() {
		return valueToString(nbt);
	}

}
