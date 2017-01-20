package yushijinhun.maptranslator.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTDouble extends NBT.NBTPrimitive {

	public static final byte ID = 6;

	private double data;

	protected NBTDouble() {}

	public NBTDouble(double data) {
		this.data = data;
	}

	@Override
	public NBT clone() {
		return new NBTDouble(data);
	}

	@Override
	public boolean equals(Object another) {
		if (super.equals(another)) {
			NBTDouble casted = (NBTDouble) another;
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
		return (float) data;
	}

	@Override
	public byte getId() {
		return (byte) 6;
	}

	@Override
	public int getInt() {
		return (int) Math.floor(data);
	}

	@Override
	public long getLong() {
		return (long) Math.floor(data);
	}

	@Override
	public short getShort() {
		return (short) ((int) Math.floor(data) & 65535);
	}

	@Override
	public int hashCode() {
		long var1 = Double.doubleToLongBits(data);
		return super.hashCode() ^ (int) (var1 ^ (var1 >>> 32));
	}

	@Override
	protected void read(DataInput input) throws IOException {
		data = input.readDouble();
	}

	@Override
	public String valueToString() {
		return "" + data + "d";
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeDouble(data);
	}

}
