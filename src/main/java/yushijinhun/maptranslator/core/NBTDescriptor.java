package yushijinhun.maptranslator.core;

import java.io.IOException;
import yushijinhun.maptranslator.nbt.NBTCompound;

public interface NBTDescriptor {

	NBTCompound read() throws IOException;

	void write(NBTCompound nbt) throws IOException;
}
