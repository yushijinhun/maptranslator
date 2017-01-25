package org.to2mbn.maptranslator.data;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.to2mbn.maptranslator.data.DataDescriptorResolver.ResolvingContext;
import org.to2mbn.maptranslator.tree.DataStoreNode;
import org.to2mbn.maptranslator.tree.Node;

public class DataDescriptorGroup implements Closeable {

	public static DataDescriptorGroup createFromFiles(Path root) {
		Set<DataDescriptor> descriptors = new ConcurrentSkipListSet<>(Comparator.comparing(obj -> obj.toString()));
		Set<AutoCloseable> closeables = Collections.newSetFromMap(new ConcurrentHashMap<>());
		List<Path> files;
		try {
			files = Files.walk(root).collect(Collectors.toList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		ResolvingContext ctx = new ResolvingContext() {

			@Override
			public void addManagedResource(AutoCloseable resource) {
				closeables.add(resource);
			}

			@Override
			public void addDataDescriptor(DataDescriptor descriptor) {
				descriptors.add(descriptor);
			}
		};
		DataDescriptorResolver resolver = DataDescriptorFactory.getGlobalResolver();
		files.parallelStream().forEach(file -> resolver.process(root, file, ctx));
		return new DataDescriptorGroup(descriptors, closeables);
	}

	private static final Logger LOGGER = Logger.getLogger(DataDescriptorGroup.class.getCanonicalName());

	private final Set<AutoCloseable> closeables;
	public final Set<DataDescriptor> descriptors;
	public final AtomicInteger processed = new AtomicInteger();

	public DataDescriptorGroup(Set<DataDescriptor> descriptors, Set<AutoCloseable> closeables) {
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
						LOGGER.log(Level.WARNING, "Couldn't handle " + desp, e);
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
						root.read();
						mapper.accept(root);
						root.write();
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Couldn't handle " + desp, e);
					} finally {
						processed.getAndIncrement();
					}
				});
	}

	@Override
	public void close() {
		for (AutoCloseable closeable : closeables) {
			LOGGER.info(String.format("Closing %s", closeable));
			try {
				closeable.close();
			} catch (Throwable e) {
				LOGGER.log(Level.WARNING, String.format("Failed to close %s ,skipped", closeable), e);
				continue;
			}
		}
	}
}
