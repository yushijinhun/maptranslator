package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTShort extends NBT.NBTPrimitive {

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
	public byte getByte() {
		return (byte) (data & 255);
	}

	@Override
	public double getDouble() {
		return data;
	}

	@Override
	public float getFloat() {
		return data;
	}

	@Override
	public byte getId() {
		return (byte) 2;
	}

	@Override
	public int getInt() {
		return data;
	}

	@Override
	public long getLong() {
		return data;
	}

	@Override
	public short getShort() {
		return data;
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
	public String valueToString() {
		return "" + data + "s";
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeShort(data);
	}

}
