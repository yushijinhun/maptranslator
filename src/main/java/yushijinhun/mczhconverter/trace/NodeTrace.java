package yushijinhun.mczhconverter.trace;

import yushijinhun.mczhconverter.nbt.NBTBase;

abstract public class NodeTrace {

	public final NBTBase tag;

	public NodeTrace(NBTBase tag) {
		this.tag = tag;
	}
}
