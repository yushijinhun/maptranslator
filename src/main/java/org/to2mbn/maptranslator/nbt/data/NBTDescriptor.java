package org.to2mbn.maptranslator.nbt.data;

import java.io.UncheckedIOException;
import org.to2mbn.maptranslator.core.data.DataDescriptor;
import org.to2mbn.maptranslator.core.process.TreeConstructor;
import org.to2mbn.maptranslator.core.tree.Node;
import org.to2mbn.maptranslator.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.nbt.tree.NBTRootNode;

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
