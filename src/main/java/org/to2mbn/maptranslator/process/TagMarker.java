package org.to2mbn.maptranslator.process;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.to2mbn.maptranslator.tree.Node;

public class TagMarker {

	public final Predicate<Node> condition;
	public final Function<Node, Set<String>> tags;

	public TagMarker(Predicate<Node> condition, Function<Node, Set<String>> tags) {
		this.condition = condition;
		this.tags = tags;
	}

	public TagMarker(Predicate<Node> condition, String... tags) {
		this(condition, node -> new LinkedHashSet<>(Arrays.asList(tags)));
	}

}
