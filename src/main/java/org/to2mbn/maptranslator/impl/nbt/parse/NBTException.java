package org.to2mbn.maptranslator.impl.nbt.parse;

import org.to2mbn.maptranslator.process.ArgumentParseException;

public class NBTException extends ArgumentParseException {

	private static final long serialVersionUID = 1L;

	public NBTException() {}

	public NBTException(String message, Throwable cause) {
		super(message, cause);
	}

	public NBTException(String message) {
		super(message);
	}

	public NBTException(Throwable cause) {
		super(cause);
	}

}
