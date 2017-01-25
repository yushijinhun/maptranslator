package org.to2mbn.maptranslator.core;

import java.io.UncheckedIOException;
import org.to2mbn.maptranslator.nbt.NBTCompound;
import org.to2mbn.maptranslator.tree.NBTRootNode;
import org.to2mbn.maptranslator.tree.Node;
import org.to2mbn.maptranslator.tree.TreeConstructor;

public interface NBTDescriptor extends DataDescriptor {

	NBTCompound readNBT() throws UncheckedIOException;

	void writeNBT(NBTCompound nbt) throws UncheckedIOException;

	@Override
	default Node read() throws UncheckedIOException {
		return TreeConstructor.construct(readNBT());
	}

	@Override
	default void write(Node node) throws UncheckedIOException {
		writeNBT((NBTCompound) ((NBTRootNode) node).nbt);
	}

}
