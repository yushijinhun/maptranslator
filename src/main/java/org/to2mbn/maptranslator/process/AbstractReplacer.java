package org.to2mbn.maptranslator.process;

import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.to2mbn.maptranslator.model.ResolveFailedWarning;
import org.to2mbn.maptranslator.rules.RulesConstants;
import org.to2mbn.maptranslator.tree.Node;

public abstract class AbstractReplacer {

	private static final Logger LOGGER = Logger.getLogger(CommandReplacer.class.getCanonicalName());

	private static ThreadLocal<Stack<Consumer<ResolveFailedWarning>>> resolvingFailedListeners = ThreadLocal.withInitial(Stack::new);

	public static void redirectResolvingFailures(Runnable action, Consumer<ResolveFailedWarning> handler) {
		redirectResolvingFailures(() -> {
			action.run();
			return null;
		}, handler);
	}

	public static <T> T redirectResolvingFailures(Supplier<T> action, Consumer<ResolveFailedWarning> handler) {
		Stack<Consumer<ResolveFailedWarning>> stack = resolvingFailedListeners.get();
		stack.push(handler);
		try {
			return action.get();
		} finally {
			stack.pop();
			if (stack.isEmpty()) {
				resolvingFailedListeners.remove();
			}
		}
	}

	protected static void postResolveFailedWarning(ResolveFailedWarning post) {
		Stack<Consumer<ResolveFailedWarning>> stack = resolvingFailedListeners.get();
		stack.forEach(listener -> listener.accept(post));
		if (stack.isEmpty()) {
			resolvingFailedListeners.remove();
			LOGGER.log(Level.WARNING, String.format("Couldn't solve command node %s\nText: %s\nArguments: %s", post.path, post.text, post.arguments), post.exception);
		}
	}

	protected static Optional<String> getNodeText(Node node) {
		return node.getText().map(text -> {
			if (node.hasTag(RulesConstants.normalize_space)) {
				return StringUtils.normalizeSpace(text);
			} else {
				return text;
			}
		});
	}

}
