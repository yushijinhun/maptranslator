package yushijinhun.mczhconverter.core;

import java.util.Stack;
import yushijinhun.mczhconverter.trace.NodeTrace;

public interface NBTStringVisitor {

	String visit(String str, Stack<NodeTrace> trace);
}
