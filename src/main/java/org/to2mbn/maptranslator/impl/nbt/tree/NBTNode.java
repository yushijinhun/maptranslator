package org.to2mbn.maptranslator.impl.nbt.tree;

import java.util.Optional;
import java.util.function.Supplier;
import org.to2mbn.maptranslator.impl.nbt.parse.NBT;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTString;
import org.to2mbn.maptranslator.tree.Node;
import org.to2mbn.maptranslator.tree.TextNode;

public abstract class NBTNode extends Node implements TextNode {

	public NBT nbt;

	public NBTNode(NBT nbt) {
		this.nbt = nbt;
	}

	public void replaceNBT(NBT newnbt) {
		nbt = newnbt;
	}

	public static String valueToString(NBT nbt) {
		if (nbt instanceof NBTString) {
			return ((NBTString) nbt).getString();
		} else {
			return nbt.toString();
		}
	}

	@Override
	public String getStringValue() {
		return valueToString(nbt);
	}

	@Override
	public Optional<String> getNodeText() {
		if (nbt instanceof NBTString)
			return Optional.ofNullable(((NBTString) nbt).getString());
		return Optional.empty();
	}

	@Override
	public Node replaceNodeText(Supplier<String> proxyTarget) {
		NBTStringProxy proxy = new NBTStringProxy();
		proxy.handler = proxyTarget;
		replaceNBT(proxy);
		return this;
	}

}
