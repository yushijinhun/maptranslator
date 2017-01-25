package org.to2mbn.maptranslator.data;

import java.io.UncheckedIOException;
import java.util.Set;
import org.to2mbn.maptranslator.tree.Node;

class SyncDataDescriptor implements DataDescriptor {

	private DataDescriptor proxied;

	public SyncDataDescriptor(DataDescriptor proxied) {
		this.proxied = proxied;
	}

	@Override
	public Set<String> getTags() {
		return proxied.getTags();
	}

	@Override
	public String toString() {
		return proxied.toString();
	}

	@Override
	public Node read() throws UncheckedIOException {
		synchronized (proxied) {
			return proxied.read();
		}
	}

	@Override
	public void write(Node node) throws UncheckedIOException {
		synchronized (proxied) {
			proxied.write(node);
		}
	}

}
