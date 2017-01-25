package org.to2mbn.maptranslator.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NBTCompound extends NBT {

	public static final byte ID = 10;

	private static final int T_PRIMITIVE = 99;

	protected static String readKey(DataInput input) throws IOException {
		return input.readUTF();
	}

	protected static NBT readNBT(byte id, String key, DataInput input) throws IOException {
		NBT nbt = NBT.createNewByType(id);
		nbt.read(input);
		return nbt;
	}

	protected static byte readType(DataInput input) throws IOException {
		return input.readByte();
	}

	protected static void writeEntry(String name, NBT data, DataOutput output) throws IOException {
		output.writeByte(data.getId());

		if (data.getId() != 0) {
			output.writeUTF(name);
			data.write(output);
		}
	}

	private Map<String, NBT> tagMap = new LinkedHashMap<>();

	@Override
	public NBT clone() {
		NBTCompound copy = new NBTCompound();
		tagMap.forEach((k, v) -> copy.tagMap.put(k, v.clone()));
		return copy;
	}

	@Override
	public boolean equals(Object another) {
		if (super.equals(another)) {
			NBTCompound casted = (NBTCompound) another;
			return tagMap.equals(casted.tagMap);
		}
		return false;
	}

	public boolean getBoolean(String key) {
		return getByte(key) != 0;
	}

	public byte getByte(String key) {
		checkKeyExists(key, T_PRIMITIVE);
		return ((NBT.NBTPrimitive) tagMap.get(key)).getByte();
	}

	public byte[] getByteArray(String key) {
		checkKeyExists(key, 7);
		return ((NBTByteArray) tagMap.get(key)).getByteArray();
	}

	public NBTCompound getCompound(String key) {
		checkKeyExists(key, 10);
		return (NBTCompound) tagMap.get(key);
	}

	public double getDouble(String key) {
		checkKeyExists(key, T_PRIMITIVE);
		return ((NBT.NBTPrimitive) tagMap.get(key)).getDouble();
	}

	public float getFloat(String key) {
		checkKeyExists(key, T_PRIMITIVE);
		return ((NBT.NBTPrimitive) tagMap.get(key)).getFloat();
	}

	public int[] getIntArray(String key) {
		checkKeyExists(key, 11);
		return ((NBTIntArray) tagMap.get(key)).getIntArray();
	}

	public int getInteger(String key) {
		checkKeyExists(key, T_PRIMITIVE);
		return ((NBT.NBTPrimitive) tagMap.get(key)).getInt();
	}

	public long getLong(String key) {
		checkKeyExists(key, T_PRIMITIVE);
		return ((NBT.NBTPrimitive) tagMap.get(key)).getLong();
	}

	public short getShort(String key) {
		checkKeyExists(key, T_PRIMITIVE);
		return ((NBT.NBTPrimitive) tagMap.get(key)).getShort();
	}

	public String getString(String key) {
		checkKeyExists(key, 8);
		return tagMap.get(key).getString();
	}

	public boolean containsKey(String key) {
		return tagMap.containsKey(key);
	}

	public boolean containsKey(String key, int type) {
		byte actualType = getTagType(key);

		if (actualType == type) {
			return true;
		} else if (type != T_PRIMITIVE) {
			return false;
		} else {
			return (actualType == 1) || (actualType == 2) || (actualType == 3) || (actualType == 4) || (actualType == 5) || (actualType == 6);
		}
	}

	private void checkKeyExists(String key, int type) {
		if (!containsKey(key, type)) throw new IllegalArgumentException("no such tag: " + key);
	}

	@Override
	public byte getId() {
		return (byte) 10;
	}

	public NBT get(String key) {
		return tagMap.get(key);
	}

	public NBTList getTagList(String key, int type) {
		if (getTagType(key) != 9) {
			return new NBTList();
		}
		NBTList var3 = (NBTList) tagMap.get(key);
		return (var3.size() > 0) && (var3.getTagType() != type) ? new NBTList() : var3;
	}

	public byte getTagType(String key) {
		NBT nbt = tagMap.get(key);
		return nbt != null ? nbt.getId() : 0;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ tagMap.hashCode();
	}

	/*
	 * Merges this NBTCompound with the given compound. Any sub-compounds are merged using the same methods, other
	 * types of tags are overwritten from the given compound.
	 */
	public void merge(NBTCompound other) {
		Iterator<String> var2 = other.tagMap.keySet().iterator();

		while (var2.hasNext()) {
			String var3 = var2.next();
			NBT var4 = other.tagMap.get(var3);

			if (var4.getId() == 10) {
				if (this.containsKey(var3, 10)) {
					NBTCompound var5 = getCompound(var3);
					var5.merge((NBTCompound) var4);
				} else {
					put(var3, var4.clone());
				}
			} else {
				put(var3, var4.clone());
			}
		}
	}

	@Override
	protected void read(DataInput input) throws IOException {
		tagMap.clear();
		byte type;

		while ((type = readType(input)) != 0) {
			String key = readKey(input);
			NBT val = readNBT(type, key, input);
			tagMap.put(key, val);
		}
	}

	public void removeTag(String key) {
		tagMap.remove(key);
	}

	public void putBoolean(String key, boolean value) {
		putByte(key, (byte) (value ? 1 : 0));
	}

	public void putByte(String key, byte value) {
		tagMap.put(key, new NBTByte(value));
	}

	public void putByteArray(String key, byte[] value) {
		tagMap.put(key, new NBTByteArray(value));
	}

	public void putDouble(String key, double value) {
		tagMap.put(key, new NBTDouble(value));
	}

	public void putFloat(String key, float value) {
		tagMap.put(key, new NBTFloat(value));
	}

	public void putIntArray(String key, int[] value) {
		tagMap.put(key, new NBTIntArray(value));
	}

	public void putInteger(String key, int value) {
		tagMap.put(key, new NBTInt(value));
	}

	public void putLong(String key, long value) {
		tagMap.put(key, new NBTLong(value));
	}

	public void putShort(String key, short value) {
		tagMap.put(key, new NBTShort(value));
	}

	public void putString(String key, String value) {
		tagMap.put(key, new NBTString(value));
	}

	public void put(String key, NBT value) {
		tagMap.put(key, value);
	}

	public Map<String, NBT> tags() {
		return tagMap;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		if (!tagMap.isEmpty()) {
			tagMap.forEach((k, v) -> sb.append(k).append(':').append(v).append(','));
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append('}');
		return sb.toString();
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		for (Entry<String, NBT> entry : tagMap.entrySet()) {
			writeEntry(entry.getKey(), entry.getValue(), output);
		}
		output.writeByte(0);
	}

}
