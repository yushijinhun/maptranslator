package yushijinhun.maptranslator.tree;

import yushijinhun.maptranslator.internal.org.json.JSONArray;
import yushijinhun.maptranslator.internal.org.json.JSONException;
import yushijinhun.maptranslator.internal.org.json.JSONObject;
import yushijinhun.maptranslator.nbt.JsonNBTConverter;
import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTCompound;
import yushijinhun.maptranslator.nbt.NBTList;

public final class TreeConstructor {

	private TreeConstructor() {}

	public static NBTRootNode constructNBT(String nbt) {
		return construct(JsonNBTConverter.getTagFromJson(nbt));
	}

	public static JsonRootNode constructJson(String json) {
		try {
			return construct(new JSONObject(json));
		} catch (JSONException e) {
			throw new ArgumentParseException(e);
		}
	}

	public static NBTRootNode construct(NBT nbt) {
		NBTRootNode root = new NBTRootNode(nbt);
		constructSubtree(root);
		return root;
	}

	public static JsonRootNode construct(JSONObject json) {
		JsonRootNode root = new JsonRootNode(json);
		constructSubtree(root);
		return root;
	}

	public static void constructSubtree(NBTNode node) {
		NBT nbt = node.nbt;
		if (nbt instanceof NBTCompound) {
			NBTCompound casted = ((NBTCompound) nbt);
			casted.tags().forEach((key, childnbt) -> {
				NBTMapNode child = new NBTMapNode(childnbt, key);
				constructSubtree(child);
				node.addChild(child);
			});
		} else if (nbt instanceof NBTList) {
			NBTList casted = (NBTList) nbt;
			for (int i = 0; i < casted.size(); i++) {
				NBTListNode child = new NBTListNode(casted.get(i), i);
				constructSubtree(child);
				node.addChild(child);
			}
		}
	}

	public static void constructSubtree(JsonNode node) {
		Object json = node.json;
		if (json instanceof JSONObject) {
			JSONObject casted = (JSONObject) json;
			for (String key : casted.keySet()) {
				JsonMapNode child = new JsonMapNode(casted.get(key), key);
				constructSubtree(child);
				node.addChild(child);
			}
		} else if (json instanceof JSONArray) {
			JSONArray casted = (JSONArray) json;
			for (int i = 0; i < casted.length(); i++) {
				JsonListNode child = new JsonListNode(casted.get(i), i);
				constructSubtree(child);
				node.addChild(child);
			}
		}
	}

}
