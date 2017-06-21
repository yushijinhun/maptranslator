package org.to2mbn.maptranslator.impl.nbt.process;

import static org.to2mbn.maptranslator.process.TreeConstructorUtils.checkedParse;
import org.to2mbn.maptranslator.impl.nbt.parse.JsonNBTConverter;
import org.to2mbn.maptranslator.impl.nbt.parse.NBT;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTList;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTVersion;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTVersionConfig;
import org.to2mbn.maptranslator.impl.nbt.tree.NBTListNode;
import org.to2mbn.maptranslator.impl.nbt.tree.NBTMapNode;
import org.to2mbn.maptranslator.impl.nbt.tree.NBTNode;
import org.to2mbn.maptranslator.impl.nbt.tree.NBTRootNode;

public final class NBTTreeConstructor {

	private NBTTreeConstructor() {}

	public static NBTCompound parseNBT(String nbt) {
		if (isCrossVersionParsing()) {
			return checkedParse(JsonNBTConverter.instance()::parse,
					input -> NBTVersion.setCurrentConfig(
							new NBTVersionConfig(NBTVersion.defaultConfig.getOutputVersion(), NBTVersion.defaultConfig.getOutputVersion()),
							() -> JsonNBTConverter.instance().parse(input)),
					nbt);
		} else {
			return checkedParse(JsonNBTConverter.instance()::parse, nbt);
		}
	}

	private static boolean isCrossVersionParsing() {
		return NBTVersion.defaultConfig.getInputVersion() != NBTVersion.defaultConfig.getOutputVersion();
	}

	public static NBTRootNode constructNBT(String nbt) {
		return construct(parseNBT(nbt));
	}

	public static NBTRootNode construct(NBT nbt) {
		NBTRootNode root = new NBTRootNode(nbt);
		constructSubtree(root);
		return root;
	}

	public static void constructSubtree(NBTNode node) {
		NBT nbt = node.nbt;
		if (nbt instanceof NBTCompound) {
			NBTCompound casted = ((NBTCompound) nbt);
			casted.tags().keySet().stream()
					.map(String::intern)
					.sorted()
					.forEach(key -> {
						NBTMapNode child = new NBTMapNode(casted.get(key), key);
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
