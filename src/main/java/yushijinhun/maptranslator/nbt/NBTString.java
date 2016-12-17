package yushijinhun.maptranslator.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class NBTString extends NBT {

	private String data;

	NBTString() {}

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
		return Objects.equals(data, ((NBTString) another).data);
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
	void read(DataInput input) throws IOException {
		data = input.readUTF();
	}

	@Override
	public String toString() {
		return "\"" + data.replace("\"", "\\\"") + "\"";
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeUTF(data);
	}
}
