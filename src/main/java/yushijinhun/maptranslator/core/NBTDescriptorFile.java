package yushijinhun.maptranslator.core;

import java.io.File;
import java.util.Collections;
import java.util.Set;

abstract class NBTDescriptorFile implements NBTDescriptor {

	protected File file;

	public NBTDescriptorFile(File file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return file.getPath();
	}

	@Override
	public Set<String> getTags() {
		String filename = file.getName();
		String parentname = file.getParentFile().getName();
		if ("level.dat".equals(filename)) {
			return Collections.singleton("store.level");
		} else if ("players".equals(parentname) || "playerdata".equals(parentname)) {
			return Collections.singleton("store.player");
		} else if ("data".equals(parentname)) {
			if ("scoreboard.dat".equals(filename)) {
				return Collections.singleton("store.scoreboard");
			}
		}
		return Collections.emptySet();
	}

}
