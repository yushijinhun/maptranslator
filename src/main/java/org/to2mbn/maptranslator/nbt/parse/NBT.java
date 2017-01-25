package org.to2mbn.maptranslator.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBT {

	public abstract static class NBTPrimitive extends NBT {

		String _str;

		public abstract byte getByte();

		public abstract double getDouble();

		public abstract float getFloat();

		public abstract int getInt();

		public abstract long getLong();

		public abstract short getShort();

		@Override
		public String toString() {
			if (_str != null) return _str;
			return valueToString();
		}

		abstract protected String valueToString();

	}

	protected static NBT createNewByType(byte id) {
		switch (id) {
			case 0:
				return new NBTEnd();

			case 1:
				return new NBTByte();

			case 2:
				return new NBTShort();

			case 3:
				return new NBTInt();

			case 4:
				return new NBTLong();

			case 5:
				return new NBTFloat();

			case 6:
				return new NBTDouble();

			case 7:
				return new NBTByteArray();

			case 8:
				return new NBTString();

			case 9:
				return new NBTList();

			case 10:
				return new NBTCompound();

			case 11:
				return new NBTIntArray();

			default:
				return null;
		}
	}

	public static final String[] NBT_TYPES = new String[] { "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]" };

	@Override
	public abstract NBT clone();

	@Override
	public boolean equals(Object another) {
		if (!(another instanceof NBT)) {
			return false;
		}
		NBT casted = (NBT) another;
		return getId() == casted.getId();
	}

	public abstract byte getId();

	protected String getString() {
		return toString();
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public abstract String toString();

	protected abstract void read(DataInput input) throws IOException;

	protected abstract void write(DataOutput output) throws IOException;

}
