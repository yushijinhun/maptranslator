package yushijinhun.maptranslator.core;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import yushijinhun.maptranslator.nbt.NBTIO;
import yushijinhun.maptranslator.nbt.NBTCompound;

class NBTDescriptorPlainFile extends NBTDescriptorFile {

	public NBTDescriptorPlainFile(Path root, File file) {
		super(root, file);
	}

	@Override
	public NBTCompound read() {
		try {
			return NBTIO.read(file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void write(NBTCompound nbt) {
		try {
			NBTIO.write(nbt, file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
