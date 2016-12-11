package yushijinhun.mczhconverter.trace;

import yushijinhun.mczhconverter.nbt.NBTBase;
import yushijinhun.mczhconverter.nbt.NBTTagString;

public class CompoundChild extends NodeTrace {

	public final String key;

	public CompoundChild(NBTBase tag, String key) {
		super(tag);
		this.key = key;
	}

	@Override
	public String toString() {
		return key + ((tag instanceof NBTTagString) ? " = " + ((NBTTagString) tag).getString() : "");
	}

}
