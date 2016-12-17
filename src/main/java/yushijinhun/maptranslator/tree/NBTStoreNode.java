package yushijinhun.maptranslator.tree;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.swing.SwingUtilities;
import yushijinhun.maptranslator.core.NBTDescriptor;
import yushijinhun.maptranslator.nbt.NBTCompound;
import yushijinhun.maptranslator.tree.NBTRootNode;
import yushijinhun.maptranslator.tree.TreeConstructor;
import yushijinhun.maptranslator.tree.Node;

public class NBTStoreNode extends Node {

	public final NBTDescriptor descriptor;

	public NBTStoreNode(NBTDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public String toString() {
		return descriptor.toString();
	}

	// thread-safe
	public CompletableFuture<Void> asyncRead(Executor executor) {
		return CompletableFuture.supplyAsync(descriptor::read, executor)
				.thenAcceptAsync(nbt -> {
					unmodifiableChildren().forEach(this::removeChild);
					NBTRootNode node = TreeConstructor.construct(nbt);
					node.tags().addAll(descriptor.getTags());
					addChild(node);
				}, SwingUtilities::invokeLater);
	}

	// thread-safe
	public CompletableFuture<Void> asyncWrite(Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			if (unmodifiableChildren().size() == 1) {
				Node node = unmodifiableChildren().iterator().next();
				if (node instanceof NBTRootNode) {
					return (NBTCompound) ((NBTRootNode) node).nbt.clone();
				}
			}
			throw new IllegalStateException("No NBT data found in the node");
		}, SwingUtilities::invokeLater)
				.thenAcceptAsync(descriptor::write, executor);

	}

}
