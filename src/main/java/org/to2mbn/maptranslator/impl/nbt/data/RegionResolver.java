package org.to2mbn.maptranslator.impl.nbt.data;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.to2mbn.maptranslator.data.DataDescriptorResolver;
import org.to2mbn.maptranslator.impl.nbt.parse.RegionFile;

public class RegionResolver implements DataDescriptorResolver {

	@Override
	public void process(Path root, Path file, ResolvingContext ctx) {
		if (file.getFileName().toString().endsWith(".mca")) {
			RegionFile region;
			try {
				region = new RegionFile(file.toFile());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			ctx.addManagedResource(region);
			for (int x = 0; x < 32; x++) {
				for (int z = 0; z < 32; z++) {
					if (region.isChunkSaved(x, z)) {
						ctx.addDataDescriptor(new NBTDescriptorChunk(root, region, x, z));
					}
				}
			}
		}
	}

}
