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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser.ExtensionFilter;
import yushijinhun.maptranslator.internal.org.json.JSONObject;
import yushijinhun.maptranslator.internal.org.json.JSONTokener;
import yushijinhun.maptranslator.model.MapHandler;
import yushijinhun.maptranslator.model.ParseWarning;
import yushijinhun.maptranslator.tree.Node;

class MainApplication {

	public static void main(String[] args) {
		new JFXPanel();
		Platform.runLater(() -> new MainApplication().start());
	}

	File folder;
	MapHandler handler;
	StringDisplayWindow strDisWin;
	TranslateWindow traWin;
	Map<String, Set<Node>> mapping;
	Stage progressStage;

	void createProgressStage() {
		progressStage = new Stage(StageStyle.UTILITY);
		progressStage.setScene(new Scene(new Label("处理中...")));
		progressStage.setWidth(160);
		progressStage.setHeight(60);
	}

	void start() {
		createProgressStage();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("选择要翻译的存档");
		folder = chooser.showDialog(null);
		if (folder == null) {
			new Alert(AlertType.ERROR, "没有选择存档！").show();
			return;
		}
		showProgressWindow();
		MapHandler.create(folder)
				.thenAcceptAsync(createdHandler -> {
					hideProgressWindow();
					handler = createdHandler;
					initUI();
				}, Platform::runLater);
	}

	void initUI() {
		strDisWin = new StringDisplayWindow();
		traWin = new TranslateWindow();
		strDisWin.stage.setOnCloseRequest(event -> exit());
		traWin.stage.setOnCloseRequest(event -> exit());

		traWin.onAdded = strDisWin::onStringAddedToTranslate;
		traWin.onRemoved = strDisWin::onStringRemovedFromTranslate;
		traWin.onTextDbclick = strDisWin::jumpToString;
		strDisWin.onStringDbclick = traWin::tryAddEntry;
		strDisWin.isStringTranslated = traWin::isStringTranslated;

		strDisWin.btnLoad.setOnAction(event -> {
			showProgressWindow();
			handler.excludes().clear();
			handler.excludes().addAll(strDisWin.getIgnores());
			handler.extractStrings().thenAcceptAsync(param -> {
				mapping = param;
				strDisWin.setStrings(mapping.keySet());
				hideProgressWindow();
			}, Platform::runLater);
		});

		traWin.btnExport.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("导出");
			chooser.setSelectedExtensionFilter(new ExtensionFilter("JSON key-value mapping", ".json"));
			File target = chooser.showSaveDialog(traWin.stage);
			if (target == null) return;
			showProgressWindow();
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
			chooser.setSelectedExtensionFilter(new ExtensionFilter("JSON key-value mapping", ".json"));
			File target = chooser.showOpenDialog(traWin.stage);
			if (target == null) return;
			showProgressWindow();
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
			showProgressWindow();
			handler.excludes().clear();
			handler.excludes().addAll(strDisWin.getIgnores());
			handler.replace(traWin.toTranslateTable())
					.thenCompose(dummy -> handler.save())
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

		List<ParseWarning> warnings = handler.parseWarnings();
		if (!warnings.isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING, "部分字符串将无法以原格式保存：");
			TextArea textArea = new TextArea(
					warnings.stream()
							.map(warning -> String.format("- %s\n+ %s\n", warning.origin, warning.current))
							.collect(Collectors.joining("\n")));
			textArea.setEditable(false);
			alert.getDialogPane().setExpandableContent(textArea);
			alert.getDialogPane().setExpanded(true);
			alert.getDialogPane().expandedProperty().addListener(dummy -> Platform.runLater(() -> {
				alert.getDialogPane().requestLayout();
				alert.getDialogPane().getScene().getWindow().sizeToScene();
			}));
			alert.show();
		}
	}

	void showProgressWindow() {
		progressStage.show();
	}

	void hideProgressWindow() {
		progressStage.hide();
	}

	void exit() {
		handler.close();
		traWin.stage.close();
		strDisWin.stage.close();
	}

}
