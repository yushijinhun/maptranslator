package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class NBTCompound extends NBT {

	public static final byte ID = 10;

	private static final int T_PRIMITIVE = 99;
	private static final Pattern PATTERN_NOT_ESCAPE = Pattern.compile("[A-Za-z0-9._+-]+");

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

	public boolean containsKey(String key) {
		return tagMap.containsKey(key);
	}

	public boolean containsKey(String key, int type) {
		NBT tag = get(key);
		if (tag == null) return false;
		byte actualType = tag.getId();

		if (actualType == type) {
			return true;
		} else if (type != T_PRIMITIVE) {
			return false;
		} else {
			return (actualType == 1) || (actualType == 2) || (actualType == 3) || (actualType == 4) || (actualType == 5) || (actualType == 6);
		}
	}

	@Override
	public byte getId() {
		return ID;
	}

	public NBT get(String key) {
		return tagMap.get(key);
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
					NBTCompound var5 = (NBTCompound) get(var3);
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

	public void remove(String key) {
		tagMap.remove(key);
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
			tagMap.forEach(
					(k, v) -> sb.append(PATTERN_NOT_ESCAPE.matcher(k).matches() ? k : NBTString.escapeString(k))
							.append(':')
							.append(v)
							.append(','));
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
