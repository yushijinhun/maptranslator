package org.to2mbn.maptranslator.impl.json.data;

import java.nio.file.Path;
import org.to2mbn.maptranslator.data.DataDescriptorResolver;

public class DataJsonFileResolver implements DataDescriptorResolver {

	private String dirName;
	private String tag;

	public DataJsonFileResolver(String dirName, String tag) {
		this.dirName = dirName;
		this.tag = tag;
	}

	@Override
	public void process(Path root, Path file, ResolvingContext ctx) {
		if (file.getFileName().toString().endsWith(".json")) {
			boolean inDataFolder = false;
			for (Path element : root.relativize(file)) {
				String elementName = element.toString();
				if (inDataFolder) {
					if (elementName.equals(dirName)) {
						ctx.addDataDescriptor(new JsonDescriptor(root, file, tag));
					}
					break;
				}
				if (elementName.equals("data")) {
					inDataFolder = true;
				}
			}
		}
	}

}
