package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NBTLongArray extends NBT {

	public static final byte ID = 12;

	private long[] data;

	protected NBTLongArray() {}

	public NBTLongArray(long[] data) {
		this.data = data;
	}

	NBTLongArray(List<Long> list) {
		data = new long[list.size()];
		for (int i = 0; i < data.length; i++) {
			Long element = list.get(i);
			data[i] = element == null ? 0 : element;
		}
	}

	@Override
	public NBT clone() {
		long[] copy = new long[data.length];
		System.arraycopy(data, 0, copy, 0, data.length);
		return new NBTLongArray(copy);
	}

	@Override
	public boolean equals(Object another) {
		return super.equals(another) ? Arrays.equals(data, ((NBTLongArray) another).data) : false;
	}

	@Override
	public byte getId() {
		return ID;
	}

	public long[] get() {
		return data;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(data);
	}

	@Override
	protected void read(DataInput in) throws IOException {
		int len = in.readInt();
		data = new long[len];

		for (int i = 0; i < len; ++i) {
			data[i] = in.readLong();
		}
	}

	@Override
	public String toString() {
		StringBuilder stringbuilder = new StringBuilder("[L;");

		for (int i = 0; i < data.length; ++i) {
			if (i != 0) {
				stringbuilder.append(',');
			}

			stringbuilder.append(data[i]).append('L');
		}

		return stringbuilder.append(']').toString();
	}

	@Override
	protected void write(DataOutput out) throws IOException {
		out.writeInt(data.length);

		for (long element : data) {
			out.writeLong(element);
		}
	}

}
