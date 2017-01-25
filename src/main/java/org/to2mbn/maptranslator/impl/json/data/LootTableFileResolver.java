package org.to2mbn.maptranslator.impl.json.data;

import java.nio.file.Path;
import org.to2mbn.maptranslator.data.DataDescriptorResolver;

public class LootTableFileResolver implements DataDescriptorResolver {

	@Override
	public void process(Path root, Path file, ResolvingContext ctx) {
		String name = file.getFileName().toString();
		if (name.endsWith(".json")) {
			boolean matches = false;
			for (Path element : file) {
				if (element.toString().equals("loot_tables")) {
					matches = true;
					break;
				}
			}
			if (matches) {
				ctx.addDataDescriptor(new JsonDescriptor(root, file, "store.loottable"));
			}
		}
	}

}
