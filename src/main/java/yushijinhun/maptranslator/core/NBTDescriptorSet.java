package yushijinhun.maptranslator.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import yushijinhun.maptranslator.tree.NBTStoreNode;
import yushijinhun.maptranslator.tree.RootNode;

public class NBTDescriptorSet implements Closeable {

	private Logger logger = Logger.getLogger(getClass().getCanonicalName());
	private ExecutorService pool;

	public final Set<NBTDescriptor> descriptors;
	public final Set<Closeable> closeables;
	public final RootNode tree;

	public NBTDescriptorSet(ExecutorService pool, Set<NBTDescriptor> descriptors, Set<Closeable> closeables) {
		this.descriptors = descriptors;
		this.closeables = closeables;
		this.pool = pool;
		tree = new RootNode();
		descriptors.forEach(descriptor -> tree.addChild(new NBTStoreNode(descriptor)));
	}

	public CompletableFuture<Void> read() {
		return executeOnChildren(child -> child.asyncRead(pool));
	}

	public CompletableFuture<Void> write() {
		return executeOnChildren(child -> child.asyncWrite(pool));
	}

	private CompletableFuture<Void> executeOnChildren(Function<NBTStoreNode, CompletableFuture<?>> action) {
		List<CompletableFuture<?>> subtasks = new ArrayList<>();
		tree.unmodifiableChildren().forEach(child -> {
			if (child instanceof NBTStoreNode) {
				subtasks.add(action.apply((NBTStoreNode) child));
			}
		});
		return CompletableFuture.allOf(subtasks.toArray(new CompletableFuture[subtasks.size()]));
	}

	@Override
	public void close() {
		pool.shutdownNow();
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
