package org.to2mbn.maptranslator.nbt.data;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.to2mbn.maptranslator.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.nbt.parse.NBTIOUtils;

public class NBTDescriptorPlainFile extends NBTDescriptorFile {

	public NBTDescriptorPlainFile(Path root, File file) {
		super(root, file);
	}

	@Override
	public NBTCompound readNBT() {
		try {
			return NBTIOUtils.read(file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void writeNBT(NBTCompound nbt) {
		try {
			NBTIOUtils.write(nbt, file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
