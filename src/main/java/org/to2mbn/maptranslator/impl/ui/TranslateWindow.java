package org.to2mbn.maptranslator.impl.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

class TranslateWindow {

	static class TranslateEntry {

		String origin;
		SimpleStringProperty originProperty = new SimpleStringProperty();
		SimpleStringProperty targetProperty = new SimpleStringProperty("");

	}

	Stage stage;
	TableView<TranslateEntry> table;
	ObservableList<TranslateEntry> entries = FXCollections.observableArrayList();
	Button btnImport;
	Button btnExport;
	Button btnApply;
	TableColumn<TranslateEntry, String> colOrigin;
	TableColumn<TranslateEntry, String> colTarget;

	Consumer<String> onTextDbclick;
	Consumer<String> onAdded;
	Consumer<String> onRemoved;

	TranslateWindow() {
		stage = new Stage();
		stage.setTitle("翻译对照");
		btnImport = new Button("导入");
		btnExport = new Button("导出");
		btnApply = new Button("应用");
		table = new TableView<>(entries);
		BorderPane rootPane = new BorderPane();
		rootPane.setCenter(table);
		rootPane.setBottom(new FlowPane(btnImport, btnExport, btnApply));
		stage.setScene(new Scene(rootPane));

		colOrigin = new TableColumn<>("原文");
		colOrigin.setEditable(false);
		colOrigin.setCellValueFactory(entry -> entry.getValue().originProperty);
		colOrigin.setCellFactory(param -> {
			@SuppressWarnings("unchecked")
			TableCell<TranslateEntry, String> cell = (TableCell<TranslateEntry, String>) TableColumn.DEFAULT_CELL_FACTORY.call(param);
			cell.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					TranslateEntry selected = table.getSelectionModel().getSelectedItem();
					if (selected != null)
						onTextDbclick.accept(selected.origin);
				}
			});
			return cell;
		});

		colTarget = new TableColumn<>("译文");
		colTarget.setCellValueFactory(entry -> entry.getValue().targetProperty);
		colTarget.setCellFactory(param -> new TextFieldTableCell<TranslateEntry, String>(new DefaultStringConverter()));
		table.getColumns().add(colOrigin);
		table.getColumns().add(colTarget);

		table.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.DELETE && !table.getSelectionModel().isEmpty()) {
				String origin = table.getSelectionModel().getSelectedItem().origin;
				entries.remove(table.getSelectionModel().getSelectedIndex());
				onRemoved.accept(origin);
			}
		});
		table.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
				TranslateEntry entry = table.getSelectionModel().getSelectedItem();
				if (entry != null) {
					TranslateEditWindow.show(entry);
				}
			}
		});
		table.setEditable(true);
	}

	Map<String, String> toTranslateTable() {
		Map<String, String> result = new LinkedHashMap<>();
		entries.forEach(entry -> result.put(entry.origin, entry.targetProperty.get()));
		return result;
	}

	void tryAddEntry(String origin) {
		for (TranslateEntry entry : entries) {
			if (entry.origin.equals(origin)) {
				table.getSelectionModel().select(entry);
				table.scrollTo(entry);
				return;
			}
		}

		TranslateEntry entry = new TranslateEntry();
		entry.origin = origin;
		entry.originProperty.set(origin);
		entry.targetProperty.set(origin);
		entries.add(entry);
		onAdded.accept(origin);
		stage.requestFocus();
		table.requestFocus();
		table.getSelectionModel().select(entry);
		table.scrollTo(table.getSelectionModel().getSelectedIndex());
		TranslateEditWindow.show(entry);
	}

	void importEntry(String origin, String target) {
		for (TranslateEntry entry : entries) {
			if (entry.origin.equals(origin)) {
				entry.targetProperty.set(target);
				return;
			}
		}

		TranslateEntry entry = new TranslateEntry();
		entry.origin = origin;
		entry.originProperty.set(origin);
		entry.targetProperty.set(target);
		entries.add(entry);
		onAdded.accept(origin);
	}

	boolean isStringTranslated(String str) {
		for (TranslateEntry entry : entries) {
			if (entry.origin.equals(str))
				return true;
		}
		return false;
	}

}
