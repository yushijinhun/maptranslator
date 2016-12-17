package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTCompound;
import yushijinhun.maptranslator.nbt.NBTList;

public final class NBTTreeConstructor {

	private NBTTreeConstructor() {}

	public static NBTRootNode construct(NBT nbt) {
		NBTRootNode root = new NBTRootNode(nbt);
		constructSubtree(root);
		return root;
	}

	private static void constructSubtree(NBTNode node) {
		NBT nbt = node.nbt;
		if (nbt instanceof NBTCompound) {
			NBTCompound casted = ((NBTCompound) nbt);
			casted.tags().forEach((key, childnbt) -> {
				NBTMapNode child = new NBTMapNode(childnbt, key);
				constructSubtree(child);
				node.addChild(child);
			});
		} else if (nbt instanceof NBTList) {
			NBTList casted = (NBTList) nbt;
			for (int i = 0; i < casted.size(); i++) {
				NBTListNode child = new NBTListNode(casted.get(i), i);
				constructSubtree(child);
				node.addChild(child);
			}
		}
	}

}
