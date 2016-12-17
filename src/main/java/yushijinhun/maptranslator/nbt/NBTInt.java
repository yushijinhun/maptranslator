package yushijinhun.maptranslator.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTInt extends NBT.NBTPrimitive {

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
		return (byte) 3;
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
		return (short) (data & 65535);
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
		return "" + data;
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeInt(data);
	}

	@Override
	public Object getData() {
		return data;
	}
}
