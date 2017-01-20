package yushijinhun.maptranslator.ui;

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

	Stage stage;
	DoubleProperty progress = new SimpleDoubleProperty();
	BooleanProperty showProgress = new SimpleBooleanProperty();

	volatile Supplier<Double> progressGetter;

	Timer timer = new Timer(true);

	ProgressWindow() {
		stage = new Stage(StageStyle.UTILITY);
		stage.setTitle("进度");
		Label lbl = new Label();
		lbl.textProperty().bind(Bindings.createStringBinding(() -> {
			if (!showProgress.get()) {
				return "处理中...";
			} else {
				return String.format("处理中(%.1f%%)...", progress.get() * 100d);
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

	void show(boolean progress) {
		showProgress.set(progress);
		stage.setWidth(150);
		stage.setHeight(50);
		stage.show();
	}

	void hide() {
		showProgress.set(false);
		stage.hide();
	}

}
