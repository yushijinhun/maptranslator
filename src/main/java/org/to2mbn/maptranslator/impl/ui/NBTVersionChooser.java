package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTVersion;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTVersionConfig;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class NBTVersionChooser extends Dialog<NBTVersionConfig> {

	private ComboBox<NBTVersion> boxInputVersion;
	private ComboBox<NBTVersion> boxOutputVersion;

	public NBTVersionChooser() {
		setTitle(translate("nbt_version_chooser.title"));
		setHeaderText(translate("nbt_version_chooser.header"));
		boxInputVersion = new ComboBox<>(FXCollections.observableArrayList(NBTVersion.values()));
		boxOutputVersion = new ComboBox<>(FXCollections.observableArrayList(NBTVersion.values()));
		GridPane grid = new GridPane();
		grid.add(new Label(translate("nbt_version_chooser.input_version")), 1, 1);
		grid.add(boxInputVersion, 2, 1);
		grid.add(new Label(translate("nbt_version_chooser.output_version")), 1, 2);
		grid.add(boxOutputVersion, 2, 2);
		getDialogPane().setContent(grid);
		getDialogPane().getButtonTypes().add(ButtonType.OK);
		getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		setResultConverter(button -> {
			if (button.getButtonData() == ButtonData.OK_DONE) {
				return new NBTVersionConfig(boxInputVersion.getValue(), boxOutputVersion.getValue());
			} else {
				return null;
			}
		});

		boxInputVersion.setValue(NBTVersion.defaultConfig.getInputVersion());
		boxOutputVersion.setValue(NBTVersion.defaultConfig.getOutputVersion());
	}

}
