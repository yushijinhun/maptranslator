package yushijinhun.maptranslator.core;

import java.io.UncheckedIOException;
import java.util.Set;
import yushijinhun.maptranslator.nbt.NBTCompound;

public interface NBTDescriptor {

	NBTCompound read() throws UncheckedIOException;

	void write(NBTCompound nbt) throws UncheckedIOException;

	Set<String> getTags();
}
