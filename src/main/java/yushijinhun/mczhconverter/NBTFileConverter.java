package yushijinhun.mczhconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import yushijinhun.mczhconverter.nbt.CompressedStreamTools;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;

public class NBTFileConverter implements FileConverter {

	private NBTConverter convetor = new NBTConverter();

	@Override
	public void convert(File file) {
		NBTTagCompound nbt;
		boolean compressed;
		try {
			nbt = CompressedStreamTools.read(file);
			compressed = false;
		} catch (Exception e) {
			try (InputStream in = new FileInputStream(file)) {
				nbt = CompressedStreamTools.readCompressed(in);
				compressed = true;
			} catch (Exception e1) {
				System.err.printf("[WARNING] Failed to read %s, skipped\n", file);
				return;
			}
		}

		try {
			nbt = (NBTTagCompound) convetor.convert(nbt);
		} catch (Exception e) {
			System.err.printf("[WARNING] Failed to convert %s, skipped\n", file);
			return;
		}

		try {
			if (compressed) {
				try (OutputStream out = new FileOutputStream(file)) {
					CompressedStreamTools.writeCompressed(nbt, out);
				}
			} else {
				CompressedStreamTools.write(nbt, file);
			}
		} catch (Exception e) {
			System.err.printf("[ERROR] Failed to write %s\n", file);
			e.printStackTrace();
			return;
		}
	}
}
