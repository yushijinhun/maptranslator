package org.to2mbn.maptranslator.impl.nbt.parse;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.to2mbn.maptranslator.impl.nbt.parse.NBT.NBTPrimitive;

public class JsonNBTConverter {

	private static final Pattern INT_ARRAY_MATCHER = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

	public static NBTCompound getTagFromJson(String json) throws NBTException {
		json = json.trim();
		if (!json.startsWith("{")) {
			throw new NBTException("Invalid tag encountered, expected \'{\' as first char.");
		} else if (countTopTags(json) != 1) {
			throw new NBTException("Encountered multiple top tags, only one expected");
		} else {
			return (NBTCompound) nameValueToNBT("tag", json).parse();
		}
	}

	static int countTopTags(String str) throws NBTException {
		int count = 0;
		boolean escaped = false;
		Stack<Character> stack = new Stack<>();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);

			if (ch == '"') {
				if (isCharEscaped(str, i)) {
					if (!escaped) {
						throw new NBTException("Illegal use of \\\": " + str);
					}
				} else {
					escaped = !escaped;
				}
			} else if (!escaped) {
				if (ch != '{' && ch != '[') {
					if (ch == '}' && (stack.isEmpty() || stack.pop().charValue() != '{')) {
						throw new NBTException("Unbalanced curly brackets {}: " + str);
					}

					if (ch == ']' && (stack.isEmpty() || stack.pop().charValue() != '[')) {
						throw new NBTException("Unbalanced square brackets []: " + str);
					}
				} else {
					if (stack.isEmpty()) {
						count++;
					}

					stack.push(Character.valueOf(ch));
				}
			}
		}

		if (escaped) {
			throw new NBTException("Unbalanced quotation: " + str);
		} else if (!stack.isEmpty()) {
			throw new NBTException("Unbalanced brackets: " + str);
		} else {
			if (count == 0 && !str.isEmpty()) {
				count = 1;
			}

			return count;
		}
	}

	static JsonNBTConverter.Any nameValueToNBT(String key, String value) throws NBTException {
		value = value.trim();

		if (value.startsWith("{")) {
			value = value.substring(1, value.length() - 1);
			JsonNBTConverter.Compound compound;
			String s1;

			for (compound = new JsonNBTConverter.Compound(key); value.length() > 0; value = value.substring(s1.length() + 1)) {
				s1 = nextNameValuePair(value, true);

				if (s1.length() > 0) {
					compound.tagList.add(getTagFromNameValue(s1, false));
				}

				if (value.length() < s1.length() + 1) {
					break;
				}

				char c1 = value.charAt(s1.length());

				if (c1 != ',' && c1 != '{' && c1 != '}' && c1 != '[' && c1 != ']') {
					throw new NBTException("Unexpected token \'" + c1 + "\' at: " + value.substring(s1.length()));
				}
			}

			return compound;
		} else if (value.startsWith("[") && !INT_ARRAY_MATCHER.matcher(value).matches()) {
			value = value.substring(1, value.length() - 1);
			JsonNBTConverter.List list;
			String s;

			for (list = new JsonNBTConverter.List(key); value.length() > 0; value = value.substring(s.length() + 1)) {
				s = nextNameValuePair(value, false);

				if (s.length() > 0) {
					list.tagList.add(getTagFromNameValue(s, true));
				}

				if (value.length() < s.length() + 1) {
					break;
				}

				char c0 = value.charAt(s.length());

				if (c0 != ',' && c0 != '{' && c0 != '}' && c0 != '[' && c0 != ']') {
					throw new NBTException("Unexpected token \'" + c0 + "\' at: " + value.substring(s.length()));
				}
			}

			return list;
		} else {
			return new JsonNBTConverter.Primitive(key, value);
		}
	}

	private static JsonNBTConverter.Any getTagFromNameValue(String str, boolean isArray) throws NBTException {
		String key = locateKey(str, isArray);
		String value = locateValue(str, isArray);
		return nameValueToNBT(key, value);
	}

	private static String nextNameValuePair(String str, boolean isCompound) throws NBTException {
		int i = getNextCharIndex(str, ':');
		int j = getNextCharIndex(str, ',');

		if (isCompound) {
			if (i == -1) {
				throw new NBTException("Unable to locate name/value separator for string: " + str);
			}

			if (j != -1 && j < i) {
				throw new NBTException("Name error at: " + str);
			}
		} else if (i == -1 || i > j) {
			i = -1;
		}

		return locateValueAt(str, i);
	}

	private static String locateValueAt(String str, int index) throws NBTException {
		Stack<Character> stack = new Stack<>();
		int i = index + 1;
		boolean escaped = false;
		boolean flag1 = false;
		boolean flag2 = false;

		for (int j = 0; i < str.length(); ++i) {
			char c0 = str.charAt(i);

			if (c0 == '"') {
				if (isCharEscaped(str, i)) {
					if (!escaped) {
						throw new NBTException("Illegal use of \\\": " + str);
					}
				} else {
					escaped = !escaped;

					if (escaped && !flag2) {
						flag1 = true;
					}

					if (!escaped) {
						j = i;
					}
				}
			} else if (!escaped) {
				if (c0 != '{' && c0 != '[') {
					if (c0 == '}' && (stack.isEmpty() || stack.pop().charValue() != '{')) {
						throw new NBTException("Unbalanced curly brackets {}: " + str);
					}

					if (c0 == ']' && (stack.isEmpty() || stack.pop().charValue() != '[')) {
						throw new NBTException("Unbalanced square brackets []: " + str);
					}

					if (c0 == ',' && stack.isEmpty()) {
						return str.substring(0, i);
					}
				} else {
					stack.push(Character.valueOf(c0));
				}
			}

			if (!Character.isWhitespace(c0)) {
				if (!escaped && flag1 && j != i) {
					return str.substring(0, j + 1);
				}

				flag2 = true;
			}
		}

		return str.substring(0, i);
	}

	private static String locateKey(String str, boolean isArray) throws NBTException {
		if (isArray) {
			str = str.trim();

			if (str.startsWith("{") || str.startsWith("[")) {
				return "";
			}
		}

		int i = getNextCharIndex(str, ':');

		if (i == -1) {
			if (isArray) {
				return "";
			} else {
				throw new NBTException("Unable to locate name/value separator for string: " + str);
			}
		} else {
			return str.substring(0, i).trim();
		}
	}

	private static String locateValue(String str, boolean isArray) throws NBTException {
		if (isArray) {
			str = str.trim();

			if (str.startsWith("{") || str.startsWith("[")) {
				return str;
			}
		}

		int i = getNextCharIndex(str, ':');

		if (i == -1) {
			if (isArray) {
				return str;
			} else {
				throw new NBTException("Unable to locate name/value separator for string: " + str);
			}
		} else {
			return str.substring(i + 1).trim();
		}
	}

	private static int getNextCharIndex(String str, char targetChar) {
		int i = 0;

		for (boolean flag = true; i < str.length(); ++i) {
			char c0 = str.charAt(i);

			if (c0 == '"') {
				if (!isCharEscaped(str, i)) {
					flag = !flag;
				}
			} else if (flag) {
				if (c0 == targetChar) {
					return i;
				}

				if (c0 == '{' || c0 == '[') {
					return -1;
				}
			}
		}

		return -1;
	}

	private static boolean isCharEscaped(String str, int index) {
		return index > 0 && str.charAt(index - 1) == '\\' && !isCharEscaped(str, index - 1);
	}

	abstract static class Any {

		protected String json;

		public abstract NBT parse() throws NBTException;
	}

	static class Compound extends JsonNBTConverter.Any {

		protected java.util.List<JsonNBTConverter.Any> tagList = new ArrayList<>();

		public Compound(String jsonIn) {
			this.json = jsonIn;
		}

		@Override
		public NBT parse() throws NBTException {
			NBTCompound nbttagcompound = new NBTCompound();

			for (JsonNBTConverter.Any jsontonbt$any : this.tagList) {
				nbttagcompound.put(jsontonbt$any.json, jsontonbt$any.parse());
			}

			return nbttagcompound;
		}
	}

	static class List extends JsonNBTConverter.Any {

		protected java.util.List<JsonNBTConverter.Any> tagList = new ArrayList<>();

		public List(String json) {
			this.json = json;
		}

		@Override
		public NBT parse() throws NBTException {
			NBTList nbttaglist = new NBTList();

			int i = 0;
			for (JsonNBTConverter.Any child : this.tagList) {
				NBT parsed = child.parse();
				try {
					nbttaglist.add(parsed);
				} catch (IllegalArgumentException e) {
					// maybe the nbt's id doesn't match the list's
					throw new NBTException("type of " + parsed + " is expected to be " + NBT.NBT_TYPES[nbttaglist.getTagType()] + ", but " + NBT.NBT_TYPES[parsed.getId()]);
				}
				if (child.json.isEmpty()) {
					nbttaglist._noIndex.add(i);
				}
				i++;
			}

			return nbttaglist;
		}
	}

	static class Primitive extends JsonNBTConverter.Any {

		private static final Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
		private static final Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
		private static final Pattern BYTE = Pattern.compile("[-+]?[0-9]+[b|B]");
		private static final Pattern LONG = Pattern.compile("[-+]?[0-9]+[l|L]");
		private static final Pattern SHORT = Pattern.compile("[-+]?[0-9]+[s|S]");
		private static final Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");
		private static final Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");

		protected String jsonValue;

		public Primitive(String jsonIn, String valueIn) {
			this.json = jsonIn;
			this.jsonValue = valueIn;
		}

		@Override
		public NBT parse() throws NBTException {
			try {
				NBTPrimitive primitive = null;
				if (DOUBLE.matcher(this.jsonValue).matches()) {
					primitive = new NBTDouble(Double.parseDouble(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
				} else if (FLOAT.matcher(this.jsonValue).matches()) {
					primitive = new NBTFloat(Float.parseFloat(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
				} else if (BYTE.matcher(this.jsonValue).matches()) {
					primitive = new NBTByte(Byte.parseByte(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
				} else if (LONG.matcher(this.jsonValue).matches()) {
					primitive = new NBTLong(Long.parseLong(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
				} else if (SHORT.matcher(this.jsonValue).matches()) {
					primitive = new NBTShort(Short.parseShort(this.jsonValue.substring(0, this.jsonValue.length() - 1)));
				} else if (INTEGER.matcher(this.jsonValue).matches()) {
					primitive = new NBTInt(Integer.parseInt(this.jsonValue));
				} else if (DOUBLE_UNTYPED.matcher(this.jsonValue).matches()) {
					primitive = new NBTDouble(Double.parseDouble(this.jsonValue));
				}
				if (primitive != null) {
					primitive._str = jsonValue;
					return primitive;
				}

				if ("true".equalsIgnoreCase(this.jsonValue) || "false".equalsIgnoreCase(this.jsonValue)) {
					NBTByte tagByte = new NBTByte((byte) (Boolean.parseBoolean(this.jsonValue) ? 1 : 0));
					tagByte._str = jsonValue;
					return tagByte;
				}
			} catch (NumberFormatException var6) {
				this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
				NBTString tag = new NBTString(this.jsonValue);
				return tag;
			}

			if (this.jsonValue.startsWith("[") && this.jsonValue.endsWith("]")) {
				String s = this.jsonValue.substring(1, this.jsonValue.length() - 1);
				String[] astring = Stream.of(s.split(",")).filter(ele -> !ele.isEmpty()).toArray(String[]::new);

				try {
					int[] aint = new int[astring.length];

					for (int j = 0; j < astring.length; ++j) {
						aint[j] = Integer.parseInt(astring[j].trim());
					}

					return new NBTIntArray(aint);
				} catch (NumberFormatException var5) {
					NBTString tag = new NBTString(this.jsonValue);
					return tag;
				}
			} else {
				if (this.jsonValue.startsWith("\"") && this.jsonValue.endsWith("\"")) {
					this.jsonValue = this.jsonValue.substring(1, this.jsonValue.length() - 1);
				}

				this.jsonValue = this.jsonValue.replaceAll("\\\\\"", "\"");
				StringBuilder stringbuilder = new StringBuilder();

				for (int i = 0; i < this.jsonValue.length(); ++i) {
					if (i < this.jsonValue.length() - 1 && this.jsonValue.charAt(i) == '\\' && this.jsonValue.charAt(i + 1) == '\\') {
						stringbuilder.append('\\');
						++i;
					} else {
						stringbuilder.append(this.jsonValue.charAt(i));
					}
				}

				NBTString tag = new NBTString(stringbuilder.toString());
				return tag;
			}
		}
	}
}
