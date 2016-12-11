package yushijinhun.mczhconverter.core;

import java.util.Stack;
import yushijinhun.mczhconverter.nbt.NBTBase;
import yushijinhun.mczhconverter.nbt.NBTTagCompound;
import yushijinhun.mczhconverter.nbt.NBTTagList;
import yushijinhun.mczhconverter.nbt.NBTTagString;
import yushijinhun.mczhconverter.trace.CompoundChild;
import yushijinhun.mczhconverter.trace.ListChild;
import yushijinhun.mczhconverter.trace.NodeTrace;
import yushijinhun.mczhconverter.trace.RootTag;

public final class NBTStringIterator {

	public static void accept(String source, NBTBase nbt, NBTStringVisitor visitor) {
		if (!isLeaf(nbt)) {
			Stack<NodeTrace> trace = new Stack<>();
			trace.push(new RootTag(nbt, source));
			acceptTree(nbt, visitor, trace);
			trace.pop();
		}
	}

	private static void acceptTree(NBTBase nbt, NBTStringVisitor visitor, Stack<NodeTrace> trace) {
		if (nbt instanceof NBTTagCompound) {
			acceptCompound((NBTTagCompound) nbt, visitor, trace);
		} else {
			acceptList((NBTTagList) nbt, visitor, trace);
		}
	}

	private static void acceptCompound(NBTTagCompound compound, NBTStringVisitor visitor, Stack<NodeTrace> trace) {
		for (String name : compound.getKeySet()) {
			NBTBase nbt = compound.getTag(name);
			trace.push(new CompoundChild(nbt, name));
			if (isLeaf(nbt)) {
				if (nbt instanceof NBTTagString) {
					compound.setTag(name, acceptString((NBTTagString) nbt, visitor, trace));
				}
			} else {
				acceptTree(nbt, visitor, trace);
			}
			trace.pop();
		}
	}

	private static void acceptList(NBTTagList list, NBTStringVisitor visitor, Stack<NodeTrace> trace) {
		for (int i = 0; i < list.tagCount(); i++) {
			NBTBase nbt = list.get(i);
			trace.push(new ListChild(nbt, i));
			if (isLeaf(nbt)) {
				if (nbt instanceof NBTTagString) {
					list.set(i, acceptString((NBTTagString) nbt, visitor, trace));
				}
			} else {
				acceptTree(nbt, visitor, trace);
			}
			trace.pop();
		}
	}

	private static boolean isLeaf(NBTBase nbt) {
		if ((nbt instanceof NBTTagCompound) || (nbt instanceof NBTTagList)) {
			return false;
		}
		return true;
	}

	private static NBTTagString acceptString(NBTTagString nbt, NBTStringVisitor visitor, Stack<NodeTrace> trace) {
		String result = visitor.visit(nbt.getString(), trace);
		return result == null ? nbt : new NBTTagString(result);
	}

	private NBTStringIterator() {
	}
}
