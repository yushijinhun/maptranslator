package org.to2mbn.maptranslator.impl.plain.data;

import org.to2mbn.maptranslator.data.DataFileResolver;

public class FunctionResolver extends DataFileResolver {

	public FunctionResolver() {
		super("functions", ".mcfunction", (root, file) -> new PlainFileDescriptor(root, file, "store.mcfunction"));
	}

}
