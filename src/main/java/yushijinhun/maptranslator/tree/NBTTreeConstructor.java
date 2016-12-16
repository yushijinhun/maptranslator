package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBTBase;
import yushijinhun.maptranslator.nbt.NBTTagCompound;
import yushijinhun.maptranslator.nbt.NBTTagList;

public final class NBTTreeConstructor {

	private NBTTreeConstructor() {}

	public static NBTRootNode construct(NBTBase nbt) {
		NBTRootNode root = new NBTRootNode(nbt);
		constructSubtree(root);
		return root;
	}

	private static void constructSubtree(NBTNode node) {
		NBTBase nbt = node.nbt;
		if (nbt instanceof NBTTagCompound) {
			NBTTagCompound casted = ((NBTTagCompound) nbt);
			for (String key : casted.getKeySet()) {
				NBTMapNode child = new NBTMapNode(casted.getTag(key), key);
				constructSubtree(child);
				node.addChild(child);
			}
		} else if (nbt instanceof NBTTagList) {
			NBTTagList casted = (NBTTagList) nbt;
			for (int i = 0; i < casted.tagCount(); i++) {
				NBTListNode child = new NBTListNode(casted.get(i), i);
				constructSubtree(child);
				node.addChild(child);
			}
		}
	}

}
