package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTEnd extends NBT {

	public static final byte ID = 0;

	@Override
	public NBT clone() {
		return new NBTEnd();
	}

	@Override
	public byte getId() {
		return (byte) 0;
	}

	@Override
	protected void read(DataInput input) throws IOException {}

	@Override
	public String toString() {
		return "END";
	}

	@Override
	protected void write(DataOutput output) throws IOException {}

}
