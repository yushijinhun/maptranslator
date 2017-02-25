package org.to2mbn.maptranslator.impl.nbt.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTIOUtils;

public class NBTDescriptorGzipFile extends NBTDescriptorFile {

	public NBTDescriptorGzipFile(Path root, Path file) {
		super(root, file);
	}

	@Override
	public NBTCompound readNBT() {
		try (InputStream in = new BufferedInputStream(Files.newInputStream(file))) {
			return NBTIOUtils.readCompressed(in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void writeNBT(NBTCompound nbt) {
		try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file))) {
			NBTIOUtils.writeCompressed(nbt, out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
