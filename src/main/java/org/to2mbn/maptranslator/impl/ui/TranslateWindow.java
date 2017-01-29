package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.UIUtils.reportException;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.application.Platform;
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
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

class TranslateWindow {

	public static class TranslateEntry {

		String origin;
		SimpleStringProperty originProperty = new SimpleStringProperty();
		SimpleStringProperty targetProperty = new SimpleStringProperty("");

	}

	public Stage stage;
	private TableView<TranslateEntry> table;
	private ObservableList<TranslateEntry> entries = FXCollections.observableArrayList();
	private Button btnImport;
	private Button btnExport;
	private Button btnApply;
	private TableColumn<TranslateEntry, String> colOrigin;
	private TableColumn<TranslateEntry, String> colTarget;

	public Consumer<String> onTextDbclick;
	public Consumer<String> onAdded;
	public Consumer<String> onRemoved;
	public Supplier<CompletableFuture<Map<String, String>>> importer;
	public Consumer<Map<String, String>> exporter;
	public Consumer<Map<String, String>> applier;

	public TranslateWindow() {
		stage = new Stage();
		stage.setTitle(translate("translate.title"));
		btnImport = new Button(translate("translate.import"));
		btnExport = new Button(translate("translate.export"));
		btnApply = new Button(translate("translate.apply"));
		table = new TableView<>(entries);
		BorderPane rootPane = new BorderPane();
		rootPane.setCenter(table);
		rootPane.setBottom(new FlowPane(btnImport, btnExport, btnApply));
		stage.setScene(new Scene(rootPane));

		colOrigin = new TableColumn<>(translate("tranalate.origin"));
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

		colTarget = new TableColumn<>(translate("tranalate.translated"));
		colTarget.setCellValueFactory(entry -> entry.getValue().targetProperty);
		colTarget.setCellFactory(param -> new TextFieldTableCell<TranslateEntry, String>(new DefaultStringConverter()));
		table.getColumns().add(colOrigin);
		table.getColumns().add(colTarget);
		table.setEditable(true);

		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DELETE, KeyCombination.CONTROL_DOWN), () -> {
			if (!table.getSelectionModel().isEmpty()) {
				String origin = table.getSelectionModel().getSelectedItem().origin;
				entries.remove(table.getSelectionModel().getSelectedIndex());
				onRemoved.accept(origin);
			}
		});
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN), () -> {
			TranslateEntry entry = table.getSelectionModel().getSelectedItem();
			if (entry != null) {
				TranslateEditWindow.show(entry);
			}
		});

		btnApply.setOnAction(event -> applier.accept(toTranslateTable()));
		btnExport.setOnAction(event -> exporter.accept(toTranslateTable()));
		btnImport.setOnAction(event -> importer.get()
				.thenAcceptAsync(result -> {
					if (result != null) {
						result.forEach((k, v) -> importEntry(k, v));
					}
				}, Platform::runLater)
				.exceptionally(reportException));
	}

	private Map<String, String> toTranslateTable() {
		Map<String, String> result = new LinkedHashMap<>();
		entries.forEach(entry -> result.put(entry.origin, entry.targetProperty.get()));
		return result;
	}

	public void tryAddEntry(String origin) {
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

	private void importEntry(String origin, String target) {
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

	public boolean isStringTranslated(String str) {
		for (TranslateEntry entry : entries) {
			if (entry.origin.equals(str))
				return true;
		}
		return false;
	}

}
