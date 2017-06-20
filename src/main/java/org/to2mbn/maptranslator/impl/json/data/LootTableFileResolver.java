package org.to2mbn.maptranslator.impl.json.data;

import org.to2mbn.maptranslator.data.DataFileResolver;

public class LootTableFileResolver extends DataFileResolver {

	public LootTableFileResolver() {
		super("loot_tables", ".json", (root, file) -> new JsonDescriptor(root, file, "store.loottable"));
	}

}
