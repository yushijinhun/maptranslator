package yushijinhun.mczhconverter.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;

public class NBTDescriptorSet implements Closeable {

	protected class VisitTask implements Runnable {

		public final NBTDescriptor descriptor;
		public final NBTStringVisitor visitor;
		public final boolean write;

		public VisitTask(NBTDescriptor descriptor, NBTStringVisitor visitor, boolean write) {
			this.descriptor = descriptor;
			this.visitor = visitor;
			this.write = write;
		}

		@Override
		public void run() {
			logger.info(String.format("%s accpet %s with %s", descriptor, visitor, write ? "rw" : "r"));
			try {
				NBTTagCompound nbt = descriptor.read();
				NBTStringIterator.accept(nbt, visitor);
				if (write) {
					descriptor.write(nbt);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, String.format("%s failed to accpet %s", descriptor, visitor), e);
			}
		}
	}

	protected Logger logger = Logger.getLogger(getClass().getCanonicalName());
	protected ExecutorService pool;

	public final Set<NBTDescriptor> descriptors;
	public final Set<Closeable> closeables;

	public NBTDescriptorSet(ExecutorService pool, Set<NBTDescriptor> descriptors, Set<Closeable> closeables) {
		this.descriptors = descriptors;
		this.closeables = closeables;
		this.pool = pool;
	}

	public Set<Future<?>> accpetVisitor(NBTStringVisitor visitor, boolean write) {
		Set<Future<?>> tasks = Collections.synchronizedSet(new LinkedHashSet<Future<?>>());
		for (NBTDescriptor descriptor : descriptors) {
			tasks.add(pool.submit(new VisitTask(descriptor, visitor, write)));
		}
		return tasks;
	}

	@Override
	public void close() {
		for (Closeable closeable : closeables) {
			logger.info(String.format("Closing %s", closeable));
			try {
				closeable.close();
			} catch (IOException e) {
				logger.log(Level.WARNING, String.format("Failed to close %s ,skipped", closeable), e);
				continue;
			}
		}
	}


	@Override
	protected void finalize() throws Throwable {
		pool.shutdownNow();
		close();
		super.finalize();
	}
}
