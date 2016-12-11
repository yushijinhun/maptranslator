package yushijinhun.mczhconverter.trace;

import yushijinhun.mczhconverter.nbt.NBTBase;
import yushijinhun.mczhconverter.nbt.NBTTagString;

public class ListChild extends NodeTrace {

	public final int idx;

	public ListChild(NBTBase tag, int idx) {
		super(tag);
		this.idx = idx;
	}

	@Override
	public String toString() {
		return "[" + idx + "]" + ((tag instanceof NBTTagString) ? " = " + ((NBTTagString) tag).getString() : "");
	}

}
