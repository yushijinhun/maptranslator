package org.to2mbn.maptranslator.ui;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.to2mbn.maptranslator.tree.Node;
import org.to2mbn.maptranslator.tree.TextContext;
import org.to2mbn.maptranslator.ui.TreeItemConstructor.XTreeCell;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

class TreeViewWindow {

	Stage stage;
	TreeView<Node> tree;
	MenuItem menuGoInto = new MenuItem();
	MenuItem menuUpTo = new MenuItem();
	MenuItem menuShowIn = new MenuItem("在原文列表中显示");
	MenuItem menuCopyPath = new MenuItem("复制路径");
	MenuItem menuCopyValue = new MenuItem("复制值");
	ContextMenu popupMenu = new ContextMenu(menuGoInto, menuUpTo, menuShowIn, new SeparatorMenuItem(), menuCopyPath, menuCopyValue);
	IntegerProperty currentAppearance = new SimpleIntegerProperty();
	IntegerProperty totalAppearance = new SimpleIntegerProperty();
	ObjectProperty<List<String[]>> appearances = new SimpleObjectProperty<>();
	boolean loadingNode = false;

	Consumer<String> showIn;
	Predicate<String> isStringInList;
	Function<Object, CompletableFuture<Optional<Node>>> nodeLoader;

	TreeViewWindow() {
		stage = new Stage();
		stage.setTitle("NBT树浏览器");
		tree = new TreeView<>();
		tree.setCellFactory(param -> new XTreeCell());

		BorderPane rootPane = new BorderPane();
		rootPane.setCenter(tree);
		stage.setScene(new Scene(rootPane));
		tree.setShowRoot(true);

		Label lblAppearanceCount = new Label();
		lblAppearanceCount.textProperty().bind(Bindings.concat("第", currentAppearance.add(1), "个匹配，共", totalAppearance, "个 (Ctrl+N 下一个，Ctrl+P 上一个，Ctrl+; 清除)"));
		rootPane.setBottom(lblAppearanceCount);
		lblAppearanceCount.visibleProperty().bind(appearances.isNotNull());

		Label lblPath = new Label();
		lblPath.textProperty().bind(Bindings.createStringBinding(() -> {
			TreeItem<Node> item = tree.getSelectionModel().getSelectedItem();
			if (item == null) item = tree.getRoot();
			if (item != null) {
				return item.getValue().getPath();
			} else {
				return "没有选中节点";
			}
		}, tree.getSelectionModel().selectedItemProperty(), tree.rootProperty()));
		rootPane.setTop(lblPath);

		tree.rootProperty().addListener(dummy -> updateContextMenu());
		tree.getSelectionModel().selectedItemProperty().addListener(dummy -> updateContextMenu());
		updateContextMenu();

		tree.setContextMenu(popupMenu);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> switchAppearance(+1));
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), () -> switchAppearance(-1));
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.SEMICOLON, KeyCombination.CONTROL_DOWN), () -> appearances.set(null));
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN), () -> showGoTo());

		stage.getScene().getStylesheets().add("/org/to2mbn/maptranslator/ui/TreeViewWindow.css");
	}

	void copyToClipboard(String string) {
		ClipboardContent content = new ClipboardContent();
		content.putString(string);
		Clipboard.getSystemClipboard().setContent(content);
	}

	void setRoot(Node node) {
		tree.setRoot(TreeItemConstructor.getItem(node));
		tree.refresh();
	}

	void selectNode(Node node) {
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
			setRoot(newRoot);
		}
		tree.getSelectionModel().select(TreeItemConstructor.getItem(node));
		int idx = tree.getSelectionModel().getSelectedIndex();
		tree.getTreeItem(idx).setExpanded(true);
		tree.scrollTo(idx);
	}

	void setAppearances(List<String[]> nodes) {
		appearances.set(nodes);
		if (nodes != null) {
			currentAppearance.set(0);
			totalAppearance.set(nodes.size());
			switchAppearance(0);
		}
	}

	Optional<Optional<Node>> tryLoadFromCurrent(Object path) {
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

	void switchNode(Object path) {
		Optional<Optional<Node>> tryCurrent = tryLoadFromCurrent(path);
		if (tryCurrent.isPresent()) {
			tryCurrent.get().ifPresent(node -> selectNode(node));
		} else {
			loadingNode = true;
			nodeLoader.apply(path)
					.thenAcceptAsync(optional -> {
						loadingNode = false;
						optional.ifPresent(node -> selectNode(node));
					}, Platform::runLater);
		}
	}

	void switchAppearance(int offset) {
		if (appearances.get() != null && !loadingNode) {
			int newIdx = currentAppearance.get() + offset;
			if (newIdx < 0) newIdx = totalAppearance.get() - 1;
			if (newIdx >= totalAppearance.get()) newIdx = 0;
			currentAppearance.set(newIdx);
			String[] path = appearances.get().get(newIdx);
			switchNode(path);
		}
	}

	void showGoTo() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("切换节点");
		dialog.setHeaderText("切换节点");
		dialog.setContentText("要切换到的节点的路径：");
		dialog.setOnHidden(event -> {
			String result = dialog.getResult();
			if (result != null && !result.trim().isEmpty()) switchNode(result);
		});
		dialog.show();
		dialog.setWidth(600);
		dialog.getDialogPane().getScene().getWindow().centerOnScreen();
	}

	void updateContextMenu() {
		Node rootParent;
		Node selected;
		Node root;
		if (tree.getRoot() != null) {
			root = tree.getRoot().getValue();
			rootParent = root.parent();
			selected = Optional.ofNullable(tree.getSelectionModel().getSelectedItem()).map(item -> item.getValue()).orElse(null);
		} else {
			rootParent = selected = root = null;
		}
		if (rootParent == null) {
			menuUpTo.setText("返回到...");
			menuUpTo.setDisable(true);
		} else {
			menuUpTo.setText("返回到 " + rootParent);
			menuUpTo.setDisable(false);
		}
		if (selected == null) {
			menuGoInto.setText("进入到...");
			menuGoInto.setDisable(true);
		} else {
			menuGoInto.setText("进入到 " + selected);
			menuGoInto.setDisable(selected == root);
		}
		String text;
		if (selected != null) {
			text = TextContext.textFromNode(selected).orElse(null);
		} else {
			text = null;
		}
		if (selected == null || text == null) {
			menuShowIn.setDisable(true);
		} else {
			menuShowIn.setDisable(!isStringInList.test(text));
		}
		menuCopyPath.setDisable(selected == null);
		menuCopyValue.setDisable(selected == null);
		menuUpTo.setOnAction(e -> {
			if (rootParent != null) {
				setRoot(rootParent);
			}
		});
		menuGoInto.setOnAction(e -> {
			if (selected != null) {
				setRoot(selected);
			}
		});
		menuShowIn.setOnAction(e -> {
			if (text != null) {
				showIn.accept(text);
			}
		});
		menuCopyPath.setOnAction(e -> {
			if (selected != null) {
				copyToClipboard(selected.getPath());
			}
		});
		menuCopyValue.setOnAction(e -> {
			if (selected != null) {
				copyToClipboard(selected.getStringValue());
			}
		});
	}

}
