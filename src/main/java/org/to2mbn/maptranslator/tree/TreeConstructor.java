package org.to2mbn.maptranslator.tree;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.to2mbn.maptranslator.internal.org.json.JSONArray;
import org.to2mbn.maptranslator.internal.org.json.JSONException;
import org.to2mbn.maptranslator.internal.org.json.JSONObject;
import org.to2mbn.maptranslator.nbt.JsonNBTConverter;
import org.to2mbn.maptranslator.nbt.NBT;
import org.to2mbn.maptranslator.nbt.NBTCompound;
import org.to2mbn.maptranslator.nbt.NBTList;

public final class TreeConstructor {

	private static final boolean _TO_STRING_VERIFY = true;

	private static final Logger LOGGER = Logger.getLogger(TreeConstructor.class.getCanonicalName());

	private static NBTCompound parseNBT(String nbt) {
		NBTCompound parsed = JsonNBTConverter.getTagFromJson(nbt);
		if (_TO_STRING_VERIFY) {
			try {
				String serialized = parsed.toString();
				if (!parsed.equals(JsonNBTConverter.getTagFromJson(serialized))) {
					throw new IllegalStateException("Object mismatch, serialized: " + serialized);
				}
			} catch (Exception e) {
				throwParsingVerifyFailedException("nbt", nbt, e);
			}
		}
		return parsed;
	}

	private static JSONArray parseJsonArray(String json) {
		JSONArray parsed = new JSONArray(json);
		if (_TO_STRING_VERIFY) {
			try {
				String serialized = parsed.toString();
				if (!parsed.equals(new JSONArray(serialized))) {
					throw new IllegalStateException("Object mismatch, serialized: " + serialized);
				}
			} catch (Exception e) {
				throwParsingVerifyFailedException("json array", json, e);
			}
		}
		return parsed;
	}

	private static JSONObject parseJsonObject(String json) {
		JSONObject parsed = new JSONObject(json);
		if (_TO_STRING_VERIFY) {
			try {
				String serialized = parsed.toString();
				if (!parsed.equals(new JSONObject(serialized))) {
					throw new IllegalStateException("Object mismatch, serialized: " + serialized);
				}
			} catch (Exception e) {
				throwParsingVerifyFailedException("json object", json, e);
			}
		}
		return parsed;
	}

	private static void throwParsingVerifyFailedException(String type, String origin, Throwable cause) {
		String msg = "*** Parsing verify failed (" + type + "): " + origin;
		LOGGER.log(Level.WARNING, msg, cause);
		throw new ArgumentParseException(msg, cause);
	}

	private TreeConstructor() {}

	public static NBTRootNode constructNBT(String nbt) {
		return construct(parseNBT(nbt));
	}

	public static JsonRootNode constructJson(String json) {
		try {
			return construct(parseJsonObject(json));
		} catch (JSONException e) {
			try {
				return construct(parseJsonArray(json));
			} catch (JSONException e1) {
				e.addSuppressed(e1);
				throw new ArgumentParseException(json, e);
			}

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

	public static JsonRootNode construct(JSONArray json) {
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
