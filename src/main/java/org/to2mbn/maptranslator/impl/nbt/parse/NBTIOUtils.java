package org.to2mbn.maptranslator.impl.nbt.parse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class NBTIOUtils {

	private static NBT readRawTag(DataInput in) throws IOException {
		byte type = in.readByte();

		if (type == 0) {
			return new NBTEnd();
		}
		in.readUTF();
		NBT nbt = NBT.createNewByType(type);

		nbt.read(in);
		return nbt;
	}

	public static NBTCompound readTag(DataInput in) throws IOException {
		NBT nbt = readRawTag(in);

		if (nbt instanceof NBTCompound) {
			return (NBTCompound) nbt;
		}
		throw new IOException("Root tag must be a named compound tag");
	}

	public static NBTCompound read(DataInputStream in) throws IOException {
		return readTag(in);
	}

	public static NBTCompound read(Path file) throws IOException {
		if (!Files.exists(file)) {
			return null;
		}
		try (DataInputStream in = new DataInputStream(Files.newInputStream(file))) {
			return readTag(in);
		}
	}

	public static NBTCompound readCompressed(InputStream in) throws IOException {
		try (DataInputStream gzipin = new DataInputStream(new BufferedInputStream(new GZIPInputStream(in)))) {
			return readTag(gzipin);
		}
	}

	public static void safeWrite(NBTCompound nbt, Path file) throws IOException {
		Path tempFile = file.getParent().resolve(file.getFileName().toString() + "_tmp");
		Files.deleteIfExists(tempFile);
		write(nbt, tempFile);
		Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
	}

	public static void write(NBTCompound nbt, DataOutput out) throws IOException {
		writeTag(nbt, out);
	}

	public static void write(NBTCompound nbt, Path file) throws IOException {
		try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
			write(nbt, out);
		}
	}

	public static void writeCompressed(NBTCompound nbt, OutputStream out) throws IOException {
		try (DataOutputStream gzipout = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(out)))) {
			write(nbt, gzipout);
		}
	}

	private static void writeTag(NBT nbt, DataOutput out) throws IOException {
		out.writeByte(nbt.getId());

		if (nbt.getId() != 0) {
			out.writeUTF("");
			nbt.write(out);
		}
	}

	private NBTIOUtils() {}
}
