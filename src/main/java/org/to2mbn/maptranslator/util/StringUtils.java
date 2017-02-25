package org.to2mbn.maptranslator.util;

public final class StringUtils {

	private StringUtils() {}

	public static boolean stringEquals(String a, String b) {
		if (a == b) return true;
		if (a == null) return false;
		if (a.hashCode() != b.hashCode()) return false;
		return a.equals(b);
	}

}
