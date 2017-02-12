package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.ProgressWindow.progressWindow;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.alert;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.reportException;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.to2mbn.maptranslator.model.MapHandler;
import org.to2mbn.maptranslator.model.ParsingWarning;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;

class MainApplication {

	public static void main(String[] args) {
		new JFXPanel();
		Platform.runLater(() -> new MainApplication().start());
	}

	private MapHandler handler;
	private OriginalTextsWindow originalTextsWindow;
	private TranslateWindow translateWindow;
	private NBTExplorerWindow nbtWindow;
	private Map<String, List<String[]>> appearancesMapping;

	private void start() {
		ProgressWindow.initWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle(translate("load_save.title"));
		File selected = chooser.showDialog(null);
		if (selected == null) {
			alert(AlertType.ERROR, "load_save.no_chosen");
			return;
		}
		progressWindow().show(false);
		MapHandler.create(selected.toPath())
				.thenAcceptAsync(createdHandler -> {
					progressWindow().hide();
					handler = createdHandler;
					initUI();
				}, Platform::runLater)
				.exceptionally(reportException);
	}

	private void initUI() {
		progressWindow().progressGetter = () -> ((double) handler.currentProgress()) / ((double) handler.totalProgress());
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
		nbtWindow.mapHandler = handler;

		originalTextsWindow.loader = () -> {
			progressWindow().show(true);
			handler.excludes().clear();
			handler.excludes().addAll(originalTextsWindow.getIgnores());
			return handler.extractStrings()
					.thenApplyAsync(param -> {
						appearancesMapping = param;
						progressWindow().hide();
						nbtWindow.reload();
						showParsingWarnings();
						return param.keySet();
					}, Platform::runLater);
		};

		translateWindow.applier = data -> {
			progressWindow().show(true);
			handler.excludes().clear();
			handler.excludes().addAll(originalTextsWindow.getIgnores());
			handler
					.replace(data)
					.handleAsync((result, err) -> {
						progressWindow().hide();
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
				nbtWindow.switchNode(path)
						.thenAcceptAsync(found -> {
							if (found) nbtWindow.stage.requestFocus();
						}, Platform::runLater);

			};
			reportWindow.stage.show();
		}
	}
}
