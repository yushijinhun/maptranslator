package yushijinhun.maptranslator.core;

import java.io.IOException;
import yushijinhun.maptranslator.nbt.NBTTagCompound;

public interface NBTDescriptor {

	NBTTagCompound read() throws IOException;

	void write(NBTTagCompound nbt) throws IOException;
}
