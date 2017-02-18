package org.to2mbn.maptranslator.impl.ui;

import static org.to2mbn.maptranslator.impl.ui.UIUtils.setCssClass;
import org.to2mbn.maptranslator.impl.json.parse.JSONArray;
import org.to2mbn.maptranslator.impl.json.parse.JSONObject;
import org.to2mbn.maptranslator.impl.json.tree.JsonNode;
import org.to2mbn.maptranslator.impl.model.ResolveFailedWarning;
import org.to2mbn.maptranslator.impl.nbt.parse.NBT;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTByte;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTByteArray;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTDouble;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTFloat;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTInt;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTIntArray;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTList;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTLong;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTShort;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTString;
import org.to2mbn.maptranslator.impl.nbt.tree.NBTNode;
import org.to2mbn.maptranslator.rules.RulesConstants;
import org.to2mbn.maptranslator.tree.ClauseNode;
import org.to2mbn.maptranslator.tree.CommandArgumentNode;
import org.to2mbn.maptranslator.tree.DataStoreNode;
import org.to2mbn.maptranslator.tree.Node;
import org.to2mbn.maptranslator.tree.TextArgumentNode;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

class NodeTreeCells {

	private static final Image img_byte_array = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/byte_array.png");
	private static final Image img_byte = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/byte.png");
	private static final Image img_compound = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/compound.png");
	private static final Image img_double = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/double.png");
	private static final Image img_float = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/float.png");
	private static final Image img_int_array = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/int_array.png");
	private static final Image img_int = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/int.png");
	private static final Image img_list = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/list.png");
	private static final Image img_long = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/long.png");
	private static final Image img_short = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/short.png");
	private static final Image img_string = new Image("/org/to2mbn/maptranslator/ui/icon/nbt/string.png");
	private static final Image img_file = new Image("/org/to2mbn/maptranslator/ui/icon/common/package-x-generic.png");
	private static final Image img_argument = new Image("/org/to2mbn/maptranslator/ui/icon/common/curly-brackets.png");

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
		} else if (value instanceof CommandArgumentNode) {
			icon = img_argument;
		}
		if (icon != null) {
			node.setGraphic(new ImageView(icon));
		}

		value.unmodifiableChildren().forEach(child -> node.getChildren().add(construct(child)));
		value.properties().put("javafx.treeitem", node);
		return node;
	}

	public static Callback<TreeView<Node>, TreeCell<Node>> cellFactory() {
		return param -> new NodeTreeCell();
	}

	private static class NodeTreeCell extends TreeCell<Node> {

		@Override
		protected void updateItem(Node item, boolean empty) {
			super.updateItem(item, empty);
			if (!empty) {
				setText(item.getDisplayText());
				setGraphic(NodeTreeCells.getItem(item).getGraphic());
				setCssClass(this, "translatable", item.hasTag(RulesConstants.translatable));
				setCssClass(this, "resolve-failed", item.properties().containsKey("resolve_failure.post"));
				setTooltip(new Tooltip(tooltipFor(item)));
			} else {
				setText(null);
				setGraphic(null);
				setCssClass(this, "translatable", false);
				setCssClass(this, "resolve-failed", false);
				setTooltip(null);
			}

		}

	}

	@SuppressWarnings("unchecked")
	public static TreeItem<Node> getItem(Node node) {
		return (TreeItem<Node>) node.properties().get("javafx.treeitem");
	}

	public static String tooltipFor(Node node) {
		StringBuilder sb = new StringBuilder();
		if (node.tags().isEmpty()) {
			sb.append("No tags");
		} else {
			sb.append("Tag(s): ");
			boolean first = true;
			for (String tag : node.tags()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(tag);
			}
		}
		ResolveFailedWarning failure = (ResolveFailedWarning) node.properties().get("resolve_failure.post");
		if (failure != null) {
			sb.append("\nError: ");
			if (failure.exception == null) {
				sb.append("(None)");
			} else {
				String message = failure.exception.getMessage();
				if (message == null) {
					sb.append(failure);
				} else {
					sb.append(message);
				}
			}
		}
		return sb.toString();
	}

}
