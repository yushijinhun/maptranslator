package yushijinhun.maptranslator.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTByte extends NBT.NBTPrimitive {

	private byte data;

	NBTByte() {}

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
	public byte getByte() {
		return data;
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
		return (byte) 1;
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
	void read(DataInput input) throws IOException {
		data = input.readByte();
	}

	@Override
	public String toString() {
		return "" + data + "b";
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeByte(data);
	}
}
