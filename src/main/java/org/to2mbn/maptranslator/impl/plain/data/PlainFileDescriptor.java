package org.to2mbn.maptranslator.impl.plain.data;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.to2mbn.maptranslator.data.DataDescriptor;
import org.to2mbn.maptranslator.impl.plain.tree.PlainFileNode;
import org.to2mbn.maptranslator.impl.plain.tree.TextLineNode;
import org.to2mbn.maptranslator.tree.Node;

public class PlainFileDescriptor implements DataDescriptor {

	private final Path root;
	private final Path file;
	private Set<String> tags;

	public PlainFileDescriptor(Path root, Path file, String... tags) {
		this.root = root;
		this.file = file;
		this.tags = new LinkedHashSet<>();
		this.tags.addAll(Arrays.asList(tags));
	}

	@Override
	public String toString() {
		return root.relativize(file).toString();
	}

	@Override
	public Node read() throws UncheckedIOException {
		PlainFileNode root = new PlainFileNode();
		try {
			List<String> lines = Files.readAllLines(file);
			if (lines.get(lines.size() - 1).isEmpty()) {
				lines.remove(lines.size() - 1);
			}

			int lineNumber = 1;
			for (String line : lines) {
				root.addChild(new TextLineNode(line, lineNumber));
				lineNumber++;
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return root;
	}

	@Override
	public void write(Node node) throws UncheckedIOException {
		try (Writer writer = Files.newBufferedWriter(file)) {
			writer.write(((PlainFileNode) node).getStringValue());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public Set<String> getTags() {
		return tags;
	}

}
