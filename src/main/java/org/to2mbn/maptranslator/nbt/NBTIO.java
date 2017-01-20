package org.to2mbn.maptranslator.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class NBTIO {

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

	public static NBTCompound read(DataInputStream p_74794_0_) throws IOException {
		return readTag(p_74794_0_);
	}

	public static NBTCompound read(File file) throws IOException {
		if (!file.exists()) {
			return null;
		}
		try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
			return readTag(in);
		}
	}

	public static NBTCompound readCompressed(InputStream in) throws IOException {
		try (DataInputStream gzipin = new DataInputStream(new BufferedInputStream(new GZIPInputStream(in)))) {
			return readTag(gzipin);
		}
	}

	public static void safeWrite(NBTCompound nbt, File file) throws IOException {
		File tempFile = new File(file.getAbsolutePath() + "_tmp");

		if (tempFile.exists()) {
			tempFile.delete();
		}

		write(nbt, tempFile);

		if (file.exists()) {
			file.delete();
		}

		if (file.exists()) {
			throw new IOException("Failed to delete " + file);
		}
		tempFile.renameTo(file);
	}

	public static void write(NBTCompound nbt, DataOutput out) throws IOException {
		writeTag(nbt, out);
	}

	public static void write(NBTCompound nbt, File file) throws IOException {
		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
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

	private NBTIO() {}
}
