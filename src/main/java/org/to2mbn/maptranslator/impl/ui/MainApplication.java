package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.UIUtils.reportException;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.alert;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.to2mbn.maptranslator.impl.json.parse.JSONObject;
import org.to2mbn.maptranslator.impl.json.parse.JSONTokener;
import org.to2mbn.maptranslator.model.MapHandler;
import org.to2mbn.maptranslator.model.ParsingWarning;
import org.to2mbn.maptranslator.tree.Node;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

class MainApplication {

	public static void main(String[] args) {
		new JFXPanel();
		Platform.runLater(() -> new MainApplication().start());
	}

	private MapHandler handler;
	private OriginalTextsWindow originalTextsWindow;
	private TranslateWindow translateWindow;
	private NBTExplorerWindow nbtWindow;
	private ProgressWindow progressWinow;
	private Map<String, List<String[]>> appearancesMapping;

	private void start() {
		progressWinow = new ProgressWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle(translate("load_save.title"));
		File selected = chooser.showDialog(null);
		if (selected == null) {
			alert(AlertType.ERROR, "load_save.no_chosen");
			return;
		}
		progressWinow.show(false);
		MapHandler.create(selected.toPath())
				.thenAcceptAsync(createdHandler -> {
					progressWinow.hide();
					handler = createdHandler;
					initUI();
				}, Platform::runLater)
				.exceptionally(reportException);
	}

	private void initUI() {
		progressWinow.progressGetter = () -> ((double) handler.currentProgress()) / ((double) handler.totalProgress());
		originalTextsWindow = new OriginalTextsWindow();
		translateWindow = new TranslateWindow();
		nbtWindow = new NBTExplorerWindow();
		originalTextsWindow.stage.setOnCloseRequest(event -> exit());
		translateWindow.stage.setOnCloseRequest(event -> exit());
		nbtWindow.stage.setOnCloseRequest(event -> exit());

		translateWindow.onAdded = originalTextsWindow::onStringAddedToTranslate;
		translateWindow.onRemoved = originalTextsWindow::onStringRemovedFromTranslate;
		translateWindow.onTextDbclick = originalTextsWindow::jumpToString;
		originalTextsWindow.onStringDbclick = translateWindow::tryAddEntry;
		originalTextsWindow.isStringTranslated = translateWindow::isStringTranslated;
		nbtWindow.showInOriginalTexts = originalTextsWindow::jumpToString;
		nbtWindow.isStringInList = originalTextsWindow::stringExists;
		originalTextsWindow.showSelectedInNBTExplorer = str -> {
			if (appearancesMapping.containsKey(str)) {
				nbtWindow.stage.requestFocus();
				nbtWindow.appearances.set(new ArrayList<>(appearancesMapping.get(str)));
			}
		};
		nbtWindow.nodeLoader = path -> {
			progressWinow.show(false);
			return ((path instanceof String)
					? handler.resolveNode((String) path)
					: handler.resolveNode((String[]) path))
							.thenApplyAsync(result -> {
								result.ifPresent(node -> {
									Node root = node;
									while (root.parent() != null)
										root = root.parent();
									NodeTreeCells.construct(root);
								});
								progressWinow.hide();
								return result;
							}, Platform::runLater);
		};

		originalTextsWindow.loader = () -> {
			progressWinow.show(true);
			handler.excludes().clear();
			handler.excludes().addAll(originalTextsWindow.getIgnores());
			return handler.extractStrings()
					.thenApplyAsync(param -> {
						appearancesMapping = param;
						progressWinow.hide();
						nbtWindow.reload();
						showParsingWarnings();
						return param.keySet();
					}, Platform::runLater);
		};

		translateWindow.exporter = data -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle(translate("translate.export"));
			chooser.setSelectedExtensionFilter(new ExtensionFilter("*.json", "*.json"));
			File target = chooser.showSaveDialog(translateWindow.stage);
			if (target == null) return;
			progressWinow.show(false);
			CompletableFuture
					.runAsync(() -> {
						JSONObject json = new JSONObject(data);
						try (Writer writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
							writer.write(json.toString());
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					})
					.handleAsync((result, err) -> {
						progressWinow.hide();
						if (err != null) {
							reportException(err);
						}
						return null;
					}, Platform::runLater);
		};

		translateWindow.importer = () -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle(translate("translate.import"));
			chooser.setSelectedExtensionFilter(new ExtensionFilter("*.json", "*.json"));
			File target = chooser.showOpenDialog(translateWindow.stage);
			if (target == null) return CompletableFuture.completedFuture(null);
			progressWinow.show(false);
			return CompletableFuture
					.supplyAsync(() -> {
						try (Reader reader = new InputStreamReader(new FileInputStream(target), "UTF-8")) {
							return new JSONObject(new JSONTokener(reader));
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					})
					.handleAsync((result, err) -> {
						progressWinow.hide();
						if (err == null) {
							Map<String, String> mapping = new LinkedHashMap<>();
							for (String key : result.keySet()) {
								mapping.put(key, result.getString(key));
							}
							return mapping;
						} else {
							reportException(err);
							return null;
						}
					}, Platform::runLater);
		};

		translateWindow.applier = data -> {
			progressWinow.show(true);
			handler.excludes().clear();
			handler.excludes().addAll(originalTextsWindow.getIgnores());
			handler
					.replace(data)
					.handleAsync((result, err) -> {
						progressWinow.hide();
						if (err == null) {
							alert(AlertType.INFORMATION, "translate.apply.success.message");
						} else {
							reportException(err);
						}
						return null;
					}, Platform::runLater);
		};

		originalTextsWindow.stage.show();
		translateWindow.stage.show();
		nbtWindow.stage.show();
	}

	private void exit() {
		handler.close();
		translateWindow.stage.close();
		originalTextsWindow.stage.close();
		nbtWindow.stage.close();
	}

	private void showParsingWarnings() {
		List<ParsingWarning> warnings = handler.lastParsingWarnings();
		if (!warnings.isEmpty()) {
			ReportWindow reportWindow = new ReportWindow(ParsingResultGenerator.warningsToHtml(warnings), translate("report.title"));
			reportWindow.gotoNodeListener = path -> {
				nbtWindow.switchNode(path);
				nbtWindow.stage.requestFocus();
			};
			reportWindow.stage.show();
		}
	}
}
