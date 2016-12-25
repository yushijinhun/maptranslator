package yushijinhun.maptranslator;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import yushijinhun.maptranslator.core.NBTDescriptorFactory;
import yushijinhun.maptranslator.core.NBTDescriptorSet;
import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTString;
import yushijinhun.maptranslator.tree.ClauseNode;
import yushijinhun.maptranslator.tree.MinecraftRules;
import yushijinhun.maptranslator.tree.NBTNode;
import yushijinhun.maptranslator.tree.Node;
import yushijinhun.maptranslator.tree.TextArgumentNode;
import yushijinhun.maptranslator.tree.TreeIterator;

public class Main {

	private static Map<String, String> diff = new HashMap<>();

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
		System.out.println();
		diff.forEach((k, v) -> System.out.printf("- %s\n+ %s\n\n", k, v));
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
		if (node instanceof NBTNode) {
			if (((NBTNode) node).nbt instanceof NBT.NBTPrimitive || ((NBTNode) node).nbt instanceof NBTString) {
				System.out.print(" = ");
				System.out.print(((NBTNode) node).nbt);
			}
		}
		System.out.println();
		if (node.properties().containsKey("originCmd")) {
			String s = extractString(node);
			if (!s.equals(node.properties().get("originCmd"))) {
				diff.put((String) node.properties().get("originCmd"), s);
			}
		}
		node.unmodifiableChildren().forEach(child -> printTree(child, tabs + 1));
	}

	private static String extractString(Node node) {
		if (node instanceof NBTNode) {
			return ((NBTString) ((NBTNode) node).nbt).getString();
		} else if (node instanceof TextArgumentNode) {
			return ((TextArgumentNode) node).text;
		} else if (node instanceof ClauseNode) {
			return ((ClauseNode) node).toArgumentString();
		}
		throw new IllegalArgumentException(node.toString());
	}

}
