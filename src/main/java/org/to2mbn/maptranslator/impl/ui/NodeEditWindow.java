package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.ProgressWindow.progressWindow;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.reportException;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.to2mbn.maptranslator.model.MapHandler;
import org.to2mbn.maptranslator.process.IteratorArgument;
import org.to2mbn.maptranslator.process.NodeReplacer;
import org.to2mbn.maptranslator.process.TreeIterator;
import org.to2mbn.maptranslator.tree.Node;
import org.to2mbn.maptranslator.tree.TextNode;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NodeEditWindow {

	public static void show(Node node, MapHandler handler, BiConsumer<Node, String[]> changeListener) {
		if (!(node instanceof TextNode)) throw new IllegalArgumentException("Not a TextNode: " + node);
		NodeEditWindow window = new NodeEditWindow(node);
		window.nodeSaver = handler::saveNode;
		window.nodeLoader = handler::resolveNode;
		window.changeListener = changeListener;
		window.stage.show();
		window.stage.requestFocus();
		window.txt.requestFocus();
	}

	private Stage stage;
	private TextArea txt;
	private Button btnOk;
	private Button btnRestore;
	private Node node;
	private BiConsumer<Node, String[]> changeListener; // old root node, node path
	private Function<String[], CompletableFuture<Optional<Node>>> nodeLoader;
	private Function<Node, CompletableFuture<Void>> nodeSaver;

	private NodeEditWindow(Node node) {
		this.node = node;
		stage = new Stage(StageStyle.UTILITY);
		stage.setTitle(translate("node_edit.title"));
		txt = new TextArea(node.getText().get());
		btnRestore = new Button(translate("node_edit.reset"));
		btnOk = new Button(translate("node_edit.submit"));

		HBox btnBox = new HBox(btnRestore, btnOk);
		BorderPane rootPane = new BorderPane();
		rootPane.setTop(new Label(node.getPath()));
		rootPane.setCenter(txt);
		rootPane.setBottom(btnBox);
		stage.setScene(new Scene(rootPane));

		stage.getScene().addMnemonic(new Mnemonic(btnOk, new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN)));

		btnRestore.setOnAction(event -> restore());
		btnOk.setOnAction(event -> saveAndClose());
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN), this::saveAndClose);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), this::restore);
	}

	private void saveAndClose() {
		progressWindow().show(false);
		stage.close();
		String newText = txt.getText();
		nodeLoader.apply(node.getPathArray())
				.thenApply(optional -> optional.flatMap(Node::relatedTextNode).orElseThrow(() -> new IllegalStateException("Couldn't lookup node: " + node.getPath())))
				.thenComposeAsync(newNode -> {
					IteratorArgument arg = new IteratorArgument();
					arg.replacers.add(new NodeReplacer(
							nodeToMatch -> nodeToMatch == newNode,
							nodeToModify -> ((TextNode) nodeToModify).replaceNodeText(() -> newText)));
					Node root = newNode.root();
					TreeIterator.iterate(arg, root);
					return nodeSaver.apply(root);
				})
				.handleAsync((dummy, err) -> {
					progressWindow().hide();
					if (err == null) {
						changeListener.accept(node.root(), node.getPathArray());
					} else {
						reportException(err);
					}
					return null;
				}, Platform::runLater);
	}

	private void restore() {
		txt.setText(node.getText().get());
	}

}
