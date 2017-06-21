package org.to2mbn.maptranslator.process;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TreeConstructorUtils {

	private TreeConstructorUtils() {}

	private static final boolean _TO_STRING_VERIFY = true;
	private static final Logger LOGGER = Logger.getLogger(TreeConstructorUtils.class.getCanonicalName());

	public static <T> T checkedParse(Function<String, T> parser, String input) {
		return checkedParse(parser, parser, input);
	}

	public static <T> T checkedParse(Function<String, T> parser, Function<String, T> parser2nd, String input) {
		T parsed = parser.apply(input);
		if (_TO_STRING_VERIFY) {
			try {
				String serialized = parsed.toString();
				if (!parsed.equals(parser2nd.apply(serialized))) {
					throw new IllegalStateException("Object mismatch, serialized: " + serialized);
				}
			} catch (Exception e) {
				String msg = "*** Parsing verify failed: " + input;
				LOGGER.log(Level.WARNING, msg, e);
				throw new ArgumentParseException(msg, e);
			}
		}
		return parsed;
	}

}
