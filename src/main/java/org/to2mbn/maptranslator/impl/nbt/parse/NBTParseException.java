package org.to2mbn.maptranslator.impl.nbt.parse;

public class NBTParseException extends NBTException {

	private static final long serialVersionUID = 1L;

	public NBTParseException(String message, String inputString, int cursor) {
		super(message + " at: " + toPositionMessage(inputString, cursor));
	}

	private static String toPositionMessage(String inputString, int cursor) {
		StringBuilder sb = new StringBuilder();
		int i = Math.min(inputString.length(), cursor);

		if (i > 35) {
			sb.append("...");
		}

		sb.append(inputString.substring(Math.max(0, i - 35), i));
		sb.append("<--[HERE]");
		return sb.toString();
	}
}
