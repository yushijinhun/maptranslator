package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTByteArray extends NBT {

	public static final byte ID = 7;

	private byte[] data;

	protected NBTByteArray() {}

	public NBTByteArray(byte[] data) {
		this.data = data;
	}

	@Override
	public NBT clone() {
		byte[] copy = new byte[data.length];
		System.arraycopy(data, 0, copy, 0, data.length);
		return new NBTByteArray(copy);
	}

	@Override
	public boolean equals(Object another) {
		return super.equals(another) ? Arrays.equals(data, ((NBTByteArray) another).data) : false;
	}

	public byte[] getByteArray() {
		return data;
	}

	@Override
	public byte getId() {
		return (byte) 7;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(data);
	}

	@Override
	protected void read(DataInput input) throws IOException {
		int len = input.readInt();
		data = new byte[len];
		input.readFully(data);
	}

	@Override
	public String toString() {
		return "[" + data.length + " bytes]";
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeInt(data.length);
		output.write(data);
	}

}
