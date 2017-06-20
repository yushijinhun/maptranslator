package org.to2mbn.maptranslator.data;

import java.nio.file.Path;

public class DataFileResolver implements DataDescriptorResolver {

	public static interface DescriptorFactory {

		DataDescriptor produce(Path root, Path file);

	}

	private String dirName;
	private String suffix;
	private DescriptorFactory factory;

	public DataFileResolver(String dirName, String suffix, DescriptorFactory factory) {
		this.dirName = dirName;
		this.suffix = suffix;
		this.factory = factory;
	}

	@Override
	public void process(Path root, Path file, ResolvingContext ctx) {
		if (file.getFileName().toString().endsWith(suffix)) {
			boolean inDataFolder = false;
			for (Path element : root.relativize(file)) {
				String elementName = element.toString();
				if (inDataFolder) {
					if (elementName.equals(dirName)) {
						ctx.addDataDescriptor(factory.produce(root, file));
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
