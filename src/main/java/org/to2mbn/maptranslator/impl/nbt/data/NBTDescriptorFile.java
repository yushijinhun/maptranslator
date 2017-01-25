package org.to2mbn.maptranslator.impl.nbt.data;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

abstract class NBTDescriptorFile implements NBTDescriptor {

	private final Path root;
	protected final Path file;

	public NBTDescriptorFile(Path root, Path file) {
		this.root = root;
		this.file = file;
	}

	@Override
	public String toString() {
		return root.relativize(file).toString();
	}

	@Override
	public Set<String> getTags() {
		String filename = file.getFileName().toString();
		String parentname = file.normalize().getParent().getFileName().toString();
		if ("level.dat".equals(filename)) {
			return Collections.singleton("store.level");
		} else if ("players".equals(parentname) || "playerdata".equals(parentname)) {
			return Collections.singleton("store.player");
		} else if ("data".equals(parentname)) {
			if ("scoreboard.dat".equals(filename)) {
				return Collections.singleton("store.scoreboard");
			}
		} else if ("structures".equals(parentname)) {
			if (filename.endsWith(".nbt")) {
				return Collections.singleton("store.structure");
			}
		}
		return Collections.emptySet();
	}

}
