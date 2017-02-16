package org.to2mbn.maptranslator.process;

import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.to2mbn.maptranslator.rules.RulesConstants;
import org.to2mbn.maptranslator.tree.Node;

public abstract class AbstractReplacer {

	private static final Logger LOGGER = Logger.getLogger(CommandReplacer.class.getCanonicalName());

	private static ThreadLocal<Stack<Consumer<NodeParsingException>>> parsingExceptionListeners = ThreadLocal.withInitial(Stack::new);

	public static void redirectParsingExceptions(Runnable action, Consumer<NodeParsingException> handler) {
		redirectParsingExceptions(() -> {
			action.run();
			return null;
		}, handler);
	}

	public static <T> T redirectParsingExceptions(Supplier<T> action, Consumer<NodeParsingException> handler) {
		Stack<Consumer<NodeParsingException>> stack = parsingExceptionListeners.get();
		stack.push(handler);
		try {
			return action.get();
		} finally {
			stack.pop();
			if (stack.isEmpty()) {
				parsingExceptionListeners.remove();
			}
		}
	}

	private static void postNodeParsingException(NodeParsingException exception) {
		LOGGER.log(Level.WARNING, "Couldn't parse node", exception);
		Stack<Consumer<NodeParsingException>> stack = parsingExceptionListeners.get();
		stack.forEach(listener -> listener.accept(exception));
		if (stack.isEmpty()) {
			parsingExceptionListeners.remove();
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

	public NodeReplacer toNodeReplacer() {
		return new NodeReplacer(
				node -> defer(() -> matches(node), node, false),
				node -> defer(() -> replace(node), node, null));
	}

	private <T> T defer(Callable<T> func, Node node, T defaultVal) {
		try {
			return func.call();
		} catch (NodeParsingException e) {
			postNodeParsingException(e);
		} catch (Throwable e) {
			postNodeParsingException(new NodeParsingException(node, e));
		}
		return defaultVal;
	}

	abstract protected boolean matches(Node node) throws NodeParsingException;

	abstract protected Node replace(Node node) throws NodeParsingException;

}
