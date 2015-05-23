package yushijinhun.mczhconverter.core;

public class NBTStringVisitorLookupUnicodeChar extends NBTStringVisitorLookup {

	@Override
	public String visit(String str) {
		for (char ch : str.toCharArray()) {
			if (ch > '\u00ef') {
				return super.visit(str);
			}
		}
		return null;
	}
}
