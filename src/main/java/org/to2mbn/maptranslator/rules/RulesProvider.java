package org.to2mbn.maptranslator.rules;

import java.util.Collection;
import org.to2mbn.maptranslator.process.NodeReplacer;
import org.to2mbn.maptranslator.process.TagMarker;

public interface RulesProvider {

	Collection<? extends TagMarker> getTagMarkers();

	Collection<? extends NodeReplacer> getNodeReplacers();

}
