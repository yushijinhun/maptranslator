package yushijinhun.mczhconverter.core;

import java.util.Map;

public class NBTStringVisitorApply implements NBTStringVisitor {

	private Map<String, String> patch;

	public NBTStringVisitorApply(Map<String, String> patch) {
		this.patch = patch;
	}

	@Override
	public String visit(String str) {
		String result = patch.get(str);
		if (result != null) {
			System.out.println();
		}
		return result;
	}

}
