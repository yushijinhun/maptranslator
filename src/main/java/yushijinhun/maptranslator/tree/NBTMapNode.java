package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBTBase;

public class NBTMapNode extends NBTNode {

	public final String key;

	public NBTMapNode(NBTBase nbt, String key) {
		super(nbt);
		this.key = key;
	}

	@Override
	public String toString() {
		return key;
	}

}
