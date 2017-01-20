package yushijinhun.maptranslator.internal.org.json;

import java.lang.ref.WeakReference;

class _ParseMetadata {

	WeakReference<Object> expected;
	char quoter;

	_ParseMetadata(Object expected, char quoter) {
		this.expected = new WeakReference<Object>(expected);
		this.quoter = quoter;
	}

	boolean matches(Object obj) {
		Object exp = expected.get();
		return exp != null && exp.equals(obj);
	}

}
