package yushijinhun.mczhconverter.trace;

import yushijinhun.mczhconverter.nbt.NBTBase;

public class RootTag extends NodeTrace {

	public final String source;

	public RootTag(NBTBase tag, String source) {
		super(tag);
		this.source = source;
	}

	@Override
	public String toString() {
		return source;
	}

}
