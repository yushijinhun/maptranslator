package org.to2mbn.maptranslator.impl.nbt.parse;

public interface JsonNBTConverter {

	NBTCompound parse(String json) throws NBTException;

	static JsonNBTConverter instance() {
		return NBTVersion.getCurrentConfig().getInputVersion().parser;
	}

}
