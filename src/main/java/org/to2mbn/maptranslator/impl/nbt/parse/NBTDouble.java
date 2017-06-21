package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTDouble extends NBT.NBTPrimitive<Double> {

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
	public byte getId() {
		return ID;
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
	public String toString() {
		return data + "d";
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeDouble(data);
	}

	@Override
	public Double get() {
		return data;
	}

}
