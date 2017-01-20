package org.to2mbn.maptranslator.core;

import java.io.UncheckedIOException;
import java.util.Set;
import org.to2mbn.maptranslator.nbt.NBTCompound;

public interface NBTDescriptor {

	NBTCompound read() throws UncheckedIOException;

	void write(NBTCompound nbt) throws UncheckedIOException;

	Set<String> getTags();
}
