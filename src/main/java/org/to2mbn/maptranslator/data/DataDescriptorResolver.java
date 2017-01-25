package org.to2mbn.maptranslator.data;

import java.nio.file.Path;

public interface DataDescriptorResolver {

	public static interface ResolvingContext {

		void addDataDescriptor(DataDescriptor descriptor);

		void addManagedResource(AutoCloseable resource);

	}

	void process(Path root, Path file, ResolvingContext ctx);

}
