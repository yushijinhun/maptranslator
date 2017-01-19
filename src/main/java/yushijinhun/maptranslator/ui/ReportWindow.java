package yushijinhun.maptranslator.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.CompletableFuture;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

class ReportWindow {

	Stage stage;
	WebView webview;
	Button btnExport;
	String report;
	String title;

	ReportWindow(String report, String title) {
		this.report = report;
		this.title = title;
		stage = new Stage();
		stage.setTitle(title);
		webview = new WebView();
		btnExport = new Button("导出为HTML");

		HBox btnPane = new HBox(btnExport);
		BorderPane rootPane = new BorderPane();
		rootPane.setCenter(webview);
		rootPane.setBottom(btnPane);
		stage.setScene(new Scene(rootPane));

		webview.getEngine().loadContent(report);

		btnExport.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("导出为HTML");
			chooser.setSelectedExtensionFilter(new ExtensionFilter("*.html", "*.html"));
			chooser.setInitialFileName(title + ".html");
			File target = chooser.showSaveDialog(stage);
			if (target == null) return;
			CompletableFuture.runAsync(() -> {
				try (Writer writer = new OutputStreamWriter(new FileOutputStream(target), "UTF-8")) {
					writer.write(report);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		});
	}

}
