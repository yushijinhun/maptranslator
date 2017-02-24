package org.to2mbn.maptranslator.impl.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Function;
import org.apache.commons.lang3.exception.ExceptionUtils;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

class UIUtils {

	public static final ResourceBundle bundle = ResourceBundle.getBundle("org.to2mbn.maptranslator.ui.lang");

	public static final void alert(AlertType type, String format, Object... args) {
		Alert alert = new Alert(type);
		alert.getDialogPane().setContent(new Label(translate(format, args)));
		alert.show();
	}

	public static final String translate(String format, Object... args) {
		try {
			return String.format(bundle.getString(format), args);
		} catch (MissingResourceException e) {
			e.printStackTrace();
			return format;
		}
	}

	public static final String translateRaw(String key) {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			e.printStackTrace();
			return key;
		}
	}

	public static final Function<Throwable, Void> reportException = e -> {
		reportException(e);
		return null;
	};

	public static void reportException(Throwable e) {
		e.printStackTrace();
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			Scene scene = alert.getDialogPane().getScene();
			Stage stage = (Stage) scene.getWindow();
			alert.setTitle("maptranslator");
			alert.setHeaderText("An exception occurred.");
			alert.setContentText(e.getClass().getName() + ": " + e.getMessage());
			TextArea textArea = new TextArea(ExceptionUtils.getStackTrace(e));
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

	public static void copyToClipboard(String string) {
		ClipboardContent content = new ClipboardContent();
		content.putString(string);
		Clipboard.getSystemClipboard().setContent(content);
	}

	public static void setCssClass(Node node, String cssClass, boolean enabled) {
		if (enabled && !node.getStyleClass().contains(cssClass)) {
			node.getStyleClass().add(cssClass);
		} else if (!enabled && node.getStyleClass().contains(cssClass)) {
			node.getStyleClass().removeAll(cssClass);
		}
	}
}
