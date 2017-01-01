package yushijinhun.maptranslator;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import yushijinhun.maptranslator.core.NBTDescriptorFactory;
import yushijinhun.maptranslator.core.NBTDescriptorSet;
import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.tree.MinecraftRules;
import yushijinhun.maptranslator.tree.NBTNode;
import yushijinhun.maptranslator.tree.Node;
import yushijinhun.maptranslator.tree.TextNodeReplacer;
import yushijinhun.maptranslator.tree.TreeIterator;
import yushijinhun.maptranslator.tree.TextNodeReplacer.TextContext;

public class Main {

	private static Map<String, String> diff = new HashMap<>();
	private static Set<String> translatables = new LinkedHashSet<>();

	public static void main(String[] args) throws Exception {
		//test code
		NBTDescriptorSet collection = NBTDescriptorFactory.getDescriptors(new File("/home/yushijinhun/.minecraft/saves/Captive Minecraft III"), 8);
		collection.read().get();

		TreeIterator iterator = new TreeIterator();
		iterator.markers.addAll(Arrays.asList(MinecraftRules.MARKERS));
		iterator.replacers.addAll(Arrays.asList(MinecraftRules.REPLACERS));
		iterator.iterate(collection.tree);

		printTree(collection.tree, 0);
		collection.close();
		Thread.sleep(500);
		System.out.println();
		diff.forEach((k, v) -> System.out.printf("- %s\n+ %s\n\n", k, v));
		System.out.println();
		System.out.println();
		translatables.forEach(System.out::println);
	}

	private static void printTree(Node node, int tabs) {
		for (int i = 0; i < tabs; i++)
			System.out.print("    ");
		if (node instanceof NBTNode) {
			System.out.print('<');
			System.out.print(NBT.NBT_TYPES[((NBTNode) node).nbt.getId()]);
			System.out.print("> ");
		}
		System.out.print(node);
		System.out.print(' ');
		System.out.print(node.tags());
		if (node instanceof NBTNode && ((NBTNode) node).nbt instanceof NBT.NBTPrimitive) {
			System.out.print(" = ");
			System.out.print(((NBTNode) node).nbt);
		} else {
			TextNodeReplacer.getText(node).ifPresent(t -> {
				System.out.print(" = ");
				System.out.print(t);
			});
		}
		System.out.println();
		if (node.properties().containsKey("origin")) {
			String s = TextNodeReplacer.getText(node).get();
			if (!s.equals(node.properties().get("origin"))) {
				diff.put((String) node.properties().get("origin"), s);
			}
		}
		if (node.hasTag(MinecraftRules.translatable)) {
			TextContext ctx = TextNodeReplacer.getContext(node);
			if (ctx != null) {
				String text = ctx.getText(node);
				if (text != null) {
					translatables.add(text);
				}
			}
		}
		node.unmodifiableChildren().forEach(child -> printTree(child, tabs + 1));
	}

}
