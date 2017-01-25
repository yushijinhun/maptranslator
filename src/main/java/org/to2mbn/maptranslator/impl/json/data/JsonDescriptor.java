package org.to2mbn.maptranslator.impl.json.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.to2mbn.maptranslator.data.DataDescriptor;
import org.to2mbn.maptranslator.impl.json.process.JsonTreeConstructor;
import org.to2mbn.maptranslator.impl.json.tree.JsonRootNode;
import org.to2mbn.maptranslator.tree.Node;

public class JsonDescriptor implements DataDescriptor {

	private final Path root;
	private final Path file;
	private Set<String> tags;

	public JsonDescriptor(Path root, Path file, String... tags) {
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
		String str;
		try (InputStream in = Files.newInputStream(file)) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream((int) Files.size(file));
			byte[] buffer = new byte[8192];
			int read;
			while ((read = in.read(buffer)) != -1) {
				bout.write(buffer, 0, read);
			}
			str = bout.toString("UTF-8");
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		Node node = JsonTreeConstructor.constructJson(str);
		node.properties().put("json.to_string.algorithm", "format");
		return node;
	}

	@Override
	public void write(Node node) throws UncheckedIOException {
		try (Writer writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"))) {
			writer.write(((JsonRootNode) node).toArgumentString());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public Set<String> getTags() {
		return tags;
	}

}
