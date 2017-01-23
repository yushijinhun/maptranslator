package org.to2mbn.maptranslator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.to2mbn.maptranslator.core.NBTDescriptor;
import org.to2mbn.maptranslator.core.NBTDescriptorFactory;
import org.to2mbn.maptranslator.core.NBTDescriptorGroup;
import org.to2mbn.maptranslator.tree.AbstractReplacer;
import org.to2mbn.maptranslator.tree.IteratorArgument;
import org.to2mbn.maptranslator.tree.MinecraftRules;
import org.to2mbn.maptranslator.tree.NBTStoreNode;
import org.to2mbn.maptranslator.tree.Node;
import org.to2mbn.maptranslator.tree.NodeReplacer;
import org.to2mbn.maptranslator.tree.TextContext;
import org.to2mbn.maptranslator.tree.TreeIterator;
import org.to2mbn.maptranslator.ui.TreeItemConstructor;

class MapHandlerImpl implements MapHandler {

	public static CompletableFuture<MapHandler> create(File dir) {
		return new MapHandlerImpl(dir).init();
	}

	private File dir;
	private NBTDescriptorGroup desGroup;
	private List<String> excludes = new Vector<>();
	private Map<String, StringMismatchWarning> stringMismatches = new ConcurrentSkipListMap<>();
	private Map<String, ResolveFailedWarning> resolveFailures = new ConcurrentSkipListMap<>();
	private IteratorArgument mapResolvingArgument;
	private ForkJoinPool pool = new ForkJoinPool(4 * Runtime.getRuntime().availableProcessors());

	public MapHandlerImpl(File dir) {
		this.dir = dir;
		mapResolvingArgument = new IteratorArgument();
		mapResolvingArgument.markers.addAll(Arrays.asList(MinecraftRules.MARKERS));
		mapResolvingArgument.replacers.addAll(Arrays.asList(MinecraftRules.REPLACERS));
	}

	private CompletableFuture<MapHandler> init() {
		return CompletableFuture.runAsync(() -> desGroup = NBTDescriptorFactory.getDescriptors(dir))
				.thenApply(dummy -> this);
	}

	@Override
	public CompletableFuture<Map<String, List<String[]>>> extractStrings() {
		return CompletableFuture.supplyAsync(() -> {
			Predicate<String> excluder = createTextExcluder();
			BiConsumer<Map<String, List<String[]>>, Map<String, List<String[]>>> merger = (a, b) -> {
				b.forEach((k, v) -> {
					List<String[]> list = a.get(k);
					if (list == null) {
						a.put(k, v);
					} else {
						list.addAll(v);
					}
				});
			};
			clearLastWarnings();
			return desGroup.read(node -> {
				AbstractReplacer.redirectResolvingFailures(() -> resolveMap(node), failure -> resolveFailures.put(failure.path, failure));
				computeStringMismatches(node);

				// === Test code
				// For languages which have non-unicode characters,
				// we can easily test if the strings we need to translate are all found, 
				// by comparing our outputs to the strings that contain non-unicode characters.
				//
				// test(node);
				// ===

				return extractStrings(node, excluder);
			}).collect(LinkedHashMap<String, List<String[]>>::new, merger, merger);
		}, pool);
	}

	@Override
	public CompletableFuture<Void> replace(Map<String, String> table) {
		return CompletableFuture.runAsync(() -> {
			IteratorArgument arg = createReplacingArgument(table);
			desGroup.write(node -> {
				resolveMap(node);
				new TreeIterator(arg).iterate(node);
			});
		}, pool);
	}

	@Override
	public CompletableFuture<Optional<Node>> resolveNode(String[] path) {
		return CompletableFuture.supplyAsync(() -> {
			String despName = path[0];
			for (NBTDescriptor desp : desGroup.descriptors) {
				if (desp.toString().equals(despName)) {
					return resolveNode(path, desp);
				}
			}
			return Optional.empty();
		}, pool);
	}

	@Override
	public CompletableFuture<Optional<Node>> resolveNode(String argPath) {
		return CompletableFuture.supplyAsync(() -> {
			String path = argPath.trim();
			if (path.startsWith("/")) path = path.substring(1);
			for (NBTDescriptor desp : desGroup.descriptors) {
				String despName = desp.toString();
				if (path.startsWith(despName)) {
					String[] split = path.substring(despName.length()).split("/");
					int splitLen = split.length;
					int splitPos = 0;
					if (splitLen > 0 && split[splitLen - 1].isEmpty()) splitLen--;
					if (splitLen > 0 && split[0].isEmpty()) {
						splitLen--;
						splitPos++;
					}
					String[] pathArray = new String[splitLen + 1];
					System.arraycopy(split, splitPos, pathArray, 1, splitLen);
					pathArray[0] = despName;
					return resolveNode(pathArray, desp);
				}
			}
			return Optional.empty();
		});
	}

	private Optional<Node> resolveNode(String[] path, NBTDescriptor desp) {
		NBTStoreNode root = new NBTStoreNode(desp);
		root.read();
		resolveMap(root);
		Optional<Node> result = root.resolve(path, 1);
		if (result.isPresent()) {
			TreeItemConstructor.construct(root);
			return result;
		} else {
			root.close();
			return Optional.empty();
		}
	}

	private void resolveMap(Node node) {
		new TreeIterator(mapResolvingArgument).iterate(node);
	}

	private void computeStringMismatches(Node root) {
		root.travel(node -> {
			if (node.properties().containsKey("origin")) {
				TextContext.textFromNode(node).ifPresent(current -> {
					String origin = (String) node.properties().get("origin");
					if (!origin.equals(current)) {
						StringMismatchWarning mismatch = new StringMismatchWarning(node, origin, current);
						stringMismatches.put(mismatch.path, mismatch);
					}
				});
			}
		});
	}

	private Map<String, List<String[]>> extractStrings(Node root, Predicate<String> excluder) {
		Map<String, List<String[]>> result = new LinkedHashMap<>();
		root.travel(node -> {
			if (node.hasTag(MinecraftRules.translatable)) {
				TextContext.textFromNode(node).ifPresent(text -> {
					if (!text.trim().isEmpty()) {
						List<String[]> g = result.get(text);
						if (g != null) {
							g.add(node.getPathArray());
						} else {
							if (!excluder.test(text)) {
								g = new ArrayList<>();
								g.add(node.getPathArray());
								result.put(text, g);
							}
						}
					}
				});
			}
		});
		return result;
	}

	// === Test code
	/*
	private void test(Node root) {
		root.travel(node -> {
			if (node.hasTag(MinecraftRules.translatable)) {
				while (node != null) {
					node.tags().add("_tc");
					node = node.parent();
				}
			}
		});
		root.travel(node -> {
			if (node instanceof NBTMapNode && ((NBTMapNode) node).key().equals("LastOutput")) return;
			TextNodeReplacer.getText(node).ifPresent(text -> {
				boolean ta = false;
				for (int i = 0; i < text.length(); i++) {
					if (text.charAt(i) > '\u00ff') {
						ta = true;
						break;
					}
				}
				if (ta) {
					if (!node.hasTag("_tc")) {
						System.out.printf("%s\n%s\n\n", node.getPath(), text);
					}
				}
			});
		});
	}
	*/
	// ===

	private IteratorArgument createReplacingArgument(Map<String, String> table) {
		Predicate<String> excluder = createTextExcluder();
		IteratorArgument arg = new IteratorArgument();
		arg.maxIterations = 1;
		arg.replacers.add(new NodeReplacer(
				node -> {
					if (node.hasTag(MinecraftRules.translatable)) {
						Optional<String> optionalText = TextContext.textFromNode(node);
						if (optionalText.isPresent()) {
							String text = optionalText.get();
							return table.containsKey(text) && !excluder.test(text);
						}
					}
					return false;
				},
				node -> {
					String replacement = table.get(TextContext.textFromNode(node).get());
					return TextContext.getContext(node).replaceNode(node, () -> replacement);
				}));
		return arg;
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

	@Override
	public CompletableFuture<Void> close() {
		return CompletableFuture.runAsync(() -> desGroup.close());
	}

	@Override
	public List<String> excludes() {
		return excludes;
	}

	@Override
	public List<ParsingWarning> lastParsingWarnings() {
		List<ParsingWarning> result = new ArrayList<>();
		result.addAll(resolveFailures.values());
		result.addAll(stringMismatches.values());
		return result;
	}

	private void clearLastWarnings() {
		stringMismatches.clear();
		resolveFailures.clear();
	}

	@Override
	public long currentProgress() {
		return desGroup.processed.get();
	}

	@Override
	public long totalProgress() {
		return desGroup.descriptors.size();
	}

}
