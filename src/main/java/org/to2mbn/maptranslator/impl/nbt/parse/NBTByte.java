package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTByte extends NBT.NBTPrimitive<Byte> {

	public static final byte ID = 1;

	private byte data;

	protected NBTByte() {}

	public NBTByte(byte data) {
		this.data = data;
	}

	@Override
	public NBT clone() {
		return new NBTByte(data);
	}

	@Override
	public boolean equals(Object p_equals_1_) {
		if (super.equals(p_equals_1_)) {
			NBTByte var2 = (NBTByte) p_equals_1_;
			return data == var2.data;
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
		data = input.readByte();
	}

	@Override
	public String toString() {
		return "" + data + "b";
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeByte(data);
	}

	@Override
	public Byte get() {
		return data;
	}

}
