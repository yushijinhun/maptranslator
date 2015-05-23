package yushijinhun.mczhconverter.core;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class NBTStringVisitorLookup implements NBTStringVisitor {

	protected Set<String> strs = Collections.synchronizedSet(new LinkedHashSet<String>());

	@Override
	public String visit(String str) {
		strs.add(str);
		return null;
	}

	public Set<String> getStrings() {
		return strs;
	}
}
