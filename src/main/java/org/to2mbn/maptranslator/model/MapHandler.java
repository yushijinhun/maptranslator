package org.to2mbn.maptranslator.model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.to2mbn.maptranslator.core.tree.Node;

public interface MapHandler {

	public static CompletableFuture<MapHandler> create(File mapFolder) {
		return MapHandlerImpl.create(mapFolder);
	}

	List<String> excludes();

	CompletableFuture<Map<String, List<String[]>>> extractStrings();

	List<ParsingWarning> lastParsingWarnings();

	CompletableFuture<Void> replace(Map<String, String> table);

	CompletableFuture<Optional<Node>> resolveNode(String[] path);

	CompletableFuture<Optional<Node>> resolveNode(String path);

	CompletableFuture<Void> close();

	long currentProgress();

	long totalProgress();

}
