package yushijinhun.maptranslator.core;

import java.io.File;
import java.io.IOException;
import yushijinhun.maptranslator.nbt.NBTIO;
import yushijinhun.maptranslator.nbt.NBTCompound;

public class NBTDescriptorFile implements NBTDescriptor {

	private File file;

	public NBTDescriptorFile(File file) {
		this.file = file;
	}

	@Override
	public NBTCompound read() throws IOException {
		return NBTIO.read(file);
	}

	@Override
	public void write(NBTCompound nbt) throws IOException {
		NBTIO.write(nbt, file);
	}

	@Override
	public String toString() {
		return file.getPath();
	}
}
