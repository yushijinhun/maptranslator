package yushijinhun.mczhconverter.nbt;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile implements Closeable {
	class ChunkBuffer extends ByteArrayOutputStream {
		private int chunkX;
		private int chunkZ;

		public ChunkBuffer(int x, int z) {
			super(8096);
			chunkX = x;
			chunkZ = z;
		}

		@Override
		public void close() throws IOException {
			RegionFile.this.write(chunkX, chunkZ, buf, count);
		}

		@Override
		protected void finalize() throws Throwable {
			close();
			super.finalize();
		}
	}

	private static final byte[] emptySector = new byte[4096];
	private RandomAccessFile dataFile;
	private int[] offsets;
	private int[] chunkTimestamps;
	private List<Boolean> sectorFree;
	private File file;
	private Object lock = new Object();

	public RegionFile(File file) throws IOException {
		this.file = file;
		open();
	}

	protected void open() throws IOException {
		synchronized (lock) {
			if (file.exists()) {
				file.lastModified();
			}

			chunkTimestamps = new int[1024];
			offsets = new int[1024];

			dataFile = new RandomAccessFile(file, "rw");
			int var2;

			if (dataFile.length() < 4096L) {
				for (var2 = 0; var2 < 1024; ++var2) {
					dataFile.writeInt(0);
				}

				for (var2 = 0; var2 < 1024; ++var2) {
					dataFile.writeInt(0);
				}
			}

			if ((dataFile.length() & 4095L) != 0L) {
				for (var2 = 0; var2 < (dataFile.length() & 4095L); ++var2) {
					dataFile.write(0);
				}
			}

			var2 = (int) dataFile.length() / 4096;
			sectorFree = new ArrayList<>(var2);
			int var3;

			for (var3 = 0; var3 < var2; ++var3) {
				sectorFree.add(Boolean.valueOf(true));
			}

			sectorFree.set(0, Boolean.valueOf(false));
			sectorFree.set(1, Boolean.valueOf(false));
			dataFile.seek(0L);
			int var4;

			for (var3 = 0; var3 < 1024; ++var3) {
				var4 = dataFile.readInt();
				offsets[var3] = var4;

				if ((var4 != 0) && (((var4 >> 8) + (var4 & 255)) <= sectorFree.size())) {
					for (int var5 = 0; var5 < (var4 & 255); ++var5) {
						sectorFree.set((var4 >> 8) + var5, Boolean.valueOf(false));
					}
				}
			}

			for (var3 = 0; var3 < 1024; ++var3) {
				var4 = dataFile.readInt();
				chunkTimestamps[var3] = var4;
			}
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (lock) {
			if (dataFile != null) {
				dataFile.close();
			}
		}
	}

	public DataInputStream getChunkDataInputStream(int x, int y) throws IOException {
		synchronized (lock) {
			if (outOfBounds(x, y)) {
				return null;
			}
			int var3 = getOffset(x, y);

			if (var3 == 0) {
				return null;
			}
			int var4 = var3 >> 8;
			int var5 = var3 & 255;

			if ((var4 + var5) > sectorFree.size()) {
				return null;
			}
			dataFile.seek(var4 * 4096);
			int var6 = dataFile.readInt();

			if (var6 > (4096 * var5)) {
				return null;
			} else if (var6 <= 0) {
				return null;
			} else {
				byte var7 = dataFile.readByte();
				byte[] var8;

				if (var7 == 1) {
					var8 = new byte[var6 - 1];
					dataFile.read(var8);
					return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(var8))));
				} else if (var7 == 2) {
					var8 = new byte[var6 - 1];
					dataFile.read(var8);
					return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(var8))));
				} else {
					return null;
				}
			}
		}
	}

	public DataOutputStream getChunkDataOutputStream(int x, int z) {
		return outOfBounds(x, z) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(x, z)));
	}

	private int getOffset(int x, int y) {
		return offsets[x + (y * 32)];
	}

	public boolean isChunkSaved(int x, int z) {
		return getOffset(x, z) != 0;
	}

	private boolean outOfBounds(int x, int z) {
		return (x < 0) || (x >= 32) || (z < 0) || (z >= 32);
	}

	private void setOffset(int x, int z, int offset) throws IOException {
		synchronized (lock) {
			offsets[x + (z * 32)] = offset;
			dataFile.seek((x + (z * 32)) * 4);
			dataFile.writeInt(offset);
		}
	}

	private void write(int sectorNumber, byte[] data, int length) throws IOException {
		synchronized (lock) {
			dataFile.seek(sectorNumber * 4096);
			dataFile.writeInt(length + 1);
			dataFile.writeByte(2);
			dataFile.write(data, 0, length);
		}
	}

	protected void write(int x, int z, byte[] data, int length) throws IOException {
		synchronized (lock) {
			int var5 = getOffset(x, z);
			int var6 = var5 >> 8;
			int var7 = var5 & 255;
			int var8 = ((length + 5) / 4096) + 1;

			if (var8 >= 256) {
				return;
			}

			if ((var6 != 0) && (var7 == var8)) {
				write(var6, data, length);
			} else {
				int var9;

				for (var9 = 0; var9 < var7; ++var9) {
					sectorFree.set(var6 + var9, Boolean.valueOf(true));
				}

				var9 = sectorFree.indexOf(Boolean.valueOf(true));
				int var10 = 0;
				int var11;

				if (var9 != -1) {
					for (var11 = var9; var11 < sectorFree.size(); ++var11) {
						if (var10 != 0) {
							if (sectorFree.get(var11).booleanValue()) {
								++var10;
							} else {
								var10 = 0;
							}
						} else if (sectorFree.get(var11).booleanValue()) {
							var9 = var11;
							var10 = 1;
						}

						if (var10 >= var8) {
							break;
						}
					}
				}

				if (var10 >= var8) {
					var6 = var9;
					setOffset(x, z, (var9 << 8) | var8);

					for (var11 = 0; var11 < var8; ++var11) {
						sectorFree.set(var6 + var11, Boolean.valueOf(false));
					}

					write(var6, data, length);
				} else {
					dataFile.seek(dataFile.length());
					var6 = sectorFree.size();

					for (var11 = 0; var11 < var8; ++var11) {
						dataFile.write(emptySector);
						sectorFree.add(Boolean.valueOf(false));
					}

					write(var6, data, length);
					setOffset(x, z, (var6 << 8) | var8);
				}
			}
		}
	}

	public File getFile() {
		return file;
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
