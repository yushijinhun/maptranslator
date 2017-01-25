package org.to2mbn.maptranslator.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;
import org.to2mbn.maptranslator.process.NodeReplacer;
import org.to2mbn.maptranslator.process.TagMarker;

public final class RulesFactory {

	private RulesFactory() {}

	private static class GlobalRulesProvider implements RulesProvider {

		private List<RulesProvider> rules = new ArrayList<>();

		public GlobalRulesProvider() {
			ServiceLoader.load(RulesProvider.class).forEach(rules::add);
		}

		@Override
		public Collection<? extends TagMarker> getTagMarkers() {
			return collect(RulesProvider::getTagMarkers);
		}

		@Override
		public Collection<? extends NodeReplacer> getNodeReplacers() {
			return collect(RulesProvider::getNodeReplacers);
		}

		private <T> Set<T> collect(Function<RulesProvider, Collection<? extends T>> func) {
			Set<T> result = new LinkedHashSet<>();
			rules.forEach(rule -> result.addAll(func.apply(rule)));
			return result;
		}

	}

	public static RulesProvider getGlobalProvider() {
		return new GlobalRulesProvider();
	}

}
