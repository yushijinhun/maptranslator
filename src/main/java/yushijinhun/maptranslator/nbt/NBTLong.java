package yushijinhun.maptranslator.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTLong extends NBT.NBTPrimitive {

	public static final byte ID = 4;

	private long data;

	protected NBTLong() {}

	public NBTLong(long data) {
		this.data = data;
	}

	@Override
	public NBT clone() {
		return new NBTLong(data);
	}

	@Override
	public boolean equals(Object another) {
		if (super.equals(another)) {
			NBTLong casted = (NBTLong) another;
			return data == casted.data;
		}
		return false;
	}

	@Override
	public byte getByte() {
		return (byte) ((int) (data & 255L));
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
		return (byte) 4;
	}

	@Override
	public int getInt() {
		return (int) (data & -1L);
	}

	@Override
	public long getLong() {
		return data;
	}

	@Override
	public short getShort() {
		return (short) ((int) (data & 65535L));
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ (int) (data ^ (data >>> 32));
	}

	@Override
	protected void read(DataInput input) throws IOException {
		data = input.readLong();
	}

	@Override
	public String toString() {
		return "" + data + "L";
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeLong(data);
	}

	@Override
	public Object getData() {
		return data;
	}
}
