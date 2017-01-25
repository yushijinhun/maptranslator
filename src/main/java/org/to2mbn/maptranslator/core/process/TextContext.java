package org.to2mbn.maptranslator.core.process;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import org.to2mbn.maptranslator.core.tree.Node;

public interface TextContext {

	static final Map<Class<?>, TextContext> CTXS = TextContextImpl.createRegistry();

	String getText(Node node);

	Node replaceNode(Node node, Supplier<String> proxyTarget);

	static Optional<String> textFromNode(Node node) {
		TextContext ctx = getContext(node);
		if (ctx == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(ctx.getText(node));
	}

	static TextContext getContext(Node node) {
		for (Entry<Class<?>, TextContext> ety : TextContext.CTXS.entrySet()) {
			if (ety.getKey().isInstance(node)) return ety.getValue();
		}
		return null;
	}

}