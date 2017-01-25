package org.to2mbn.maptranslator.ui;

import org.to2mbn.maptranslator.core.MinecraftRules;
import org.to2mbn.maptranslator.core.tree.ClauseNode;
import org.to2mbn.maptranslator.core.tree.DataStoreNode;
import org.to2mbn.maptranslator.core.tree.Node;
import org.to2mbn.maptranslator.core.tree.NodeArgument;
import org.to2mbn.maptranslator.core.tree.TextArgumentNode;
import org.to2mbn.maptranslator.json.parse.JSONArray;
import org.to2mbn.maptranslator.json.parse.JSONObject;
import org.to2mbn.maptranslator.json.tree.JsonNode;
import org.to2mbn.maptranslator.nbt.parse.NBT;
import org.to2mbn.maptranslator.nbt.parse.NBTByte;
import org.to2mbn.maptranslator.nbt.parse.NBTByteArray;
import org.to2mbn.maptranslator.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.nbt.parse.NBTDouble;
import org.to2mbn.maptranslator.nbt.parse.NBTFloat;
import org.to2mbn.maptranslator.nbt.parse.NBTInt;
import org.to2mbn.maptranslator.nbt.parse.NBTIntArray;
import org.to2mbn.maptranslator.nbt.parse.NBTList;
import org.to2mbn.maptranslator.nbt.parse.NBTLong;
import org.to2mbn.maptranslator.nbt.parse.NBTShort;
import org.to2mbn.maptranslator.nbt.parse.NBTString;
import org.to2mbn.maptranslator.nbt.tree.NBTNode;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TreeItemConstructor {

	static final Image img_boolean = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/boolean.png");
	static final Image img_byte_array = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/byte_array.png");
	static final Image img_byte = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/byte.png");
	static final Image img_compound = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/compound.png");
	static final Image img_double = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/double.png");
	static final Image img_float = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/float.png");
	static final Image img_int_array = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/int_array.png");
	static final Image img_int = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/int.png");
	static final Image img_list = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/list.png");
	static final Image img_long = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/long.png");
	static final Image img_short = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/short.png");
	static final Image img_string = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/string.png");
	static final Image img_file = new Image("/org/to2mbn/maptranslator/ui/icon/common/package-x-generic.png");
	static final Image img_argument = new Image("/org/to2mbn/maptranslator/ui/icon/common/curly-brackets.png");

	public static TreeItem<Node> construct(Node value) {
		TreeItem<Node> node = new TreeItem<>(value);

		Image icon = null;
		if (value instanceof NBTNode) {
			NBT nbt = ((NBTNode) value).nbt;
			if (nbt instanceof NBTByteArray) {
				icon = img_byte_array;
			} else if (nbt instanceof NBTByte) {
				icon = img_byte;
			} else if (nbt instanceof NBTCompound) {
				icon = img_compound;
			} else if (nbt instanceof NBTDouble) {
				icon = img_double;
			} else if (nbt instanceof NBTFloat) {
				icon = img_float;
			} else if (nbt instanceof NBTIntArray) {
				icon = img_int_array;
			} else if (nbt instanceof NBTInt) {
				icon = img_int;
			} else if (nbt instanceof NBTList) {
				icon = img_list;
			} else if (nbt instanceof NBTLong) {
				icon = img_long;
			} else if (nbt instanceof NBTShort) {
				icon = img_short;
			} else if (nbt instanceof NBTString) {
				icon = img_string;
			}
		} else if (value instanceof DataStoreNode) {
			icon = img_file;
		} else if (value instanceof ClauseNode || value instanceof TextArgumentNode) {
			icon = img_string;
		} else if (value instanceof JsonNode) {
			Object json = ((JsonNode) value).json;
			if (json instanceof JSONObject) {
				icon = img_compound;
			} else if (json instanceof JSONArray) {
				icon = img_list;
			} else {
				icon = img_string;
			}
		} else if (value instanceof NodeArgument) {
			icon = img_argument;
		}
		if (icon != null) {
			node.setGraphic(new ImageView(icon));
		}

		value.unmodifiableChildren().forEach(child -> node.getChildren().add(construct(child)));
		value.properties().put("javafx.treeitem", node);
		return node;
	}

	static class XTreeCell extends TreeCell<Node> {

		@Override
		protected void updateItem(Node item, boolean empty) {
			super.updateItem(item, empty);
			boolean translatable = false;
			if (!empty) {
				setText(item.getDisplayText());
				setGraphic(TreeItemConstructor.getItem(item).getGraphic());
				if (item.hasTag(MinecraftRules.translatable)) {
					translatable = true;
				}
				setTooltip(new Tooltip(item.tags().toString()));
			} else {
				setText(null);
				setGraphic(null);
				setTooltip(null);
			}
			if (translatable != getStyleClass().contains("translatable")) {
				if (translatable) {
					getStyleClass().add("translatable");
				} else {
					getStyleClass().remove("translatable");
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	static TreeItem<Node> getItem(Node node) {
		return (TreeItem<Node>) node.properties().get("javafx.treeitem");
	}

}
