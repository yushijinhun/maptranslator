package yushijinhun.mczhconverter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import yushijinhun.mczhconverter.nbt.CompressedStreamTools;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;
import yushijinhun.mczhconverter.nbt.RegionFile;

public class RegionFileConverter implements FileConverter {

	private NBTConverter convetor = new NBTConverter();

	@Override
	public void convert(File file) {
		try (RegionFile region = new RegionFile(file)) {
			for (int x = 0; x < 32; x++) {
				for (int z = 0; z < 32; z++) {
					if (region.isChunkSaved(x, z)) {
						NBTTagCompound nbt;
						try (DataInputStream in = region.getChunkDataInputStream(x, z)) {
							nbt = CompressedStreamTools.read(in);
						} catch (Exception e) {
							System.err.printf("[ERROR] Failed to read %s chunk %d, %d\n", file, x, z);
							e.printStackTrace();
							continue;
						}

						try {
							nbt = (NBTTagCompound) convetor.convert(nbt);
						} catch (Exception e) {
							System.err.printf("[ERROR] Failed to convert %s chunk %d, %d\n", file, x, z);
							e.printStackTrace();
							continue;
						}

						try (DataOutputStream out = region.getChunkDataOutputStream(x, z)) {
							CompressedStreamTools.write(nbt, out);
						} catch (Exception e) {
							System.err.printf("[ERROR] Failed to write %s chunk %d, %d\n", file, x, z);
							e.printStackTrace();
							continue;
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.printf("[ERROR] Failed to handle %s\n", file);
			e.printStackTrace();
		}
	}

}
