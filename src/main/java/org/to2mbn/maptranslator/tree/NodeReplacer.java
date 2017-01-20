package org.to2mbn.maptranslator.tree;

import java.util.function.Function;
import java.util.function.Predicate;

public class NodeReplacer {

	public final Predicate<Node> condition;
	public final Function<Node, Node> replacer;

	public NodeReplacer(Predicate<Node> condition, Function<Node, Node> replacer) {
		this.condition = condition;
		this.replacer = replacer;
	}

}
