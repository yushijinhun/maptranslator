package yushijinhun.mczhconverter.core;

import java.io.File;
import java.io.IOException;
import yushijinhun.mczhconverter.nbt.CompressedStreamTools;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;

public class NBTDescriptorFile implements NBTDescriptor {

	private File file;

	public NBTDescriptorFile(File file) {
		this.file = file;
	}

	@Override
	public NBTTagCompound read() throws IOException {
		return CompressedStreamTools.read(file);
	}

	@Override
	public void write(NBTTagCompound nbt) throws IOException {
		CompressedStreamTools.write(nbt, file);
	}

	@Override
	public String toString() {
		return "NBT[" + file.getPath() + "]";
	}
}
