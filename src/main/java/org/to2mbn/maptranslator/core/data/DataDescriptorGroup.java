package org.to2mbn.maptranslator.core.data;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.to2mbn.maptranslator.core.tree.DataStoreNode;
import org.to2mbn.maptranslator.core.tree.Node;

public class DataDescriptorGroup implements Closeable {

	private Logger logger = Logger.getLogger(getClass().getCanonicalName());

	private final Set<Closeable> closeables;
	public final Set<DataDescriptor> descriptors;
	public final AtomicInteger processed = new AtomicInteger();

	public DataDescriptorGroup(Set<DataDescriptor> descriptors, Set<Closeable> closeables) {
		this.closeables = closeables;
		this.descriptors = descriptors;
	}

	public synchronized <T> Stream<T> read(Function<Node, T> mapper) {
		processed.set(0);
		return descriptors.parallelStream()
				.map(desp -> {
					try {
						DataStoreNode root = desp.createNode();
						root.read();
						Optional<T> result = Optional.of(mapper.apply(root));
						root.close();
						return result;
					} catch (Exception e) {
						logger.log(Level.WARNING, "Couldn't handle " + desp, e);
						return Optional.<T> empty();
					} finally {
						processed.getAndIncrement();
					}
				})
				.filter(optional -> optional.isPresent())
				.map(Optional::get);
	}

	public synchronized void write(Consumer<Node> mapper) {
		processed.set(0);
		descriptors.parallelStream()
				.forEach(desp -> {
					try {
						DataStoreNode root = new DataStoreNode(desp);
						try {
							root.read();
						} catch (UncheckedIOException e) {
							logger.log(Level.WARNING, "Couldn't read " + desp, e);
							return;
						}
						mapper.accept(root);
						try {
							root.write();
						} catch (UncheckedIOException e) {
							logger.log(Level.WARNING, "Couldn't read " + desp, e);
							return;
						}
					} finally {
						processed.getAndIncrement();
					}
				});
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
}
