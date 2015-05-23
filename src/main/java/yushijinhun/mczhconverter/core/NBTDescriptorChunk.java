package yushijinhun.mczhconverter.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import yushijinhun.mczhconverter.nbt.CompressedStreamTools;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;
import yushijinhun.mczhconverter.nbt.RegionFile;

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
	public NBTTagCompound read() throws IOException {
		try (DataInputStream in = file.getChunkDataInputStream(x, y)) {
			return CompressedStreamTools.read(in);
		}
	}

	@Override
	public void write(NBTTagCompound nbt) throws IOException {
		try (DataOutputStream out = file.getChunkDataOutputStream(x, y)) {
			CompressedStreamTools.write(nbt, out);
		}
	}

	@Override
	public String toString() {
		return "Chunk[" + x + "," + y + "][" + file.getFile().getPath() + "]";
	}
}
