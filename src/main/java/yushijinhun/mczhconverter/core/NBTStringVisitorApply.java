package yushijinhun.mczhconverter.core;

import java.util.Map;
import java.util.Stack;
import yushijinhun.mczhconverter.filter.TraceFiltering;
import yushijinhun.mczhconverter.trace.NodeTrace;

public class NBTStringVisitorApply implements NBTStringVisitor {

	private Map<String, String> patch;

	public NBTStringVisitorApply(Map<String, String> patch) {
		this.patch = patch;
	}

	@Override
	public String visit(String str, Stack<NodeTrace> trace) {
		if (TraceFiltering.shouldIgnore(str, trace))
			return null;
		return patch.get(str);
	}

}
