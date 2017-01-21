package org.to2mbn.maptranslator.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTFloat extends NBT.NBTPrimitive {

	public static final byte ID = 5;

	private float data;

	protected NBTFloat() {}

	public NBTFloat(float data) {
		this.data = data;
	}

	@Override
	public NBT clone() {
		return new NBTFloat(data);
	}

	@Override
	public boolean equals(Object another) {
		if (super.equals(another)) {
			NBTFloat casted = (NBTFloat) another;
			return data == casted.data;
		}
		return false;
	}

	@Override
	public byte getByte() {
		return (byte) ((int) Math.floor(data) & 255);
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
		return (byte) 5;
	}

	@Override
	public int getInt() {
		return (int) Math.floor(data);
	}

	@Override
	public long getLong() {
		return (long) data;
	}

	@Override
	public short getShort() {
		return (short) ((int) Math.floor(data) & 65535);
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Float.floatToIntBits(data);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		data = input.readFloat();
	}

	@Override
	public String valueToString() {
		return "" + data + "f";
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeFloat(data);
	}

}