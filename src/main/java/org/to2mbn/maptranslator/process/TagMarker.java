package org.to2mbn.maptranslator.process;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.to2mbn.maptranslator.tree.Node;

public class TagMarker {

	public final Predicate<Node> condition;
	public final Function<Node, Set<String>> tags;

	public TagMarker(Predicate<Node> condition, Function<Node, Set<String>> tags) {
		this.condition = condition;
		this.tags = tags.andThen(in -> {
			Set<String> intered = new HashSet<>();
			in.forEach(tag -> intered.add(tag.intern()));
			return intered;
		});
	}

	public TagMarker(Predicate<Node> condition, String... tags) {
		this(condition, tagsFunction(tags));
	}

	private static Function<Node, Set<String>> tagsFunction(String... tags) {
		Set<String> result = Stream.of(tags).map(String::intern).collect(Collectors.toSet());
		return dummy -> result;
	}

}
