package yushijinhun.maptranslator.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import yushijinhun.maptranslator.nbt.NBTIO;
import yushijinhun.maptranslator.nbt.NBTCompound;
import yushijinhun.maptranslator.nbt.RegionFile;

public class NBTDescriptorChunk implements NBTDescriptor {

	private RegionFile file;
	private int x;
	private int y;

	public NBTDescriptorChunk(RegionFile file, int x, int y) {
		this.file = file;
		this.x = x;
		this.y = y;
	}

	@Override
	public NBTCompound read() throws IOException {
		try (DataInputStream in = file.getChunkDataInputStream(x, y)) {
			return NBTIO.read(in);
		}
	}

	@Override
	public void write(NBTCompound nbt) throws IOException {
		try (DataOutputStream out = file.getChunkDataOutputStream(x, y)) {
			NBTIO.write(nbt, out);
		}
	}

	@Override
	public String toString() {
		return file.getFile().getPath() + "/chunk[" + x + "," + y + "]";
	}
}
