package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import org.to2mbn.maptranslator.impl.ui.TranslateWindow.TranslateEntry;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class TranslateEditWindow {

	static void show(TranslateEntry entry) {
		TranslateEditWindow win = new TranslateEditWindow(entry);
		win.stage.show();
		win.stage.requestFocus();
		win.txt.requestFocus();
	}

	Stage stage;
	TextArea txt;
	Button btnRestore;
	Button btnOk;
	TranslateEntry entry;

	TranslateEditWindow(TranslateEntry entry) {
		stage = new Stage(StageStyle.UTILITY);
		stage.setTitle(translate("translate_edit.title"));
		txt = new TextArea(entry.targetProperty.get());
		btnRestore = new Button(translate("translate_edit.reset"));
		btnOk = new Button(translate("translate_edit.submit"));
		this.entry = entry;

		HBox btnBox = new HBox(btnRestore, btnOk);
		BorderPane rootPane = new BorderPane();
		rootPane.setCenter(txt);
		rootPane.setBottom(btnBox);
		stage.setScene(new Scene(rootPane));

		stage.getScene().addMnemonic(new Mnemonic(btnOk, new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN)));

		btnRestore.setOnAction(event -> restore());
		btnOk.setOnAction(event -> saveAndClose());
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN), this::saveAndClose);
		stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), this::restore);
	}

	void saveAndClose() {
		entry.targetProperty.set(txt.getText());
		stage.close();
	}

	void restore() {
		txt.setText(entry.origin);
	}
}
