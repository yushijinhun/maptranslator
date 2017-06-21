package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTShort extends NBT.NBTPrimitive<Short> {

	public static final byte ID = 2;

	private short data;

	protected NBTShort() {}

	public NBTShort(short data) {
		this.data = data;
	}

	@Override
	public NBT clone() {
		return new NBTShort(data);
	}

	@Override
	public boolean equals(Object another) {
		if (super.equals(another)) {
			NBTShort casted = (NBTShort) another;
			return data == casted.data;
		}
		return false;
	}

	@Override
	public byte getId() {
		return ID;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ data;
	}

	@Override
	protected void read(DataInput input) throws IOException {
		data = input.readShort();
	}

	@Override
	public String toString() {
		return data + "s";
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeShort(data);
	}

	@Override
	public Short get() {
		return data;
	}

}
