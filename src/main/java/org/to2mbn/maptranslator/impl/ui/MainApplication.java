package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.UIUtils.reportException;
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
import java.nio.file.Path;
import java.util.ArrayList;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

class MainApplication {

	public static void main(String[] args) {
		new JFXPanel();
		Platform.runLater(() -> new MainApplication().start());
	}

	Path folder;
	MapHandler handler;
	StringDisplayWindow strDisWin;
	TranslateWindow traWin;
	TreeViewWindow treeWin;
	ProgressWindow progressWin;
	Map<String, List<String[]>> mapping;

	void start() {
		progressWin = new ProgressWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle(translate("load_save.title"));
		File selected = chooser.showDialog(null);
		if (selected == null) {
			new Alert(AlertType.ERROR, translate("load_save.no_chosen")).show();
			return;
		}
		folder = selected.toPath();
		showProgressWindow(false);
		MapHandler.create(folder)
				.thenAcceptAsync(createdHandler -> {
					hideProgressWindow();
					handler = createdHandler;
					initUI();
				}, Platform::runLater)
				.exceptionally(reportException);
	}

	void initUI() {
		progressWin.progressGetter = () -> ((double) handler.currentProgress()) / ((double) handler.totalProgress());
		strDisWin = new StringDisplayWindow();
		traWin = new TranslateWindow();
		treeWin = new TreeViewWindow();
		strDisWin.stage.setOnCloseRequest(event -> exit());
		traWin.stage.setOnCloseRequest(event -> exit());
		treeWin.stage.setOnCloseRequest(event -> exit());

		traWin.onAdded = strDisWin::onStringAddedToTranslate;
		traWin.onRemoved = strDisWin::onStringRemovedFromTranslate;
		traWin.onTextDbclick = strDisWin::jumpToString;
		strDisWin.onStringDbclick = traWin::tryAddEntry;
		strDisWin.isStringTranslated = traWin::isStringTranslated;
		treeWin.showIn = strDisWin::jumpToString;
		treeWin.isStringInList = strDisWin::stringExists;
		strDisWin.showIn = str -> {
			if (mapping.containsKey(str)) {
				treeWin.stage.requestFocus();
				treeWin.tree.requestFocus();
				treeWin.setAppearances(new ArrayList<>(mapping.get(str)));
			}
		};
		treeWin.nodeLoader = path -> {
			showProgressWindow(false);
			return ((path instanceof String)
					? handler.resolveNode((String) path)
					: handler.resolveNode((String[]) path))
							.thenApplyAsync(result -> {
								result.ifPresent(node -> {
									Node root = node;
									while (root.parent() != null)
										root = root.parent();
									TreeItemConstructor.construct(root);
								});
								hideProgressWindow();
								return result;
							}, Platform::runLater);
		};

		strDisWin.btnLoad.setOnAction(event -> {
			showProgressWindow(true);
			handler.excludes().clear();
			handler.excludes().addAll(strDisWin.getIgnores());
			handler.extractStrings()
					.thenAcceptAsync(param -> {
						mapping = param;
						strDisWin.setStrings(mapping.keySet());
						hideProgressWindow();
						treeWin.reload();
						showParseWarnings();
					}, Platform::runLater)
					.exceptionally(reportException);
		});

		traWin.btnExport.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle(translate("translate.export"));
			chooser.setSelectedExtensionFilter(new ExtensionFilter("*.json", "*.json"));
			File target = chooser.showSaveDialog(traWin.stage);
			if (target == null) return;
			showProgressWindow(false);
			Map<String, String> copy = traWin.toTranslateTable();
			CompletableFuture
					.runAsync(() -> {
						JSONObject json = new JSONObject(copy);
						try (Writer writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
							writer.write(json.toString());
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					})
					.handleAsync((result, err) -> {
						hideProgressWindow();
						if (err != null) {
							reportException(err);
						}
						return null;
					}, Platform::runLater);
		});

		traWin.btnImport.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle(translate("translate.import"));
			chooser.setSelectedExtensionFilter(new ExtensionFilter("*.json", "*.json"));
			File target = chooser.showOpenDialog(traWin.stage);
			if (target == null) return;
			showProgressWindow(false);
			CompletableFuture
					.supplyAsync(() -> {
						try (Reader reader = new InputStreamReader(new FileInputStream(target), "UTF-8")) {
							return new JSONObject(new JSONTokener(reader));
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					})
					.handleAsync((result, err) -> {
						hideProgressWindow();
						if (err == null) {
							for (String originStr : result.keySet()) {
								String targetStr = result.getString(originStr);
								traWin.importEntry(originStr, targetStr);
							}
						} else {
							reportException(err);
						}
						return null;
					}, Platform::runLater);
		});

		traWin.btnApply.setOnAction(event -> {
			showProgressWindow(true);
			handler.excludes().clear();
			handler.excludes().addAll(strDisWin.getIgnores());
			handler
					.replace(traWin.toTranslateTable())
					.handleAsync((result, err) -> {
						hideProgressWindow();
						if (err == null) {
							new Alert(AlertType.INFORMATION, translate("translate.apply.success.message")).show();
						} else {
							reportException(err);
						}
						return null;
					}, Platform::runLater);
		});

		strDisWin.stage.show();
		traWin.stage.show();
		treeWin.stage.show();
	}

	void showProgressWindow(boolean progress) {
		progressWin.show(progress);
	}

	void hideProgressWindow() {
		progressWin.hide();
	}

	void exit() {
		handler.close();
		traWin.stage.close();
		strDisWin.stage.close();
		treeWin.stage.close();
	}

	void showParseWarnings() {
		List<ParsingWarning> warnings = handler.lastParsingWarnings();
		if (!warnings.isEmpty()) {
			new ReportWindow(ParsingResultGenerator.warningsToHtml(warnings), translate("report.title")).stage.show();
		}
	}
}
