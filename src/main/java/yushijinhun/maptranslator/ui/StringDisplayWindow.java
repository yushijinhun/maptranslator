package yushijinhun.maptranslator.ui;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

class StringDisplayWindow {

	static final String DEFAULT_IGNORE = "@";

	Stage stage;
	ListView<String> list;
	ObservableList<String> strings = FXCollections.observableArrayList();
	TextArea txtIgnore;
	Button btnLoad;

	Map<String, ListCell<String>> cellsMapping = new WeakHashMap<>();

	Consumer<String> onStringDbclick;
	Predicate<String> isStringTranslated;

	StringDisplayWindow() {
		stage = new Stage();
		stage.setTitle("原文列表");
		list = new ListView<>(strings);
		txtIgnore = new TextArea(DEFAULT_IGNORE);
		btnLoad = new Button("读取");

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
		scene.getStylesheets().add("/yushijinhun/maptranslator/ui/StringDisplayWindow.css");

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
		list.getSelectionModel().select(origin);
		list.scrollTo(origin);
	}

	void setStrings(Set<String> newstrings) {
		strings.setAll(newstrings);
	}

}
