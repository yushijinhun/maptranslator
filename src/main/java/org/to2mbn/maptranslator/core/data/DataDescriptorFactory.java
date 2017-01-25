package org.to2mbn.maptranslator.core.data;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import org.to2mbn.maptranslator.nbt.data.NBTDescriptorChunk;
import org.to2mbn.maptranslator.nbt.data.NBTDescriptorGzipFile;
import org.to2mbn.maptranslator.nbt.data.NBTDescriptorPlainFile;
import org.to2mbn.maptranslator.nbt.parse.RegionFile;

public final class DataDescriptorFactory {

	private static Logger logger = Logger.getLogger(DataDescriptorFactory.class.getCanonicalName());

	public static DataDescriptorGroup getDescriptors(File file) {
		Set<DataDescriptor> descriptors = new ConcurrentSkipListSet<>(Comparator.comparing(obj -> obj.toString()));
		Set<Closeable> closeables = Collections.newSetFromMap(new ConcurrentHashMap<>());
		Path root = file.toPath();
		List<Path> files;
		try {
			files = Files.walk(root).collect(Collectors.toList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		files.parallelStream().map(path -> path.toFile()).forEach(f -> getDescriptorsFromFile(root, f, descriptors, closeables));
		return new DataDescriptorGroup(descriptors, closeables);
	}

	private static void getDescriptorsFromFile(Path root, File file, Set<DataDescriptor> result, Set<Closeable> closeables) {
		if (file.getName().endsWith(".mca")) {
			getDescriptorsFromMcaFile(root, file, result, closeables);
		} else if (file.getName().endsWith(".dat") || file.getName().endsWith(".nbt")) {
			getDescriptorsFromNBTFile(root, file, result);
		}
	}

	private static void getDescriptorsFromMcaFile(Path root, File file, Set<DataDescriptor> result, Set<Closeable> closeables) {
		RegionFile region;
		try {
			region = new RegionFile(file);
		} catch (IOException e) {
			logger.log(Level.WARNING, String.format("Failed to open mca file: %s ,skipped", file.getPath()), e);
			return;
		}
		closeables.add(region);
		for (int x = 0; x < 32; x++) {
			for (int z = 0; z < 32; z++) {
				if (region.isChunkSaved(x, z)) {
					result.add(new SyncDataDescriptor(new NBTDescriptorChunk(root, region, x, z)));
				}
			}
		}
	}

	private static void getDescriptorsFromNBTFile(Path root, File file, Set<DataDescriptor> result) {
		if (isInGzip(file)) {
			result.add(new SyncDataDescriptor(new NBTDescriptorGzipFile(root, file)));
		} else {
			result.add(new SyncDataDescriptor(new NBTDescriptorPlainFile(root, file)));
		}
	}

	private static boolean isInGzip(File file) {
		try (InputStream in = new FileInputStream(file)) {
			int b1 = in.read();
			int b2 = in.read();
			return ((b2 << 8) | b1) == GZIPInputStream.GZIP_MAGIC;
		} catch (IOException e) {
			return false;
		}
	}

	private DataDescriptorFactory() {
	}
}
