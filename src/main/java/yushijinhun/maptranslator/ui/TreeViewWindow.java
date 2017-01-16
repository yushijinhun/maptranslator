package yushijinhun.maptranslator.ui;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import yushijinhun.maptranslator.tree.Node;
import yushijinhun.maptranslator.tree.TextNodeReplacer;
import yushijinhun.maptranslator.ui.TreeItemConstructor.XTreeCell;

class TreeViewWindow {

	Stage stage;
	TreeView<Node> tree;
	MenuItem menuGoInto = new MenuItem();
	MenuItem menuUpTo = new MenuItem();
	MenuItem menuShowIn = new MenuItem("在原文列表中显示");
	ContextMenu popupMenu = new ContextMenu(menuGoInto, menuUpTo, menuShowIn);
	IntegerProperty currentAppearance = new SimpleIntegerProperty();
	IntegerProperty totalAppearance = new SimpleIntegerProperty();
	ObjectProperty<List<String[]>> appearances = new SimpleObjectProperty<>();
	boolean loadingNode = false;

	Consumer<String> showIn;
	Predicate<String> isStringInList;
	Function<String[], CompletableFuture<Optional<Node>>> nodeLoader;

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

		tree.setContextMenu(popupMenu);
		popupMenu.setOnShowing(event -> {
			Node up = null;
			Node down = null;
			Node current = null;
			if (tree.getRoot() != null) {
				current = tree.getRoot().getValue();
				up = current.parent();
				down = Optional.ofNullable(tree.getSelectionModel().getSelectedItem()).map(item -> item.getValue()).orElse(null);
			}
			if (up == null) {
				menuUpTo.setText("返回到...");
				menuUpTo.setDisable(true);
			} else {
				menuUpTo.setText("返回到 " + up);
				menuUpTo.setDisable(false);
			}
			if (down == null) {
				menuGoInto.setText("进入到...");
				menuGoInto.setDisable(true);
			} else {
				menuGoInto.setText("进入到 " + down);
				menuGoInto.setDisable(down == current);
			}
			String text = null;
			if (down != null) {
				text = TextNodeReplacer.getText(down).orElse(null);
			}
			if (down == null || text == null) {
				menuShowIn.setDisable(true);
			} else {
				menuShowIn.setDisable(!isStringInList.test(text));
			}
			Node upNode = up;
			Node downNode = down;
			String nodeText = text;
			menuUpTo.setOnAction(e -> {
				if (upNode != null) {
					setRoot(upNode);
				}
			});
			menuGoInto.setOnAction(e -> {
				if (downNode != null) {
					setRoot(downNode);
				}
			});
			menuShowIn.setOnAction(e -> {
				if (nodeText != null) {
					showIn.accept(nodeText);
				}
			});
			popupMenu.getScene().getRoot().requestLayout();
		});
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> {
			switchAppearance(+1);
		});
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), () -> {
			switchAppearance(-1);
		});
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.SEMICOLON, KeyCombination.CONTROL_DOWN), () -> {
			appearances.set(null);
		});

		stage.getScene().getStylesheets().add("/yushijinhun/maptranslator/ui/TreeViewWindow.css");
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
			currentAppearance.set(1);
			totalAppearance.set(nodes.size());
			switchAppearance(0);
		}
	}

	Optional<Optional<Node>> tryLoadFromCurrent(String[] path) {
		if (tree.getRoot() != null) {
			Node root = tree.getRoot().getValue();
			while (root.parent() != null)
				root = root.parent();
			if (root.toString().equals(path[0])) {
				return Optional.of(root.resolve(path, 1));
			}
		}
		return Optional.empty();
	}

	void switchAppearance(int offset) {
		if (appearances.get() != null && !loadingNode) {
			int newIdx = currentAppearance.get() + offset;
			if (newIdx < 0) newIdx = totalAppearance.get() - 1;
			if (newIdx >= totalAppearance.get()) newIdx = 0;
			currentAppearance.set(newIdx);
			String[] path = appearances.get().get(newIdx);
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
	}

}
