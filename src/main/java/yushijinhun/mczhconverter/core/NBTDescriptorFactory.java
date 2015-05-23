package yushijinhun.mczhconverter.core;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import yushijinhun.mczhconverter.nbt.RegionFile;

public final class NBTDescriptorFactory {

	private static Logger logger = Logger.getLogger(NBTDescriptorFactory.class.getCanonicalName());

	public static NBTDescriptorSet createDescriptorSet(File file, int threads) {
		Set<NBTDescriptor> descriptors = new LinkedHashSet<>();
		Set<Closeable> closeables = new LinkedHashSet<>();
		getDescriptors(file, descriptors, closeables);
		return new NBTDescriptorSet(Executors.newFixedThreadPool(threads), descriptors, closeables);
	}

	private static void getDescriptors(File file, Set<NBTDescriptor> result, Set<Closeable> closeables) {
		if (file.isFile()) {
			getDescriptorsFromFile(file, result, closeables);
		} else if (file.isDirectory()) {
			for (String child : file.list()) {
				getDescriptors(new File(file, child), result, closeables);
			}
		}
	}

	private static void getDescriptorsFromFile(File file, Set<NBTDescriptor> result, Set<Closeable> closeables) {
		if (file.getName().endsWith(".mca")) {
			getDescriptorsFromMcaFile(file, result, closeables);
		} else if (file.getName().endsWith(".dat")) {
			getDescriptorsFromNBTFile(file, result);
		}
	}

	private static void getDescriptorsFromMcaFile(File file, Set<NBTDescriptor> result, Set<Closeable> closeables) {
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
					result.add(new NBTDescriptorChunk(region, x, z));
				}
			}
		}
	}

	private static void getDescriptorsFromNBTFile(File file, Set<NBTDescriptor> result) {
		if (isInGzip(file)) {
			result.add(new NBTDescriptorGzipFile(file));
		} else {
			result.add(new NBTDescriptorFile(file));
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

	private NBTDescriptorFactory() {
	}
}
