package org.to2mbn.maptranslator.impl.nbt.tree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import org.to2mbn.maptranslator.impl.nbt.parse.NBT;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTString;

public class NBTStringProxy extends NBTString {

	public Supplier<String> handler;

	@Override
	public NBT clone() {
		return new NBTString(get());
	}

	@Override
	public boolean equals(Object another) {
		if (!super.equals(another)) {
			return false;
		}
		return Objects.equals(get(), ((NBTString) another).get());
	}

	@Override
	public String get() {
		return handler.get();
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ get().hashCode();
	}

	@Override
	protected void read(DataInput input) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return escapeString(get());
	}

	@Override
	protected void write(DataOutput output) throws IOException {
		output.writeUTF(get());
	}

}
