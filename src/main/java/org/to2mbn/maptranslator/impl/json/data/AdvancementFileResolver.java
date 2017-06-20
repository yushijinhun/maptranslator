package org.to2mbn.maptranslator.impl.json.data;

import org.to2mbn.maptranslator.data.DataFileResolver;

public class AdvancementFileResolver extends DataFileResolver {

	public AdvancementFileResolver() {
		super("advancements", ".json", (root, file) -> new JsonDescriptor(root, file, "store.advancements"));
	}


}
