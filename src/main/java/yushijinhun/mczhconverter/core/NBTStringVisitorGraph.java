package yushijinhun.mczhconverter.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import org.json.JSONObject;
import yushijinhun.mczhconverter.filter.TraceFiltering;
import yushijinhun.mczhconverter.trace.CompoundChild;
import yushijinhun.mczhconverter.trace.ListChild;
import yushijinhun.mczhconverter.trace.NodeTrace;
import yushijinhun.mczhconverter.trace.RootTag;

public class NBTStringVisitorGraph implements NBTStringVisitor {

	private Map<String, Set<String>> g = new TreeMap<>();
	private Map<String, Set<String>> egdata = new HashMap<>();

	@Override
	public String visit(String str, Stack<NodeTrace> trace) {
		if (TraceFiltering.shouldIgnore(str, trace)) return null;
		printtrace(trace);
		synchronized (g) {
			if (!g.containsKey("ROOT")) g.put("ROOT", new TreeSet<String>());
			String prev = "ROOT";
			for (NodeTrace node : trace) {
				if (node instanceof CompoundChild) {
					String k = ((CompoundChild) node).key;
					if (k.startsWith("[") && k.endsWith("]")) {
						k = "[?,?]";
					}
					if (!g.containsKey(k)) g.put(k, new TreeSet<String>());
					g.get(prev).add(k);
					prev = k;
				}
				if (trace.peek() instanceof CompoundChild) {
					String k = ((CompoundChild) trace.peek()).key;
					String v = trace.peek().tag.toString();
					if (!egdata.containsKey(k)) egdata.put(k, new TreeSet<String>());
					egdata.get(k).add(v);
				}
			}
		}
		return null;

	}

	public void writeGraph() {
		printGraphTo("graph", false, true, true);
		printGraphTo("graph_full", true, true, true);
	}

	private void printGraphTo(String to, boolean includeEg, boolean autoconvert, boolean autoshow) {
		synchronized (g) {
			try (PrintWriter pw = new PrintWriter(to + ".dot")) {
				pw.println("digraph G {");
				if (includeEg) {
					for (Map.Entry<String, Set<String>> ety : egdata.entrySet()) {
						String u = ety.getKey();
						String qu = JSONObject.quote(u);
						qu = qu.substring(1, qu.length() - 1);
						pw.print("\"" + u + "\"[label=\"" + qu + "\\n");
						int count = 0;
						for (String d : ety.getValue()) {
							String quoted = JSONObject.quote(d);
							quoted = quoted.substring(1, quoted.length() - 1);
							pw.print("\\n");
							pw.print(quoted);
							if (++count > 4) break;
						}
						pw.println("\"];");
					}
				}
				for (Map.Entry<String, Set<String>> ety : g.entrySet()) {
					String u = ety.getKey();
					for (String v : ety.getValue()) {
						pw.println("\"" + u + "\" -> \"" + v + "\";");
					}
				}
				pw.println("}");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			if (autoconvert) {
				Runtime.getRuntime().exec("dot -Tsvg " + to + ".dot -o " + to + ".svg").waitFor();
			}
			if (autoshow) {
				Runtime.getRuntime().exec("firefox " + new File(to + ".svg").getAbsolutePath());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private PrintWriter tracelog;

	public NBTStringVisitorGraph() {
		try {
			tracelog = new PrintWriter("trace.log");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printtrace(Stack<NodeTrace> trace) {
		StringBuilder sb = new StringBuilder();
		for (NodeTrace node : trace) {
			if (node instanceof RootTag) {
				sb.append("ROOT");
			} else if (node instanceof CompoundChild) {
				sb.append("->").append(((CompoundChild) node).key);
			} else if (node instanceof ListChild) {
				sb.append("[").append(((ListChild) node).idx).append("]");
			}
		}
		sb.append("=").append(trace.peek().tag.toString());
		tracelog.println(sb);
		tracelog.flush();
	}

}
