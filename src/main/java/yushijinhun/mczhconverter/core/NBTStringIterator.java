package yushijinhun.mczhconverter.core;

import yushijinhun.mczhconverter.nbt.NBTBase;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;
import yushijinhun.mczhconverter.nbt.NBTTagList;
import yushijinhun.mczhconverter.nbt.NBTTagString;

public final class NBTStringIterator {

	public static void accept(NBTBase nbt, NBTStringVisitor visitor) {
		if (!isLeaf(nbt)) {
			acceptTree(nbt, visitor);
		}
	}

	private static void acceptTree(NBTBase nbt, NBTStringVisitor visitor) {
		if (nbt instanceof NBTTagCompound) {
			acceptCompound((NBTTagCompound) nbt, visitor);
		} else {
			acceptList((NBTTagList) nbt, visitor);
		}
	}

	private static void acceptCompound(NBTTagCompound compound, NBTStringVisitor visitor) {
		for (String name : compound.getKeySet()) {
			NBTBase nbt = compound.getTag(name);
			if (isLeaf(nbt)) {
				if (nbt instanceof NBTTagString) {
					compound.setTag(name, acceptString((NBTTagString) nbt, visitor));
				}
			} else {
				acceptTree(nbt, visitor);
			}
		}
	}

	private static void acceptList(NBTTagList list, NBTStringVisitor visitor) {
		for (int i = 0; i < list.tagCount(); i++) {
			NBTBase nbt = list.get(i);
			if (isLeaf(nbt)) {
				if (nbt instanceof NBTTagString) {
					list.set(i, acceptString((NBTTagString) nbt, visitor));
				}
			} else {
				acceptTree(nbt, visitor);
			}
		}
	}

	private static boolean isLeaf(NBTBase nbt) {
		if ((nbt instanceof NBTTagCompound) || (nbt instanceof NBTTagList)) {
			return false;
		}
		return true;
	}

	private static NBTTagString acceptString(NBTTagString nbt, NBTStringVisitor visitor) {
		String result = visitor.visit(nbt.getString());
		return result == null ? nbt : new NBTTagString(result);
	}

	private NBTStringIterator() {
	}
}
