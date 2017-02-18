package org.to2mbn.maptranslator.impl.ui;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.copyToClipboard;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.reportException;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.translateRaw;
import static org.to2mbn.maptranslator.impl.ui.ProgressWindow.progressWindow;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.to2mbn.maptranslator.impl.model.MapHandler;
import org.to2mbn.maptranslator.tree.Node;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

class NBTExplorerWindow {

	public Stage stage;
	private TreeView<Node> tree;
	private MenuItem menuGoInto = new MenuItem();
	private MenuItem menuUpTo = new MenuItem();
	private MenuItem menuShowInOriginalTexts = new MenuItem(translate("nbt_view.menu.show_in_strings"));
	private MenuItem menuCopyPath = new MenuItem(translate("nbt_view.menu.copy_path"));
	private MenuItem menuCopyValue = new MenuItem(translate("nbt_view.menu.copy_value"));
	private MenuItem menuEdit = new MenuItem(translate("nbt_view.menu.edit"));
	private ContextMenu popupMenu = new ContextMenu(menuGoInto, menuUpTo, menuShowInOriginalTexts, new SeparatorMenuItem(), menuCopyPath, menuCopyValue, new SeparatorMenuItem(), menuEdit);
	private IntegerProperty currentAppearance = new SimpleIntegerProperty();
	private IntegerProperty totalAppearance = new SimpleIntegerProperty();
	public ObjectProperty<List<String[]>> appearances = new SimpleObjectProperty<>();
	private boolean loadingNode = false;

	private ObjectProperty<Optional<Node>> rootNode = new SimpleObjectProperty<>(Optional.empty());
	private ObjectExpression<Optional<Node>> selectedNode;
	private ObjectExpression<Optional<Node>> rootParentNode;
	private BooleanExpression isNodeSelected;
	private ObjectExpression<Optional<String>> selectedText;

	public Consumer<String> showInOriginalTexts;
	public Predicate<String> isStringInList;
	public MapHandler mapHandler;

	public NBTExplorerWindow() {
		// UI
		stage = new Stage();
		stage.setTitle(translate("nbt_view.title"));
		tree = new TreeView<>();
		tree.setCellFactory(NodeTreeCells.cellFactory());

		BorderPane rootPane = new BorderPane();
		rootPane.setCenter(tree);
		stage.setScene(new Scene(rootPane));
		tree.setShowRoot(true);

		Label lblAppearanceCount = new Label();
		rootPane.setBottom(lblAppearanceCount);
		lblAppearanceCount.visibleProperty().bind(appearances.isNotNull());

		Label lblPath = new Label();
		rootPane.setTop(lblPath);

		// Databind
		tree.rootProperty().bind(Bindings.createObjectBinding(
				() -> rootNode.get().map(node -> NodeTreeCells.getItem(node)).orElse(null),
				rootNode));

		selectedNode = Bindings.createObjectBinding(
				() -> Optional.ofNullable(tree.getSelectionModel().getSelectedItem()).map(item -> item.getValue()),
				tree.getSelectionModel().selectedItemProperty());

		rootParentNode = Bindings.createObjectBinding(
				() -> rootNode.get().map(Node::parent),
				rootNode);

		isNodeSelected = Bindings.createBooleanBinding(
				() -> selectedNode.get().isPresent(),
				selectedNode);

		selectedText = Bindings.createObjectBinding(
				() -> selectedNode.get()
						.flatMap(Node::getText),
				selectedNode);

		lblAppearanceCount.textProperty().bind(Bindings.format(
				translateRaw("nbt_view.find.tip"),
				currentAppearance.add(1), totalAppearance));

		lblPath.textProperty().bind(Bindings.createStringBinding(
				() -> Optional.ofNullable(defaultIfNull(selectedNode.get().orElse(null), rootNode.get().orElse(null)))
						.map(Node::getPath)
						.orElse(translate("nbt_view.path.no_selected")),
				selectedNode, rootNode));

		menuUpTo.disableProperty().bind(Bindings.createBooleanBinding(
				() -> !rootParentNode.get().isPresent(),
				rootParentNode));

		menuUpTo.textProperty().bind(Bindings.createStringBinding(
				() -> rootParentNode.get()
						.map(node -> translate("nbt_view.menu.back.available", node))
						.orElse(translate("nbt_view.menu.back.invalid")),
				rootParentNode));

		menuGoInto.disableProperty().bind(Bindings.createBooleanBinding(
				() -> selectedNode.get()
						.map(node -> node == rootNode.get().orElse(null))
						.orElse(true),
				selectedNode, rootNode));

		menuGoInto.textProperty().bind(Bindings.createStringBinding(
				() -> selectedNode.get()
						.map(node -> translate("nbt_view.menu.enter.available", node))
						.orElse(translate("nbt_view.menu.enter.invalid")),
				selectedNode));

		menuCopyPath.disableProperty().bind(isNodeSelected.not());
		menuCopyValue.disableProperty().bind(isNodeSelected.not());

		menuShowInOriginalTexts.disableProperty().bind(Bindings.createBooleanBinding(
				() -> selectedText.get()
						.map(text -> !isStringInList.test(text))
						.orElse(true),
				selectedText));

		appearances.addListener(dummy -> {
			List<String[]> nodes = appearances.get();
			if (nodes != null) {
				currentAppearance.set(0);
				totalAppearance.set(nodes.size());
				switchAppearance(0);
			}
		});

		menuEdit.disableProperty().bind(Bindings.createBooleanBinding(
				() -> !selectedNode.get().map(node -> node.relatedTextNode().isPresent()).orElse(false),
				selectedNode));

		// Event Handling
		tree.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
				selectedNode.get().ifPresent(node -> {
					if (node.unmodifiableChildren().isEmpty()) {
						editNode();
					}
				});
			}
		});

		tree.setContextMenu(popupMenu);
		menuUpTo.setOnAction(e -> goUp());
		menuGoInto.setOnAction(e -> goInto());
		menuShowInOriginalTexts.setOnAction(e -> showInOriginalTexts());
		menuCopyPath.setOnAction(e -> copySelectedPath());
		menuCopyValue.setOnAction(e -> copySelectedValue());
		menuEdit.setOnAction(e -> editNode());

		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> switchAppearance(+1));
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), () -> switchAppearance(-1));
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.SEMICOLON, KeyCombination.CONTROL_DOWN), () -> appearances.set(null));
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN), this::showGoTo);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), this::copySelectedOrigin);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN), this::copySelectedValue);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN), this::copySelectedPath);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN), this::goUp);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN), this::goInto);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), this::showInOriginalTexts);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN), this::editNode);

		stage.getScene().getStylesheets().add("/org/to2mbn/maptranslator/ui/NBTExplorerWindow.css");
	}

	public void reload() {
		selectedNode.get().ifPresent(node -> loadAndSwitchNode(node.getPathArray()));
	}

	public void selectNode(Node node) {
		boolean inViewport = false;
		if (tree.getRoot() != null) {
			Node root = tree.getRoot().getValue();
			Node tmp = node;
			while (tmp != null) {
				if (tmp == root) {
					inViewport = true;
					break;
				}
				tmp = tmp.parent();
			}
		}
		if (!inViewport) {
			Node newRoot = node.parent();
			if (newRoot == null) newRoot = node;
			rootNode.set(Optional.of(newRoot));
		}
		tree.getSelectionModel().select(NodeTreeCells.getItem(node));
		int idx = tree.getSelectionModel().getSelectedIndex();
		tree.getTreeItem(idx).setExpanded(true);
		tree.scrollTo(idx);
	}

	private Optional<Optional<Node>> tryLoadFromCurrent(Object path) {
		if (tree.getRoot() != null && path instanceof String[]) {
			Node root = tree.getRoot().getValue();
			while (root.parent() != null)
				root = root.parent();
			String[] pathArray = (String[]) path;
			if (root.toString().equals(pathArray[0])) {
				return Optional.of(root.resolve(pathArray, 1));
			}
		}
		return Optional.empty();
	}

	public CompletableFuture<Boolean> switchNode(Object path) {
		Optional<Optional<Node>> tryCurrent = tryLoadFromCurrent(path);
		if (tryCurrent.isPresent()) {
			if (tryCurrent.get().isPresent()) {
				selectNode(tryCurrent.get().get());
				return CompletableFuture.completedFuture(true);
			} else {
				return CompletableFuture.completedFuture(false);
			}
		} else {
			return loadAndSwitchNode(path);
		}
	}

	public void editNode() {
		selectedNode.get().ifPresent(this::editNode);
	}

	public void editNode(Node node) {
		node.relatedTextNode().ifPresent(n -> NodeEditWindow.show(n, mapHandler, this::onNodeEdited));
	}

	private void onNodeEdited(Node oldRoot, String[] path) {
		rootNode.get().ifPresent(viewRoot -> {
			if (viewRoot.root() == oldRoot) {
				reload();
			}
		});
	}

	private CompletableFuture<Boolean> loadAndSwitchNode(Object path) {
		loadingNode = true;
		return loadNode(path)
				.thenApplyAsync(optional -> {
					loadingNode = false;
					optional.ifPresent(node -> selectNode(node));
					return optional.isPresent();
				}, Platform::runLater)
				.exceptionally(e -> {
					reportException(e);
					return false;
				});
	}

	private CompletableFuture<Optional<Node>> loadNode(Object path) {
		progressWindow().show(false);
		return ((path instanceof String)
				? mapHandler.resolveNode((String) path)
				: mapHandler.resolveNode((String[]) path))
						.thenApplyAsync(result -> {
							result.ifPresent(node -> NodeTreeCells.construct(node.root()));
							progressWindow().hide();
							return result;
						}, Platform::runLater);
	}

	private void switchAppearance(int offset) {
		if (appearances.get() != null && !loadingNode) {
			int newIdx = currentAppearance.get() + offset;
			if (newIdx < 0) newIdx = totalAppearance.get() - 1;
			if (newIdx >= totalAppearance.get()) newIdx = 0;
			currentAppearance.set(newIdx);
			String[] path = appearances.get().get(newIdx);
			switchNode(path);
		}
	}

	// Features
	private void showGoTo() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(translate("nbt_view.goto"));
		dialog.setHeaderText(translate("nbt_view.goto"));
		dialog.setContentText(translate("nbt_view.goto.message"));
		dialog.setOnHidden(event -> {
			String result = dialog.getResult();
			if (result != null && !result.trim().isEmpty()) switchNode(result);
		});
		dialog.show();
		dialog.setWidth(600);
		dialog.getDialogPane().getScene().getWindow().centerOnScreen();
	}

	private void copySelectedOrigin() {
		selectedNode.get().ifPresent(node -> {
			String origin = (String) node.properties().get("origin");
			if (origin != null) {
				copyToClipboard(origin);
			}
		});
	}

	private void copySelectedPath() {
		selectedNode.get().ifPresent(node -> copyToClipboard(node.getPath()));
	}

	private void copySelectedValue() {
		selectedNode.get().ifPresent(node -> copyToClipboard(node.getStringValue()));
	}

	private void goUp() {
		rootParentNode.get().ifPresent(node -> rootNode.set(Optional.of(node)));
	}

	private void goInto() {
		selectedNode.get().ifPresent(node -> rootNode.set(Optional.of(node)));
	}

	private void showInOriginalTexts() {
		selectedText.get().ifPresent(text -> showInOriginalTexts.accept(text));
	}

}
