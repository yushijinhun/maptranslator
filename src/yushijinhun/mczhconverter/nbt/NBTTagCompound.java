package yushijinhun.mczhconverter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NBTTagCompound extends NBTBase {

	private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
		return input.readUTF();
	}

	static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
		NBTBase var5 = NBTBase.createNewByType(id);
		var5.read(input, depth, sizeTracker);
		return var5;
	}

	private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
		return input.readByte();
	}

	private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException {
		output.writeByte(data.getId());

		if (data.getId() != 0) {
			output.writeUTF(name);
			data.write(output);
		}
	}

	/**
	 * The key-value pairs for the tag. Each key is a UTF string, each value is a tag.
	 */
	private Map<String, NBTBase> tagMap = new HashMap<>();

	/**
	 * Creates a clone of the tag.
	 */
	@Override
	public NBTBase copy() {
		NBTTagCompound var1 = new NBTTagCompound();
		Iterator<String> var2 = tagMap.keySet().iterator();

		while (var2.hasNext()) {
			String var3 = var2.next();
			var1.setTag(var3, tagMap.get(var3).copy());
		}

		return var1;
	}

	@Override
	public boolean equals(Object p_equals_1_) {
		if (super.equals(p_equals_1_)) {
			NBTTagCompound var2 = (NBTTagCompound) p_equals_1_;
			return tagMap.entrySet().equals(var2.tagMap.entrySet());
		}
		return false;
	}

	/**
	 * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
	 * method.
	 */
	public boolean getBoolean(String key) {
		return getByte(key) != 0;
	}

	/**
	 * Retrieves a byte value using the specified key, or 0 if no such key was stored.
	 */
	public byte getByte(String key) {
		try {
			return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) tagMap.get(key)).getByte();
		} catch (ClassCastException var3) {
			return (byte) 0;
		}
	}

	/**
	 * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
	 */
	public byte[] getByteArray(String key) {
		return !this.hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray) tagMap.get(key)).getByteArray();
	}

	/**
	 * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
	 * stored.
	 */
	public NBTTagCompound getCompoundTag(String key) {
		return !this.hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound) tagMap.get(key);
	}

	/**
	 * Retrieves a double value using the specified key, or 0 if no such key was stored.
	 */
	public double getDouble(String key) {
		try {
			return !this.hasKey(key, 99) ? 0.0D : ((NBTBase.NBTPrimitive) tagMap.get(key)).getDouble();
		} catch (ClassCastException var3) {
			return 0.0D;
		}
	}

	/**
	 * Retrieves a float value using the specified key, or 0 if no such key was stored.
	 */
	public float getFloat(String key) {
		try {
			return !this.hasKey(key, 99) ? 0.0F : ((NBTBase.NBTPrimitive) tagMap.get(key)).getFloat();
		} catch (ClassCastException var3) {
			return 0.0F;
		}
	}

	/**
	 * Gets the type byte for the tag.
	 */
	@Override
	public byte getId() {
		return (byte) 10;
	}

	/**
	 * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
	 */
	public int[] getIntArray(String key) {
		return !this.hasKey(key, 11) ? new int[0] : ((NBTTagIntArray) tagMap.get(key)).getIntArray();
	}

	/**
	 * Retrieves an integer value using the specified key, or 0 if no such key was stored.
	 */
	public int getInteger(String key) {
		try {
			return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) tagMap.get(key)).getInt();
		} catch (ClassCastException var3) {
			return 0;
		}
	}

	/**
	 * Gets a set with the names of the keys in the tag compound.
	 */
	public Set<String> getKeySet() {
		return tagMap.keySet();
	}

	/**
	 * Retrieves a long value using the specified key, or 0 if no such key was stored.
	 */
	public long getLong(String key) {
		try {
			return !this.hasKey(key, 99) ? 0L : ((NBTBase.NBTPrimitive) tagMap.get(key)).getLong();
		} catch (ClassCastException var3) {
			return 0L;
		}
	}

	/**
	 * Retrieves a short value using the specified key, or 0 if no such key was stored.
	 */
	public short getShort(String key) {
		try {
			return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive) tagMap.get(key)).getShort();
		} catch (ClassCastException var3) {
			return (short) 0;
		}
	}

	/**
	 * Retrieves a string value using the specified key, or an empty string if no such key was stored.
	 */
	public String getString(String key) {
		try {
			return !this.hasKey(key, 8) ? "" : tagMap.get(key).getString();
		} catch (ClassCastException var3) {
			return "";
		}
	}

	/**
	 * gets a generic tag with the specified name
	 */
	public NBTBase getTag(String key) {
		return tagMap.get(key);
	}

	/**
	 * Gets the NBTTagList object with the given name. Args: name, NBTBase type
	 */
	public NBTTagList getTagList(String key, int type) {
		if (getTagType(key) != 9) {
			return new NBTTagList();
		}
		NBTTagList var3 = (NBTTagList) tagMap.get(key);
		return (var3.tagCount() > 0) && (var3.getTagType() != type) ? new NBTTagList() : var3;
	}

	/**
	 * Get the Type-ID for the entry with the given key
	 */
	public byte getTagType(String key) {
		NBTBase var2 = tagMap.get(key);
		return var2 != null ? var2.getId() : 0;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ tagMap.hashCode();
	}

	/**
	 * Returns whether the given string has been previously stored as a key in the map.
	 */
	public boolean hasKey(String key) {
		return tagMap.containsKey(key);
	}

	public boolean hasKey(String key, int type) {
		byte var3 = getTagType(key);

		if (var3 == type) {
			return true;
		} else if (type != 99) {
			if (var3 > 0) {
				;
			}

			return false;
		} else {
			return (var3 == 1) || (var3 == 2) || (var3 == 3) || (var3 == 4) || (var3 == 5) || (var3 == 6);
		}
	}

	/**
	 * Return whether this compound has no tags.
	 */
	@Override
	public boolean hasNoTags() {
		return tagMap.isEmpty();
	}

	/**
	 * Merges this NBTTagCompound with the given compound. Any sub-compounds are merged using the same methods, other
	 * types of tags are overwritten from the given compound.
	 */
	public void merge(NBTTagCompound other) {
		Iterator<String> var2 = other.tagMap.keySet().iterator();

		while (var2.hasNext()) {
			String var3 = var2.next();
			NBTBase var4 = other.tagMap.get(var3);

			if (var4.getId() == 10) {
				if (this.hasKey(var3, 10)) {
					NBTTagCompound var5 = getCompoundTag(var3);
					var5.merge((NBTTagCompound) var4);
				} else {
					setTag(var3, var4.copy());
				}
			} else {
				setTag(var3, var4.copy());
			}
		}
	}

	@Override
	void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
		if (depth > 512) {
			throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
		}
		tagMap.clear();
		byte var4;

		while ((var4 = readType(input, sizeTracker)) != 0) {
			String var5 = readKey(input, sizeTracker);
			sizeTracker.read(16 * var5.length());
			NBTBase var6 = readNBT(var4, var5, input, depth + 1, sizeTracker);
			tagMap.put(var5, var6);
		}
	}

	/**
	 * Remove the specified tag.
	 */
	public void removeTag(String key) {
		tagMap.remove(key);
	}

	/**
	 * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
	 */
	public void setBoolean(String key, boolean value) {
		setByte(key, (byte) (value ? 1 : 0));
	}

	/**
	 * Stores a new NBTTagByte with the given byte value into the map with the given string key.
	 */
	public void setByte(String key, byte value) {
		tagMap.put(key, new NBTTagByte(value));
	}

	/**
	 * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
	 */
	public void setByteArray(String key, byte[] value) {
		tagMap.put(key, new NBTTagByteArray(value));
	}

	/**
	 * Stores a new NBTTagDouble with the given double value into the map with the given string key.
	 */
	public void setDouble(String key, double value) {
		tagMap.put(key, new NBTTagDouble(value));
	}

	/**
	 * Stores a new NBTTagFloat with the given float value into the map with the given string key.
	 */
	public void setFloat(String key, float value) {
		tagMap.put(key, new NBTTagFloat(value));
	}

	/**
	 * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
	 */
	public void setIntArray(String key, int[] value) {
		tagMap.put(key, new NBTTagIntArray(value));
	}

	/**
	 * Stores a new NBTTagInt with the given integer value into the map with the given string key.
	 */
	public void setInteger(String key, int value) {
		tagMap.put(key, new NBTTagInt(value));
	}

	/**
	 * Stores a new NBTTagLong with the given long value into the map with the given string key.
	 */
	public void setLong(String key, long value) {
		tagMap.put(key, new NBTTagLong(value));
	}

	/**
	 * Stores a new NBTTagShort with the given short value into the map with the given string key.
	 */
	public void setShort(String key, short value) {
		tagMap.put(key, new NBTTagShort(value));
	}

	/**
	 * Stores a new NBTTagString with the given string value into the map with the given string key.
	 */
	public void setString(String key, String value) {
		tagMap.put(key, new NBTTagString(value));
	}

	/**
	 * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
	 */
	public void setTag(String key, NBTBase value) {
		tagMap.put(key, value);
	}

	@Override
	public String toString() {
		String var1 = "{";
		String var3;

		for (Iterator<String> var2 = tagMap.keySet().iterator(); var2.hasNext(); var1 = var1 + var3 + ':' + tagMap.get(var3) + ',') {
			var3 = var2.next();
		}

		return var1 + "}";
	}

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension classes
	 */
	@Override
	void write(DataOutput output) throws IOException {
		Iterator<String> var2 = tagMap.keySet().iterator();

		while (var2.hasNext()) {
			String var3 = var2.next();
			NBTBase var4 = tagMap.get(var3);
			writeEntry(var3, var4, output);
		}

		output.writeByte(0);
	}
}
