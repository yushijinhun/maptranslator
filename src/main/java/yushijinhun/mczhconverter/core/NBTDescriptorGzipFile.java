package yushijinhun.mczhconverter.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import yushijinhun.mczhconverter.nbt.CompressedStreamTools;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;

public class NBTDescriptorGzipFile implements NBTDescriptor {

	private File file;

	public NBTDescriptorGzipFile(File file) {
		this.file = file;
	}

	@Override
	public NBTTagCompound read() throws IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			return CompressedStreamTools.readCompressed(in);
		}
	}

	@Override
	public void write(NBTTagCompound nbt) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file)) {
			CompressedStreamTools.writeCompressed(nbt, out);
		}
	}

	@Override
	public String toString() {
		return "GzipNBT[" + file.getPath() + "]";
	}

}
