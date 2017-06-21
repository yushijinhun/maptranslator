package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NBTIntArray extends NBT {

	public static final byte ID = 11;

	private int[] data;

	protected NBTIntArray() {}

	public NBTIntArray(int[] data) {
		this.data = data;
	}

	NBTIntArray(List<Integer> list) {
		data = new int[list.size()];
		for (int i = 0; i < data.length; i++) {
			Integer element = list.get(i);
			data[i] = element == null ? 0 : element;
		}
	}

	@Override
	public NBT clone() {
		int[] copy = new int[data.length];
		System.arraycopy(data, 0, copy, 0, data.length);
		return new NBTIntArray(copy);
	}

	@Override
	public boolean equals(Object another) {
		return super.equals(another) ? Arrays.equals(data, ((NBTIntArray) another).data) : false;
	}

	@Override
	public byte getId() {
		return ID;
	}

	public int[] get() {
		return data;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(data);
	}

	@Override
	protected void read(DataInput in) throws IOException {
		int len = in.readInt();
		data = new int[len];

		for (int i = 0; i < len; ++i) {
			data[i] = in.readInt();
		}
	}

	@Override
	public String toString() {
		switch (NBTVersion.getCurrentConfig().getOutputVersion()) {
			case MC_1_12:
				return toString_MC_1_12();
			case MC_OLD:
				return toString_MC_OLD();
		}
		throw new IllegalStateException("Unexpected output_version");
	}

	private String toString_MC_OLD() {
		StringBuilder sb = new StringBuilder("[");
		if (data.length > 0) {
			for (int d : data)
				sb.append(d).append(',');
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(']');
		return sb.toString();
	}

	private String toString_MC_1_12() {
		StringBuilder sb = new StringBuilder("[I;");
		for (int i = 0; i < data.length; ++i) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(data[i]);
		}
		return sb.append(']').toString();
	}

	@Override
	protected void write(DataOutput out) throws IOException {
		out.writeInt(data.length);

		for (int element : data) {
			out.writeInt(element);
		}
	}

}
