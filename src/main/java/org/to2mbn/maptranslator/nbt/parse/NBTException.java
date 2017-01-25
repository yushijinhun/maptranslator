package org.to2mbn.maptranslator.nbt.parse;

import org.to2mbn.maptranslator.core.process.ArgumentParseException;

public class NBTException extends ArgumentParseException {

	private static final long serialVersionUID = 1L;

	public NBTException(String message) {
		super(message);
	}
}
