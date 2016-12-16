package yushijinhun.maptranslator.core;

import java.io.File;
import java.io.IOException;
import yushijinhun.maptranslator.nbt.CompressedStreamTools;
import yushijinhun.maptranslator.nbt.NBTTagCompound;

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
		return file.getPath();
	}
}
