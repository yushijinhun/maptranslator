package yushijinhun.maptranslator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import yushijinhun.maptranslator.IteratorArgument;
import yushijinhun.maptranslator.core.NBTDescriptorFactory;
import yushijinhun.maptranslator.core.NBTDescriptorGroup;
import yushijinhun.maptranslator.tree.MinecraftRules;
import yushijinhun.maptranslator.tree.Node;
import yushijinhun.maptranslator.tree.NodeReplacer;
import yushijinhun.maptranslator.tree.TextNodeReplacer;

class MapHandlerImpl implements MapHandler {

	public static CompletableFuture<MapHandler> create(File dir) {
		return new MapHandlerImpl(dir).init();
	}

	private File dir;
	private NBTDescriptorGroup desGroup;
	private ObservableList<String> excludes = FXCollections.observableArrayList();
	private List<ParseWarning> parseWarnings = new ArrayList<>();

	public MapHandlerImpl(File dir) {
		this.dir = dir;
	}

	private CompletableFuture<MapHandler> init() {
		return CompletableFuture.runAsync(() -> desGroup = NBTDescriptorFactory.getDescriptors(dir, 16))
				.thenCompose(dummy -> desGroup.read())
				.thenRun(() -> {
					desGroup.tree.travel(node -> {
						if (node.properties().containsKey("origin")) {
							TextNodeReplacer.getText(node).ifPresent(current -> {
								String origin = (String) node.properties().get("origin");
								if (!origin.equals(current)) {
									parseWarnings.add(new ParseWarning(node, origin, current));
								}
							});
						}
					});
				})
				.thenApply(dummy -> this);
	}

	@Override
	public CompletableFuture<Map<String, Set<Node>>> extractStrings() {
		Predicate<String> excluder = createTextExcluder();
		Map<String, Set<Node>> result = new LinkedHashMap<>();
		return CompletableFuture.supplyAsync(() -> {
			rootNode().travel(node -> {
				if (node.hasTag(MinecraftRules.translatable)) {
					TextNodeReplacer.getText(node).ifPresent(text -> {
						Set<Node> g = result.get(text);
						if (g != null) {
							g.add(node);
						} else {
							if (!excluder.test(text)) {
								g = new LinkedHashSet<>();
								g.add(node);
								result.put(text, g);
							}
						}
					});
				}
			});
			return result;
		});
	}

	@Override
	public CompletableFuture<Void> replace(Map<String, String> table) {
		Predicate<String> excluder = createTextExcluder();
		IteratorArgument arg = new IteratorArgument();
		arg.maxIterations = 1;
		arg.replacers.add(new NodeReplacer(
				node -> {
					if (node.hasTag(MinecraftRules.translatable)) {
						Optional<String> optionalText = TextNodeReplacer.getText(node);
						if (optionalText.isPresent()) {
							String text = optionalText.get();
							return table.containsKey(text) && !excluder.test(text);
						}
					}
					return false;
				},
				node -> {
					String replacement = table.get(TextNodeReplacer.getText(node).get());
					return TextNodeReplacer.getContext(node).replaceNode(node, () -> replacement);
				}));
		return desGroup.iterate(arg);
	}

	@Override
	public CompletableFuture<Void> save() {
		return desGroup.write();
	}

	@Override
	public CompletableFuture<Void> close() {
		return CompletableFuture.runAsync(() -> desGroup.close());
	}

	@Override
	public ObservableList<String> excludes() {
		return excludes;
	}

	@Override
	public Node rootNode() {
		return desGroup.tree;
	}

	@Override
	public List<ParseWarning> parseWarnings() {
		return parseWarnings;
	}

	private Predicate<String> createTextExcluder() {
		Pattern[] patterns = new Pattern[excludes.size()];
		for (int i = 0; i < patterns.length; i++) {
			patterns[i] = Pattern.compile(excludes.get(i));
		}
		return text -> {
			for (Pattern p : patterns) {
				if (p.matcher(text).matches()) {
					return true;
				}
			}
			return false;
		};
	}

}
