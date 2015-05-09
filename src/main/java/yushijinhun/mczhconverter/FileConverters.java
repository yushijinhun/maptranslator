package yushijinhun.mczhconverter;


public class FileConverters {

	public static final FileConverter CONVERTER_NBT = new NBTFileConverter();
	public static final FileConverter CONVERTER_REGION = new RegionFileConverter();

	public static FileConverter getCoverterByFileName(String filename) {
		if (filename.endsWith(".mca")) {
			return CONVERTER_REGION;
		} else if (filename.endsWith(".json")) {
			return null;
		}
		return CONVERTER_NBT;
	}
}
