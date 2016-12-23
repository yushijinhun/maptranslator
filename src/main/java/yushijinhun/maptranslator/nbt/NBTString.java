package yushijinhun.maptranslator.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class NBTString extends NBT {

	public static final byte ID = 8;

	private String data;

	protected NBTString() {}

	public NBTString(String data) {
		this.data = Objects.requireNonNull(data);
	}

	@Override
	public NBT clone() {
		return new NBTString(data);
	}

	@Override
	public boolean equals(Object another) {
		if (!super.equals(another)) {
			return false;
		}
		return Objects.equals(data, ((NBTString) another).getString());
	}

	@Override
	public byte getId() {
		return (byte) 8;
	}

	@Override
	public String getString() {
		return data;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ data.hashCode();
	}

	@Override
	protected void read(DataInput input) throws IOException {
		data = input.readUTF();
	}

	@Override
	public String toString() {
		return escapeString(data);
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeUTF(data);
	}

	@Override
	public Object getData() {
		return data;
	}

	protected static String escapeString(String str) {
		StringBuilder sb = new StringBuilder(str.length() + 2);
		sb.append('"');
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '“' || ch == '\\') {
				sb.append('\\');
			}
			sb.append(ch);
		}
		sb.append('"');
		return sb.toString();
	}
}
