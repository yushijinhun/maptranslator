package org.to2mbn.maptranslator.process;

public class ArgumentParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ArgumentParseException() {}

	public ArgumentParseException(String message) {
		super(message);
	}

	public ArgumentParseException(Throwable cause) {
		super(cause);
	}

	public ArgumentParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
