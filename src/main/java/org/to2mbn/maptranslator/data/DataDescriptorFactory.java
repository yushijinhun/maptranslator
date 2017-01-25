package org.to2mbn.maptranslator.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.to2mbn.maptranslator.data.DataDescriptorResolver.ResolvingContext;

public final class DataDescriptorFactory {

	private static class VolatileResolvingContext implements ResolvingContext {

		private Thread t;
		private volatile boolean available = true;
		private ResolvingContext proxied;

		public VolatileResolvingContext(ResolvingContext ctx) {
			this.t = Thread.currentThread();
			this.proxied = ctx;
		}

		private void check() {
			if (Thread.currentThread() != t) throw new IllegalStateException("Cannot be called from thread " + Thread.currentThread().getName());
			if (!available) throw new IllegalStateException("Context is not available");
		}

		@Override
		public void addDataDescriptor(DataDescriptor descriptor) {
			check();
			proxied.addDataDescriptor(new SyncDataDescriptor(descriptor));
		}

		@Override
		public void addManagedResource(AutoCloseable resource) {
			check();
			proxied.addManagedResource(resource);
		}

		public void invalidate() {
			check();
			available = false;
			proxied = null;
		}

	}

	private static class GlobalDataDescriptorResolver implements DataDescriptorResolver {

		private List<DataDescriptorResolver> resolvers = new ArrayList<>();

		public GlobalDataDescriptorResolver() {
			ServiceLoader.load(DataDescriptorResolver.class).forEach(resolvers::add);
		}

		@Override
		public void process(Path root, Path file, ResolvingContext ctx) {
			for (DataDescriptorResolver resolver : resolvers) {
				VolatileResolvingContext vctx = new VolatileResolvingContext(ctx);
				try {
					resolver.process(root, file, vctx);
				} catch (Throwable e) {
					LOGGER.log(Level.WARNING, "Exception occurred during invoking " + resolver, e);
				}
				vctx.invalidate();
			}
		}

	}

	private static final Logger LOGGER = Logger.getLogger(DataDescriptorFactory.class.getCanonicalName());

	private DataDescriptorFactory() {}

	public static DataDescriptorResolver getGlobalResolver() {
		return new GlobalDataDescriptorResolver();
	}

}
