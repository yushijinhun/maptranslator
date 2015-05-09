package yushijinhun.mczhconverter;

import yushijinhun.mczhconverter.nbt.NBTBase;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;
import yushijinhun.mczhconverter.nbt.NBTTagList;
import yushijinhun.mczhconverter.nbt.NBTTagString;
import com.spreada.utils.chinese.ZHConverter;

public class NBTConverter {

	private ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);

	public NBTBase convert(NBTBase nbt) {
		nbt = nbt.copy();
		if (nbt instanceof NBTTagString) {
			nbt = convertString((NBTTagString) nbt);
		} else if (!isLeaf(nbt)) {
			convertTree(nbt);
		}
		return nbt;
	}

	private void convertTree(NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			convertCompound((NBTTagCompound) nbt);
		} else {
			convertList((NBTTagList) nbt);
		}
	}

	private void convertCompound(NBTTagCompound compound) {
		for (String name : compound.getKeySet()) {
			NBTBase nbt = compound.getTag(name);
			if (isLeaf(nbt)) {
				if (nbt instanceof NBTTagString) {
					compound.setTag(name, convertString((NBTTagString) nbt));
				}
			} else {
				convertTree(nbt);
			}
		}
	}

	private void convertList(NBTTagList list) {
		for (int i = 0; i < list.tagCount(); i++) {
			NBTBase nbt = list.get(i);
			if (isLeaf(nbt)) {
				if (nbt instanceof NBTTagString) {
					list.set(i, convertString((NBTTagString) nbt));
				}
			} else {
				convertTree(nbt);
			}
		}
	}

	private boolean isLeaf(NBTBase nbt) {
		if ((nbt instanceof NBTTagCompound) || (nbt instanceof NBTTagList)) {
			return false;
		}
		return true;
	}

	private NBTTagString convertString(NBTTagString nbt) {
		String src=nbt.getString();
		String out = converter.convert(src);
		if (!src.equals(out)) {
			System.out.printf("%s -> %s\n", src, out);
		}
		return new NBTTagString(out);
	}
}
