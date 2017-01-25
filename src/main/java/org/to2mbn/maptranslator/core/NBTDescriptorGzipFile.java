package org.to2mbn.maptranslator.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.to2mbn.maptranslator.nbt.NBTCompound;
import org.to2mbn.maptranslator.nbt.NBTIO;

class NBTDescriptorGzipFile extends NBTDescriptorFile {

	public NBTDescriptorGzipFile(Path root, File file) {
		super(root, file);
	}

	@Override
	public NBTCompound readNBT() {
		try (FileInputStream in = new FileInputStream(file)) {
			return NBTIO.readCompressed(in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void writeNBT(NBTCompound nbt) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			NBTIO.writeCompressed(nbt, out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
