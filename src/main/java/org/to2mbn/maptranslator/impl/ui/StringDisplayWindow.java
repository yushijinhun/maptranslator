package org.to2mbn.maptranslator.impl.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

class StringDisplayWindow {

	static final String DEFAULT_IGNORE = "@";

	Stage stage;
	ListView<String> list;
	ObservableList<String> strings = FXCollections.observableArrayList();
	TextArea txtIgnore;
	Button btnLoad;
	Set<String> stringsSet = new HashSet<>();
	Map<String, ListCell<String>> cellsMapping = new WeakHashMap<>();
	MenuItem menuShowIn = new MenuItem("查找出现");
	ContextMenu popupMenu = new ContextMenu(menuShowIn);
	TextField txtFilter;

	Runnable showFilter;
	Runnable hideFilter;

	Consumer<String> onStringDbclick;
	Predicate<String> isStringTranslated;
	Consumer<String> showIn;

	StringDisplayWindow() {
		stage = new Stage();
		stage.setTitle("原文列表");
		list = new ListView<>();
		txtIgnore = new TextArea(DEFAULT_IGNORE);
		btnLoad = new Button("读取");
		txtFilter = new TextField();

		BorderPane ignorePane = new BorderPane();
		ignorePane.setCenter(txtIgnore);
		ignorePane.setTop(new Label("忽略的字符串(Regex)："));

		BorderPane underPane = new BorderPane();
		underPane.setCenter(ignorePane);
		underPane.setRight(btnLoad);
		underPane.setMaxHeight(360);

		BorderPane rootPane = new BorderPane();
		rootPane.setCenter(list);
		rootPane.setBottom(underPane);

		Scene scene = new Scene(rootPane);
		stage.setScene(scene);
		scene.getStylesheets().add("/org/to2mbn/maptranslator/ui/StringDisplayWindow.css");

		showFilter = () -> {
			rootPane.setTop(txtFilter);
			txtFilter.requestFocus();
		};
		hideFilter = () -> {
			if (!txtFilter.getText().isEmpty())
				txtFilter.setText(null);
			rootPane.setTop(null);
		};

		list.setCellFactory(param -> new ListCell<String>() {

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(item);
				cellsMapping.entrySet().stream()
						.filter(entry -> entry.getValue() == this)
						.findFirst()
						.ifPresent(entry -> cellsMapping.remove(entry.getKey()));
				if (!empty) {
					cellsMapping.put(item, this);
					if (isStringTranslated.test(item)) {
						if (!getStyleClass().contains("translated"))
							getStyleClass().add("translated");
					} else {
						if (getStyleClass().contains("translated"))
							getStyleClass().remove("translated");
					}
				}
				if (empty && getStyleClass().contains("translated"))
					getStyleClass().remove("translated");
			}
		});

		list.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				String selected = list.getSelectionModel().getSelectedItem();
				if (selected != null) onStringDbclick.accept(selected);
			}
		});
		list.setContextMenu(popupMenu);
		popupMenu.setOnShowing(event -> menuShowIn.setDisable(list.getSelectionModel().isEmpty()));
		menuShowIn.setOnAction(event -> {
			String str = list.getSelectionModel().getSelectedItem();
			if (str != null) showIn.accept(str);
		});

		list.itemsProperty().bind(Bindings.createObjectBinding(() -> {
			if (txtFilter.getText().isEmpty()) {
				txtFilter.getStyleClass().remove("errorregex");
				return strings;
			}
			ObservableList<String> result = FXCollections.observableArrayList();
			Pattern pattern;
			try {
				pattern = Pattern.compile(txtFilter.getText());
			} catch (PatternSyntaxException e) {
				if (!txtFilter.getStyleClass().contains("errorregex")) {
					txtFilter.getStyleClass().add("errorregex");
				}
				return result;
			}
			txtFilter.getStyleClass().remove("errorregex");
			strings.forEach(str -> {
				if (pattern.matcher(str).find())
					result.add(str);
			});
			return result;
		}, strings, txtFilter.textProperty()));
		txtFilter.focusedProperty().addListener((dummy, oldVal, newVal) -> {
			if (!newVal && txtFilter.getText().isEmpty())
				hideFilter.run();
		});
		txtFilter.addEventHandler(KeyEvent.KEY_TYPED, event -> {
			if (event.getCode() == KeyCode.ESCAPE)
				hideFilter.run();
		});
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), showFilter);
	}

	void onStringAddedToTranslate(String origin) {
		Optional.ofNullable(cellsMapping.get(origin))
				.ifPresent(cell -> {
					if (!cell.getStyleClass().contains("translated"))
						cell.getStyleClass().add("translated");
				});
	}

	void onStringRemovedFromTranslate(String origin) {
		Optional.ofNullable(cellsMapping.get(origin))
				.ifPresent(cell -> {
					if (cell.getStyleClass().contains("translated"))
						cell.getStyleClass().remove("translated");
				});
	}

	List<String> getIgnores() {
		return Stream.of(txtIgnore.getText().split("\n"))
				.filter(line -> !line.trim().isEmpty())
				.collect(Collectors.toList());
	}

	void jumpToString(String origin) {
		if (!txtFilter.getText().isEmpty() && !list.getItems().contains(origin)) {
			hideFilter.run();
		}
		stage.requestFocus();
		list.requestFocus();
		list.getSelectionModel().select(origin);
		list.scrollTo(origin);
	}

	void setStrings(Set<String> newstrings) {
		strings.setAll(newstrings);
		stringsSet = newstrings;
	}

	boolean stringExists(String str) {
		return stringsSet.contains(str);
	}

}
