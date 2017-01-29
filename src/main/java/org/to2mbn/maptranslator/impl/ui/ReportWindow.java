package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.UIUtils.reportException;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

class ReportWindow {

	public Stage stage;
	private WebView webview;
	private Button btnExport;

	public Consumer<String> gotoNodeListener;

	public ReportWindow(String report, String title) {
		stage = new Stage();
		stage.setTitle(title);
		webview = new WebView();
		btnExport = new Button(translate("report.export"));

		HBox btnPane = new HBox(btnExport);
		BorderPane rootPane = new BorderPane();
		rootPane.setCenter(webview);
		rootPane.setBottom(btnPane);
		stage.setScene(new Scene(rootPane));

		webview.getEngine().loadContent(report);

		btnExport.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle(translate("report.export"));
			chooser.setSelectedExtensionFilter(new ExtensionFilter("*.html", "*.html"));
			chooser.setInitialFileName(title + ".html");
			File target = chooser.showSaveDialog(stage);
			if (target == null) return;
			CompletableFuture
					.runAsync(() -> {
						try (Writer writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
							writer.write(report);
						} catch (IOException e) {
							e.printStackTrace();
						}
					})
					.exceptionally(reportException);
		});

		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN), () -> {
			String path = getSelectedNodePath();
			if (path != null) {
				gotoNodeListener.accept(path);
			}
		});
	}

	private String getSelectedNodePath() {
		String text = (String) webview.getEngine().executeScript("window.getSelection().toString()");
		if (text == null) return null;
		text = text.replace("\n", "").trim();
		if (text.isEmpty()) return null;
		return text;
	}

}
