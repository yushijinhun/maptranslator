package org.to2mbn.maptranslator.impl.nbt.data;

import java.io.UncheckedIOException;
import org.to2mbn.maptranslator.data.DataDescriptor;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.impl.nbt.process.NBTTreeConstructor;
import org.to2mbn.maptranslator.impl.nbt.tree.NBTRootNode;
import org.to2mbn.maptranslator.tree.Node;

public interface NBTDescriptor extends DataDescriptor {

	NBTCompound readNBT() throws UncheckedIOException;

	void writeNBT(NBTCompound nbt) throws UncheckedIOException;

	@Override
	default Node read() throws UncheckedIOException {
		return NBTTreeConstructor.construct(readNBT());
	}

	@Override
	default void write(Node node) throws UncheckedIOException {
		writeNBT((NBTCompound) ((NBTRootNode) node).nbt);
	}

}
