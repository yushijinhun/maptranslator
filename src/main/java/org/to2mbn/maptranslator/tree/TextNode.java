package org.to2mbn.maptranslator.tree;

import java.util.Optional;
import java.util.function.Supplier;

public interface TextNode {

	Optional<String> getNodeText();
	Node replaceNodeText(Supplier<String> proxyTarget);

}
