package yushijinhun.maptranslator;

import java.io.File;
import yushijinhun.maptranslator.core.NBTDescriptorFactory;
import yushijinhun.maptranslator.core.NBTDescriptorSet;
import yushijinhun.maptranslator.tree.Node;

public class Main {

	public static void main(String[] args) throws Exception {
		//test code
		NBTDescriptorSet collection = NBTDescriptorFactory.getDescriptors(new File("/home/yushijinhun/.minecraft/saves/New World"), 32);
		printTree(collection.tree, 0);
		collection.read().get();
		printTree(collection.tree, 0);
		collection.close();
	}

	private static void printTree(Node node, int tabs) {
		for (int i = 0; i < tabs; i++)
			System.out.print("    ");
		System.out.println(node);
		node.unmodifiableChildren().forEach(child -> printTree(child, tabs + 1));
	}

}
