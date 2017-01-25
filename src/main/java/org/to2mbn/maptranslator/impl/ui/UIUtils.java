package org.to2mbn.maptranslator.impl.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

class UIUtils {

	static final Function<Throwable, Void> reportException = e -> {
		reportException(e);
		return null;
	};

	static void reportException(Throwable e) {
		e.printStackTrace();
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			Scene scene = alert.getDialogPane().getScene();
			Stage stage = (Stage) scene.getWindow();
			alert.setTitle("maptranslator");
			alert.setHeaderText("An exception occurred.");
			alert.setContentText(e.getClass().getName() + ": " + e.getMessage());
			TextArea textArea = new TextArea(throwableToString(e));
			textArea.setEditable(false);
			textArea.setWrapText(true);
			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			alert.getDialogPane().setExpandableContent(textArea);
			alert.getDialogPane().setExpanded(true);
			alert.getDialogPane().expandedProperty().addListener(dummy -> Platform.runLater(() -> {
				alert.getDialogPane().requestLayout();
				stage.sizeToScene();
			}));
			alert.show();
		});
	}

	static String throwableToString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		return writer.toString();
	}
}
