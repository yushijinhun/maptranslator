package yushijinhun.maptranslator.tree;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

public class TagMarker {

	public final Predicate<Node> condition;
	public final Set<String> tags;

	public TagMarker(Predicate<Node> condition, Set<String> tags) {
		this.condition = condition;
		this.tags = Collections.unmodifiableSet(tags);
	}

	public TagMarker(Predicate<Node> condition, String... tags) {
		this(condition, new LinkedHashSet<>(Arrays.asList(tags)));
	}

}
