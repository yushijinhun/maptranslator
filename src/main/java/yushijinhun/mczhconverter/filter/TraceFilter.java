package yushijinhun.mczhconverter.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import yushijinhun.mczhconverter.trace.CompoundChild;
import yushijinhun.mczhconverter.trace.NodeTrace;

public class TraceFilter {

	private static final int FIXED = 0;
	private static final int TAIL = 1;
	private static final int HEAD = 2;
	private static final int MID = 3;

	public static List<String> convertTrace(Stack<NodeTrace> trace) {
		List<String> target = new ArrayList<>(trace.size());
		for (NodeTrace node : trace)
			if (node instanceof CompoundChild)
				target.add(((CompoundChild) node).key);
		return target;
	}

	private final int mode;
	private final String[] p;

	public TraceFilter(String expression) {
		String[] splited = expression.split("/");
		boolean fromroot = splited[0].isEmpty();
		boolean varend = splited[splited.length - 1].equals("**");
		if (!fromroot && !varend) {
			mode = TAIL;
		} else if (!fromroot && varend) {
			mode = MID;
		} else if (fromroot && !varend) {
			mode = FIXED;
		} else /*if (fromroot && varend)*/ {
			mode = HEAD;
		}
		{
			int l = fromroot ? 1 : 0;
			int r = varend ? splited.length - 1 : splited.length;
			int i = 0;
			p = new String[r - l];
			for (int k = l; k < r; k++) {
				String s = splited[k];
				if (s == null || s.isEmpty()) throw new IllegalArgumentException(expression);
				if (s.equals("*")) s = null;
				p[i++] = s;
			}
		}
	}

	public boolean matches(List<String> target) {
		if (mode == FIXED) {
			if (target.size() == p.length) {
				boolean matched = true;
				for (int i = p.length - 1; i >= 0; i--) {
					if (p[i] != null && !p[i].equals(target.get(i))) {
						matched = false;
						break;
					}
				}
				return matched;
			} else {
				return false;
			}
		} else if (mode == TAIL) {
			if (target.size() < p.length) {
				return false;
			} else {
				boolean matched = true;
				for (int i = 0; i < p.length; i++) {
					if (p[p.length - i - 1] != null && !p[p.length - i - 1].equals(target.get(target.size() - i - 1))) {
						matched = false;
						break;
					}
				}
				return matched;
			}
		} else if (mode == HEAD) {
			if (target.size() < p.length) {
				return false;
			} else {
				boolean matched = true;
				for (int i = 0; i < p.length; i++) {
					if (p[i] != null && !p[i].equals(target.get(i))) {
						matched = false;
						break;
					}
				}
				return matched;
			}
		} else /* if (mode == MID)*/ {
			if (target.size() < p.length) {
				return false;
			} else {
				for (int i = 0; i < target.size() - p.length + 1; i++) {
					boolean matched = true;
					for (int l = 0; l < p.length; l++) {
						if (p[l] != null && !p[l].equals(target.get(i + l))) {
							matched = false;
							break;
						}
					}
					if (matched) return true;
				}
				return false;
			}
		}
	}

}
