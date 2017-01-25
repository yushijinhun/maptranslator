package org.to2mbn.maptranslator.impl.nbt.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.to2mbn.maptranslator.data.DataDescriptorResolver;

public class NBTFileResolver implements DataDescriptorResolver {

	@Override
	public void process(Path root, Path file, ResolvingContext ctx) {
		String name = file.getFileName().toString();
		if (name.endsWith(".dat") || name.endsWith(".nbt")) {
			if (isInGzip(file)) {
				ctx.addDataDescriptor(new NBTDescriptorGzipFile(root, file));
			} else {
				ctx.addDataDescriptor(new NBTDescriptorPlainFile(root, file));
			}
		}
	}

	private static boolean isInGzip(Path file) {
		try (InputStream in = Files.newInputStream(file)) {
			int b1 = in.read();
			int b2 = in.read();
			return ((b2 << 8) | b1) == GZIPInputStream.GZIP_MAGIC;
		} catch (IOException e) {
			return false;
		}
	}

}
