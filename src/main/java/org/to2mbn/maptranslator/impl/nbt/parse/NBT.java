package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBT {

	public abstract static class NBTPrimitive<T extends Number> extends NBT {

		public abstract T get();

	}

	protected static NBT createNewByType(byte id) {
		switch (id) {
			case NBTEnd.ID:
				return new NBTEnd();

			case NBTByte.ID:
				return new NBTByte();

			case NBTShort.ID:
				return new NBTShort();

			case NBTInt.ID:
				return new NBTInt();

			case NBTLong.ID:
				return new NBTLong();

			case NBTFloat.ID:
				return new NBTFloat();

			case NBTDouble.ID:
				return new NBTDouble();

			case NBTByteArray.ID:
				return new NBTByteArray();

			case NBTString.ID:
				return new NBTString();

			case NBTList.ID:
				return new NBTList();

			case NBTCompound.ID:
				return new NBTCompound();

			case NBTIntArray.ID:
				return new NBTIntArray();

			case NBTLongArray.ID:
				return new NBTLongArray();

			default:
				return null;
		}
	}

	public static final String[] NBT_TYPES = new String[] { "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]" };

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

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public abstract String toString();

	protected abstract void read(DataInput input) throws IOException;

	protected abstract void write(DataOutput output) throws IOException;

}
