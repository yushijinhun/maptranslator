package yushijinhun.mczhconverter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTBase.NBTPrimitive {
	/** The double value for the tag. */
	private double data;

	NBTTagDouble() {
	}

	public NBTTagDouble(double data) {
		this.data = data;
	}

	/**
	 * Creates a clone of the tag.
	 */
	@Override
	public NBTBase copy() {
		return new NBTTagDouble(data);
	}

	@Override
	public boolean equals(Object p_equals_1_) {
		if (super.equals(p_equals_1_)) {
			NBTTagDouble var2 = (NBTTagDouble) p_equals_1_;
			return data == var2.data;
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

	/**
	 * Gets the type byte for the tag.
	 */
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
	void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
		sizeTracker.read(64L);
		data = input.readDouble();
	}

	@Override
	public String toString() {
		return "" + data + "d";
	}

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension classes
	 */
	@Override
	void write(DataOutput output) throws IOException {
		output.writeDouble(data);
	}
}
