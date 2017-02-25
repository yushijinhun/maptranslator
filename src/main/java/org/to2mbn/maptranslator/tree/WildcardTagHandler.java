package org.to2mbn.maptranslator.tree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class WildcardTagHandler {

	private static Map<String, Map<String, Boolean>> cache = new ConcurrentHashMap<>();

	public static boolean matchesWildcardTag(String pattern, String str) {
		return cache
				.computeIfAbsent(pattern, dummy -> new ConcurrentHashMap<>())
				.computeIfAbsent(str, tag -> doMatchesWildcardTag(pattern, tag));
	}

	private static boolean doMatchesWildcardTag(String pattern, String str) {
		int segmentBegin = 0;
		int segmentEnd;
		int strIdx = 0;
		String segment;
		boolean match = true;
		int lenPattern = pattern.length();
		int appearanceIdx;
		do {
			segmentEnd = pattern.indexOf('*', segmentBegin);
			if (segmentEnd == -1) segmentEnd = lenPattern;
			segment = pattern.substring(segmentBegin, segmentEnd);
			if (segmentBegin == 0) {
				if (!str.startsWith(segment)) {
					match = false;
					break;
				}
				strIdx = segmentEnd;
			} else if (segmentEnd == lenPattern) {
				if (segment.length() + strIdx > str.length() || !str.endsWith(segment)) {
					match = false;
					break;
				}
			} else {
				appearanceIdx = str.indexOf(segment, strIdx);
				if (appearanceIdx == -1) {
					match = false;
					break;
				}
				strIdx = appearanceIdx + segment.length();
			}
			segmentBegin = segmentEnd + 1;
		} while (segmentEnd < lenPattern);
		return match;
	}

	public static boolean isWildcardTag(String pattern) {
		return pattern.indexOf('*') != -1;
	}

}
