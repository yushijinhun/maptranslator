package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTFloat extends NBT.NBTPrimitive<Float> {

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
	public byte getId() {
		return ID;
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
	public String toString() {
		return data + "f";
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeFloat(data);
	}

	@Override
	public Float get() {
		return data;
	}

}
