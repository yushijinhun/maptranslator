package yushijinhun.mczhconverter.core;

import java.io.IOException;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;

public interface NBTDescriptor {

	NBTTagCompound read() throws IOException;

	void write(NBTTagCompound nbt) throws IOException;
}
