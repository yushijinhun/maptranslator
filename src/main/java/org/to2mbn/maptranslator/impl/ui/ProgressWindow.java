package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class ProgressWindow {

	private static ProgressWindow progressWindow;

	public static void initWindow() {
		if (progressWindow != null) throw new IllegalStateException();
		progressWindow = new ProgressWindow();
	}

	public static void destoryWindow() {
		if (progressWindow != null) {
			progressWindow.stage.close();
			progressWindow = null;
		}
	}

	public static ProgressWindow progressWindow() {
		if (progressWindow == null) throw new IllegalStateException();
		return progressWindow;
	}

	private Stage stage;
	private DoubleProperty progress = new SimpleDoubleProperty();
	private BooleanProperty showProgress = new SimpleBooleanProperty();

	public volatile Supplier<Double> progressGetter;

	private Timer timer = new Timer(true);

	public ProgressWindow() {
		stage = new Stage(StageStyle.UTILITY);
		stage.setTitle(translate("progress.title"));
		Label lbl = new Label();
		lbl.textProperty().bind(Bindings.createStringBinding(() -> {
			if (!showProgress.get()) {
				return translate("progress.processing.message.no_progress");
			} else {
				return translate("progress.processing.message.with_progress", progress.get() * 100d);
			}
		}, progress, showProgress));
		stage.setScene(new Scene(lbl));
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (showProgress.get() && progressGetter != null) {
					Platform.runLater(() -> {
						progress.set(progressGetter.get());
					});
				}
			}
		}, 0, 100);
	}

	public void show(boolean progress) {
		showProgress.set(progress);
		stage.setWidth(150);
		stage.setHeight(50);
		stage.show();
	}

	public void hide() {
		showProgress.set(false);
		stage.hide();
	}

}
