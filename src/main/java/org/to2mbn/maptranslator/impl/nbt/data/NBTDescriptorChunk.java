package org.to2mbn.maptranslator.impl.nbt.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTIOUtils;
import org.to2mbn.maptranslator.impl.nbt.parse.RegionFile;

public class NBTDescriptorChunk implements NBTDescriptor {

	private Path root;
	private RegionFile file;
	private int x;
	private int y;

	public NBTDescriptorChunk(Path root, RegionFile file, int x, int y) {
		this.root = root;
		this.file = file;
		this.x = x;
		this.y = y;
	}

	@Override
	public NBTCompound readNBT() {
		try (DataInputStream in = file.getChunkDataInputStream(x, y)) {
			return NBTIOUtils.read(in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void writeNBT(NBTCompound nbt) {
		try (DataOutputStream out = file.getChunkDataOutputStream(x, y)) {
			NBTIOUtils.write(nbt, out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public String toString() {
		return root.relativize(file.getFile().toPath()) + "/chunk[" + x + "," + y + "]";
	}

	@Override
	public Set<String> getTags() {
		return Collections.singleton("store.chunk");
	}
}
