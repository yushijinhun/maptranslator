package org.to2mbn.maptranslator.impl.nbt.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.to2mbn.maptranslator.impl.nbt.parse.NBT.NBTPrimitive;

class JsonNBTConverter_MC_1_12 {

	static final JsonNBTConverter INSTANCE = JsonNBTConverter_MC_1_12::parse;

	private static final Pattern PATTERN_DOUBLE_1 = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
	private static final Pattern PATTERN_DOUBLE_2 = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
	private static final Pattern PATTERN_FLOAT = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
	private static final Pattern PATTERN_BYTE = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
	private static final Pattern PATTERN_LONG = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
	private static final Pattern PATTERN_SHORT = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
	private static final Pattern PATTERN_INT = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");

	private static NBTCompound parse(String jsonString) throws NBTException {
		return (new JsonNBTConverter_MC_1_12(jsonString)).parse();
	}

	private String inputString;
	private int cursor;

	private JsonNBTConverter_MC_1_12(String inputString) {
		this.inputString = inputString;
	}

	private NBTCompound parse() throws NBTException {
		NBTCompound tag = nextCompound();
		skipWhitespace();

		if (inBounds()) {
			cursor++;
			throw newParseException("Trailing data found");
		} else {
			return tag;
		}
	}

	private NBTException newParseException(String message) {
		return new NBTParseException(message, inputString, cursor);
	}

	private String nextKey() throws NBTException {
		skipWhitespace();

		if (!inBounds()) {
			throw newParseException("Expected key");
		} else {
			return current() == '"' ? nextQuotedString() : nextUnquotedString();
		}
	}

	private NBT nextSimpleValue() throws NBTException {
		skipWhitespace();

		if (current() == '"') {
			return new NBTString(nextQuotedString());
		} else {
			String value = nextUnquotedString();

			if (value.isEmpty()) {
				throw newParseException("Expected value");

			} else {
				try {
					if (PATTERN_FLOAT.matcher(value).matches())
						return new NBTFloat(Float.parseFloat(value.substring(0, value.length() - 1)));
					if (PATTERN_BYTE.matcher(value).matches())
						return new NBTByte(Byte.parseByte(value.substring(0, value.length() - 1)));
					if (PATTERN_LONG.matcher(value).matches())
						return new NBTLong(Long.parseLong(value.substring(0, value.length() - 1)));
					if (PATTERN_SHORT.matcher(value).matches())
						return new NBTShort(Short.parseShort(value.substring(0, value.length() - 1)));
					if (PATTERN_INT.matcher(value).matches())
						return new NBTInt(Integer.parseInt(value));
					if (PATTERN_DOUBLE_2.matcher(value).matches())
						return new NBTDouble(Double.parseDouble(value.substring(0, value.length() - 1)));
					if (PATTERN_DOUBLE_1.matcher(value).matches())
						return new NBTDouble(Double.parseDouble(value));
					if ("true".equalsIgnoreCase(value))
						return new NBTByte((byte) 1);
					if ("false".equalsIgnoreCase(value))
						return new NBTByte((byte) 0);
				} catch (NumberFormatException e) {
					;
				}
				return new NBTString(value);
			}
		}
	}

	private String nextQuotedString() throws NBTException {
		int beginPos = ++cursor;
		StringBuilder sb = null;
		boolean escaping = false;

		while (inBounds()) {
			char ch = next();

			if (escaping) {
				if (ch != '\\' && ch != '"')
					throw newParseException("Invalid escape of '" + ch + "'");

				escaping = false;
			} else {
				if (ch == '\\') {
					escaping = true;

					if (sb == null)
						sb = new StringBuilder(inputString.substring(beginPos, cursor - 1));

					continue;
				}

				if (ch == '"')
					return sb == null ? inputString.substring(beginPos, cursor - 1) : sb.toString();
			}

			if (sb != null)
				sb.append(ch);
		}

		throw newParseException("Missing termination quote");
	}

	private String nextUnquotedString() {
		int beginPos = cursor;
		while (inBounds() && isValidChar(current()))
			cursor++;
		return inputString.substring(beginPos, cursor);
	}

	private NBT nextValue() throws NBTException {
		skipWhitespace();

		if (!inBounds()) {
			throw newParseException("Expected value");
		} else {
			char ch = current();

			if (ch == '{') {
				return nextCompound();
			} else {
				return ch == '[' ? nextArrayOrList() : nextSimpleValue();
			}
		}
	}

	private NBT nextArrayOrList() throws NBTException {
		return offsetInBounds(2) && charAtOffset(1) != '"' && charAtOffset(2) == ';' ? nextArray() : nextList();
	}

	private NBTCompound nextCompound() throws NBTException {
		skipUntil('{');
		NBTCompound tag = new NBTCompound();
		skipWhitespace();

		while (inBounds() && current() != '}') {
			String key = nextKey();

			if (key.isEmpty())
				throw newParseException("Expected non-empty key");

			skipUntil(':');
			tag.put(key, nextValue());

			if (!nextComma())
				break;

			if (!inBounds())
				throw newParseException("Expected key");
		}

		skipUntil('}');
		return tag;
	}

	private NBT nextList() throws NBTException {
		skipUntil('[');
		skipWhitespace();

		if (!inBounds()) {
			throw newParseException("Expected value");
		} else {
			NBTList nbttaglist = new NBTList();
			int listType = -1;

			while (current() != ']') {
				NBT element = nextValue();
				int elementType = element.getId();

				if (listType < 0) {
					listType = elementType;
				} else if (elementType != listType) {
					throw newParseException("Unable to insert " + NBT.NBT_TYPES[elementType] + " into ListTag of type " + NBT.NBT_TYPES[listType]);
				}

				nbttaglist.add(element);

				if (!nextComma())
					break;

				if (!inBounds())
					throw newParseException("Expected value");
			}

			skipUntil(']');
			return nbttaglist;
		}
	}

	private NBT nextArray() throws NBTException {
		skipUntil('[');
		char ch = next();
		next();
		skipWhitespace();

		if (!inBounds()) {
			throw newParseException("Expected value");
		} else if (ch == 'B') {
			return new NBTByteArray(nextArrayNBT(NBTByteArray.ID, Byte.class));
		} else if (ch == 'L') {
			return new NBTLongArray(nextArrayNBT(NBTLongArray.ID, Long.class));
		} else if (ch == 'I') {
			return new NBTIntArray(nextArrayNBT(NBTIntArray.ID, Integer.class));
		} else {
			throw newParseException("Invalid array type '" + ch + "' found");
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends Number> List<T> nextArrayNBT(byte arrayId, Class<T> elementType) throws NBTException {
		byte elementId;
		if (elementType == Byte.class) {
			elementId = NBTByte.ID;
		} else if (elementType == Long.class) {
			elementId = NBTLong.ID;
		} else if (elementType == Integer.class) {
			elementId = NBTInt.ID;
		} else {
			throw new IllegalArgumentException("Unexpected type: " + elementType);
		}

		List<T> list = new ArrayList<>();

		while (true) {
			if (current() != ']') {
				NBT nbtbase = nextValue();
				int tagId = nbtbase.getId();

				if (tagId != elementId)
					throw newParseException("Unable to insert " + NBT.NBT_TYPES[tagId] + " into " + NBT.NBT_TYPES[arrayId]);

				list.add(((NBTPrimitive<T>) nbtbase).get());

				if (nextComma()) {
					if (!inBounds())
						throw newParseException("Expected value");

					continue;
				}
			}

			skipUntil(']');
			return list;
		}
	}

	private void skipWhitespace() {
		while (inBounds() && Character.isWhitespace(current()))
			cursor++;
	}

	private boolean nextComma() {
		skipWhitespace();

		if (inBounds() && current() == ',') {
			cursor++;
			skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	private void skipUntil(char ch) throws NBTException {
		skipWhitespace();
		boolean withinBounds = inBounds();

		if (withinBounds && current() == ch) {
			cursor++;
		} else {
			throw new NBTParseException("Expected '" + ch + "' but got '" + (withinBounds ? current() : "<EOF>") + "'", inputString, cursor + 1);
		}
	}

	private boolean isValidChar(char ch) {
		return ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch == '_' || ch == '-' || ch == '.' || ch == '+';
	}

	private boolean offsetInBounds(int n) {
		return cursor + n < inputString.length();
	}

	private boolean inBounds() {
		return offsetInBounds(0);
	}

	private char charAtOffset(int n) {
		return inputString.charAt(cursor + n);
	}

	private char current() {
		return charAtOffset(0);
	}

	private char next() {
		return inputString.charAt(cursor++);
	}
}
