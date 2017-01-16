package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTCompound;
import yushijinhun.maptranslator.nbt.NBTList;

public class NBTMapNode extends NBTNode implements MapNode {

	public final String key;

	public NBTMapNode(NBT nbt, String key) {
		super(nbt);
		this.key = key;
	}

	@Override
	public String toString() {
		return key;
	}

	@Override
	public String key() {
		return key;
	}

	@Override
	public void replaceNBT(NBT newnbt) {
		super.replaceNBT(newnbt);
		((NBTCompound) ((NBTNode) parent()).nbt).put(key, newnbt);
	}

	@Override
	public String getDisplayText() {
		if (nbt instanceof NBTCompound || nbt instanceof NBTList) {
			return toString();
		} else {
			return key + " = " + valueToString(nbt);
		}
	}

}
