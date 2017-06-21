package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTInt extends NBT.NBTPrimitive<Integer> {

	public static final byte ID = 3;

	private int data;

	protected NBTInt() {}

	public NBTInt(int data) {
		this.data = data;
	}

	@Override
	public NBT clone() {
		return new NBTInt(data);
	}

	@Override
	public boolean equals(Object another) {
		if (super.equals(another)) {
			NBTInt casted = (NBTInt) another;
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
		data = input.readInt();
	}

	@Override
	public String toString() {
		return String.valueOf(data);
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeInt(data);
	}

	@Override
	public Integer get() {
		return data;
	}

}
