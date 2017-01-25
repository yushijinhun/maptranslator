package org.to2mbn.maptranslator.process;

import java.util.function.Function;
import java.util.function.Predicate;
import org.to2mbn.maptranslator.tree.Node;

public class NodeReplacer {

	public final Predicate<Node> condition;
	public final Function<Node, Node> replacer;

	public NodeReplacer(Predicate<Node> condition, Function<Node, Node> replacer) {
		this.condition = condition;
		this.replacer = replacer;
	}

}
