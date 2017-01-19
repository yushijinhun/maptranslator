package yushijinhun.maptranslator.ui;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import yushijinhun.maptranslator.internal.org.json.JSONObject;
import yushijinhun.maptranslator.internal.org.json.JSONTokener;
import yushijinhun.maptranslator.model.MapHandler;
import yushijinhun.maptranslator.model.ParsingWarning;

class MainApplication {

	public static void main(String[] args) {
		new JFXPanel();
		Platform.runLater(() -> new MainApplication().start());
	}

	File folder;
	MapHandler handler;
	StringDisplayWindow strDisWin;
	TranslateWindow traWin;
	TreeViewWindow treeWin;
	ProgressWindow progressWin;
	Map<String, List<String[]>> mapping;

	void start() {
		progressWin = new ProgressWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("选择要翻译的存档");
		folder = chooser.showDialog(null);
		if (folder == null) {
			new Alert(AlertType.ERROR, "没有选择存档！").show();
			return;
		}
		showProgressWindow(false);
		MapHandler.create(folder)
				.thenAcceptAsync(createdHandler -> {
					hideProgressWindow();
					handler = createdHandler;
					initUI();
				}, Platform::runLater);
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
			return handler.resolveNode(path)
					.thenApplyAsync(result -> {
						hideProgressWindow();
						return result;
					}, Platform::runLater);
		};

		strDisWin.btnLoad.setOnAction(event -> {
			showProgressWindow(true);
			handler.excludes().clear();
			handler.excludes().addAll(strDisWin.getIgnores());
			handler.extractStrings().thenAcceptAsync(param -> {
				mapping = param;
				strDisWin.setStrings(mapping.keySet());
				hideProgressWindow();
				showParseWarnings();
			}, Platform::runLater);
		});

		traWin.btnExport.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("导出");
			chooser.setSelectedExtensionFilter(new ExtensionFilter("*.json", "*.json"));
			File target = chooser.showSaveDialog(traWin.stage);
			if (target == null) return;
			showProgressWindow(false);
			Map<String, String> copy = traWin.toTranslateTable();
			CompletableFuture.runAsync(() -> {
				JSONObject json = new JSONObject(copy);
				try (Writer writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
					writer.write(json.toString());
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}).handleAsync((result, err) -> {
				hideProgressWindow();
				if (err != null) {
					err.printStackTrace();
					new Alert(AlertType.ERROR, "无法导出：" + err).show();
				}
				return null;
			}, Platform::runLater);
		});

		traWin.btnImport.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("导入");
			chooser.setSelectedExtensionFilter(new ExtensionFilter("*.json", "*.json"));
			File target = chooser.showOpenDialog(traWin.stage);
			if (target == null) return;
			showProgressWindow(false);
			CompletableFuture.supplyAsync(() -> {
				try (Reader reader = new InputStreamReader(new FileInputStream(target), "UTF-8")) {
					return new JSONObject(new JSONTokener(reader));
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}).handleAsync((result, err) -> {
				hideProgressWindow();
				if (err == null) {
					for (String originStr : result.keySet()) {
						String targetStr = result.getString(originStr);
						traWin.importEntry(originStr, targetStr);
					}
				} else {
					new Alert(AlertType.ERROR, "无法导入：" + err).show();
				}
				return null;
			}, Platform::runLater);
		});

		traWin.btnApply.setOnAction(event -> {
			showProgressWindow(true);
			handler.excludes().clear();
			handler.excludes().addAll(strDisWin.getIgnores());
			handler.replace(traWin.toTranslateTable())
					.handleAsync((result, err) -> {
						hideProgressWindow();
						if (err == null) {
							new Alert(AlertType.INFORMATION, "应用成功，您可能需要重新载入");
						} else {
							new Alert(AlertType.ERROR, "应用失败：" + err);
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
			new ReportWindow(ParsingResultGenerator.warningsToHtml(warnings), "解析错误报告").stage.show();
		}
	}

}
