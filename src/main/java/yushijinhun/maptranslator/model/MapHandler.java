package yushijinhun.maptranslator.model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import yushijinhun.maptranslator.tree.Node;

public interface MapHandler {

	public static CompletableFuture<MapHandler> create(File mapFolder) {
		return MapHandlerImpl.create(mapFolder);
	}

	List<String> excludes();

	CompletableFuture<Map<String, Set<Node>>> extractStrings();

	Node rootNode();

	List<ParseWarning> parseWarnings();

	CompletableFuture<Void> save();

	CompletableFuture<Void> close();

	CompletableFuture<Void> replace(Map<String, String> table);

}
