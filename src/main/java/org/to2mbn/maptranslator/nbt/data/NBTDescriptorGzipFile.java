package org.to2mbn.maptranslator.nbt.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.to2mbn.maptranslator.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.nbt.parse.NBTIOUtils;

public class NBTDescriptorGzipFile extends NBTDescriptorFile {

	public NBTDescriptorGzipFile(Path root, File file) {
		super(root, file);
	}

	@Override
	public NBTCompound readNBT() {
		try (FileInputStream in = new FileInputStream(file)) {
			return NBTIOUtils.readCompressed(in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void writeNBT(NBTCompound nbt) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			NBTIOUtils.writeCompressed(nbt, out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
