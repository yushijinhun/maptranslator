package yushijinhun.maptranslator.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTString;

public class NBTStringProxy extends NBTString {

	public Supplier<String> handler;

	@Override
	public NBT clone() {
		return new NBTString(getString());
	}

	@Override
	public boolean equals(Object another) {
		if (!super.equals(another)) {
			return false;
		}
		return Objects.equals(getString(), ((NBTString) another).getString());
	}

	@Override
	public String getString() {
		return handler.get();
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ getString().hashCode();
	}

	@Override
	protected void read(DataInput input) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return escapeString(getString());
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeUTF(getString());
	}

}
