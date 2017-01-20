package org.to2mbn.maptranslator.core;

import java.io.UncheckedIOException;
import java.util.Set;
import org.to2mbn.maptranslator.nbt.NBTCompound;

class SyncNBTDescriptor implements NBTDescriptor {

	private NBTDescriptor proxied;

	public SyncNBTDescriptor(NBTDescriptor proxied) {
		this.proxied = proxied;
	}

	@Override
	public synchronized NBTCompound read() throws UncheckedIOException {
		return proxied.read();
	}

	@Override
	public synchronized void write(NBTCompound nbt) throws UncheckedIOException {
		proxied.write(nbt);
	}

	@Override
	public Set<String> getTags() {
		return proxied.getTags();
	}

	@Override
	public String toString() {
		return proxied.toString();
	}

}
