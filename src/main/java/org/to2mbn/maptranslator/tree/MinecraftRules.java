package org.to2mbn.maptranslator.tree;

import static org.to2mbn.maptranslator.tree.TreeConstructor.constructJson;
import static org.to2mbn.maptranslator.tree.TreeConstructor.constructNBT;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringEscapeUtils;
import org.to2mbn.maptranslator.internal.org.json.JSONArray;
import org.to2mbn.maptranslator.internal.org.json.JSONObject;
import org.to2mbn.maptranslator.nbt.NBT;
import org.to2mbn.maptranslator.nbt.NBTCompound;
import org.to2mbn.maptranslator.nbt.NBTString;

public final class MinecraftRules {

	public static final String translatable = "localizable_string";

	/*
	private static final String[] NON_TRANSLATABLE_MODIFIER_NAMES = {
	
	};
	private static final Set<String> nonTranslatableModifierNamesSet = new HashSet<>(Arrays.asList(NON_TRANSLATABLE_MODIFIER_NAMES));
	*/

	public static final TagMarker[] MARKERS = {
			new TagMarker(NodeMatcher.of("(store.level)/Data/LevelName"), "level.name", translatable),

			new TagMarker(NodeMatcher.of("(store.scoreboard)/data/Objectives/*/DisplayName"), "objective.displayname", translatable),
			new TagMarker(NodeMatcher.of("(store.scoreboard)/data/Teams/*/DisplayName"), "team.displayname", translatable),

			new TagMarker(NodeMatcher.of("(item)").and(compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID))), toCompound(nbt -> "item." + toUniqueName(nbt.getString("id"))).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(entity)").and(compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID))), toCompound(nbt -> "entity." + toUniqueName(nbt.getString("id"))).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(tileentity)").and(compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID))), toCompound(nbt -> "tileentity." + toUniqueName(nbt.getString("id"))).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(item)/tag"), "itemtag"),
			new TagMarker(NodeMatcher.of("(item)/tag").and(node -> compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID)).test(node.parent())), node -> toCompound(nbt -> "itemtag." + toUniqueName(nbt.getString("id"))).andThen(Collections::singleton).apply(node.parent())),

			new TagMarker(NodeMatcher.of("(itemtag)/display/Name"), "itemdisplay.name", translatable),
			new TagMarker(NodeMatcher.of("(itemtag)/display/Lore/*"), "itemdisplay.lore", translatable),

			new TagMarker(NodeMatcher.of("(itemtag.minecraft:written_book)"), "book.itemtag"),
			new TagMarker(NodeMatcher.of("(itemtag.minecraft:writable_book)"), "book.itemtag"),
			new TagMarker(NodeMatcher.of("(book.itemtag)/pages/*"), "book.page", "text"),
			new TagMarker(NodeMatcher.of("(itemtag.minecraft:written_book)/author"), "book.author", translatable),
			new TagMarker(NodeMatcher.of("(itemtag.minecraft:written_book)/title"), "book.title", translatable),

			new TagMarker(NodeMatcher.of("(entity)/CustomName"), "entitydisplay.name", translatable),

			new TagMarker(NodeMatcher.of("(store.player)/SelectedItem"), "item"),
			new TagMarker(NodeMatcher.of("(store.player)/Inventory/*"), "item"),
			new TagMarker(NodeMatcher.of("(store.player)/EnderItems/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity)/Inventory/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity)/Equipment/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity)/HandItems/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity)/ArmorItems/*"), "item"),
			new TagMarker(NodeMatcher.of("(horse.entity)/ArmorItem"), "item"),
			new TagMarker(NodeMatcher.of("(horse.entity)/SaddleItem"), "item"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:villager)/Offers/Recipes/*/buy"), "item"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:villager)/Offers/Recipes/*/buyB"), "item"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:villager)/Offers/Recipes/*/sell"), "item"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:item)/Item"), "item"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:item_frame)/Item"), "item"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:llama)/DecorItem"), "item"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:potion)/Potion"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity.minecraft:jukebox)/RecordItem"), "item"),

			new TagMarker(NodeMatcher.of("(tileentity)/CustomName"), "blockdisplay.name", translatable),
			new TagMarker(NodeMatcher.of("(tileentity)/Lock"), translatable),

			new TagMarker(NodeMatcher.of("(store.chunk)/Level/TileEntities/(tileentity.minecraft:sign)/(text)"), "sign.text.auto_generated"),
			new TagMarker(NodeMatcher.of("(tileentity.minecraft:sign)/Text1"), "text"),
			new TagMarker(NodeMatcher.of("(tileentity.minecraft:sign)/Text2"), "text"),
			new TagMarker(NodeMatcher.of("(tileentity.minecraft:sign)/Text3"), "text"),
			new TagMarker(NodeMatcher.of("(tileentity.minecraft:sign)/Text4"), "text"),
			new TagMarker(NodeMatcher.of("(text)").and(node -> TextContext.textFromNode(node).map(text -> {
				if (text.trim().isEmpty() ||
						text.equals("\"\"") ||
						text.equals("null"))
					return false;
				else
					return true;
			}).orElse(false)), node -> {
				String txt = TextContext.textFromNode(node).get();
				boolean isJson;
				try {
					constructJson(txt);
					isJson = true;
				} catch (ArgumentParseException e) {
					isJson = false;
				}
				if (isJson) return Collections.singleton("rawmsg");
				if (txt.startsWith("\"") && txt.endsWith("\"")) return Collections.singleton("quoted_msg");
				return Collections.singleton(translatable);
			}),

			new TagMarker(NodeMatcher.of("(entity)/DeathLootTable"), "loottable"),
			new TagMarker(NodeMatcher.of("(entity)/LootTable"), "loottable"),
			new TagMarker(NodeMatcher.of("(tileentity)/LootTable"), "loottable"),

			new TagMarker(NodeMatcher.of("(entity.minecraft:spawner_minecart)"), "spawner"),
			new TagMarker(NodeMatcher.of("(tileentity.minecraft:mob_spawner)"), "spawner"),
			new TagMarker(NodeMatcher.of("(spawner)/SpawnPotentials/*/Entity"), "entity"),
			new TagMarker(NodeMatcher.of("(spawner)/SpawnPotentials/*/Properties"), "entity", "entity.*"),
			new TagMarker(NodeMatcher.of("(spawner)/SpawnData"), "entity", "entity.*"),

			new TagMarker(NodeMatcher.of("(entity.minecraft:commandblock_minecart)/Command"), "command"),
			new TagMarker(NodeMatcher.of("(tileentity.minecraft:command_block)/Command"), "command"),

			new TagMarker(NodeMatcher.of("(item.minecraft:armor_stand.tag)/EntityTag"), "entity"),
			new TagMarker(NodeMatcher.of("(item.minecraft:spawn_egg.tag)/EntityTag"), "entity"),
			new TagMarker(NodeMatcher.of("(store.player)/RootVehicle/Entity"), "entity"),
			new TagMarker(NodeMatcher.of("(store.chunk)/Level/Entities/*"), "entity"),
			new TagMarker(NodeMatcher.of("(entity)/Riding"), "entity"),
			new TagMarker(NodeMatcher.of("(entity)/Passengers/*"), "entity"),

			new TagMarker(NodeMatcher.of("(entity.minecraft:horse)"), "horse.entity"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:donkey)"), "horse.entity"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:mule)"), "horse.entity"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:zombie_horse)"), "horse.entity"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:skeleton_horse)"), "horse.entity"),

			new TagMarker(NodeMatcher.of("(store.chunk)/Level/TileEntities/*"), "tileentity"),
			new TagMarker(NodeMatcher.of("(entity.minecraft:falling_block)/TileEntityData"), "tileentity", "tileentity.*"),
			new TagMarker(NodeMatcher.of("(itemtag)/BlockEntityTag"), "tileentity", "tileentity.*"),

			new TagMarker(NodeMatcher.of("(itemtag)/AttributeModifiers/*"), "modifier"),
			new TagMarker(NodeMatcher.of("(modifier)/Name"), "modifier_name"),
			new TagMarker(NodeMatcher.of("(entity)/Attributes/*"), "attribute"),
			new TagMarker(NodeMatcher.of("(attribute)/Modifiers/*"), "modifier"),
			// It's unnecessary to translate modifier_name
			// new TagMarker(NodeMatcher.of("(modifier_name)").and(node -> TextNodeReplacer.getText(node).map(text -> !nonTranslatableModifierNamesSet.contains(text)).orElse(false)), translatable),

			new TagMarker(NodeMatcher.of("(msg)"), node -> {
				Object obj = ((JsonNode) node).json;
				if (obj instanceof JSONObject) {
					return Collections.singleton("msg.obj");
				} else if (obj instanceof JSONArray) {
					return Collections.singleton("msg.array");
				} else if (obj instanceof String) {
					return Collections.singleton(translatable);
				} else {
					return Collections.emptySet();
				}
			}),

			new TagMarker(NodeMatcher.of("(msg.array)/*"), "msg"),
			new TagMarker(NodeMatcher.of("(msg.obj)/text"), translatable),
			new TagMarker(NodeMatcher.of("(msg.obj)/extra/*"), "msg"),
			new TagMarker(NodeMatcher.of("(msg.obj)/clickEvent"), toJson(json -> "click_event." + json.get("action")).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(msg.obj)/hoverEvent"), toJson(json -> "hover_event." + json.get("action")).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(click_event.run_command)/value").and(node -> TextContext.textFromNode(node).map(text -> !text.trim().isEmpty()).orElse(false)), node -> Collections.singleton(TextContext.textFromNode(node).get().startsWith("/") ? "command" : translatable)),
			new TagMarker(NodeMatcher.of("(click_event.suggest_command)/value"), "suggest_command", translatable),
			new TagMarker(NodeMatcher.of("(hover_event.show_text)/value"), "hover_text", "msg"),

			entityAlias("mushroomcow", "mooshroom"),
			entityAlias("ozelot", "ocelot"),
			entityAlias("leashknot", "leash_knot"),
			entityAlias("dragonfireball", "dragon_fireball"),
			entityAlias("minecartchest", "chest_minecart"),
			entityAlias("thrownegg", "egg"),
			entityAlias("lightningbolt", "lightning_bolt"),
			entityAlias("endercrystal", "ender_crystal"),
			entityAlias("minecartcommandblock", "commandblock_minecart"),
			entityAlias("enderdragon", "ender_dragon"),
			entityAlias("cavespider", "cave_spider"),
			entityAlias("thrownenderpearl", "ender_pearl"),
			entityAlias("eyeofendersignal", "eye_of_ender_signal"),
			entityAlias("areaeffectcloud", "area_effect_cloud"),
			entityAlias("armorstand", "armor_stand"),
			entityAlias("fallingsand", "falling_block"),
			entityAlias("fireworksrocketentity", "fireworks_rocket"),
			entityAlias("lavaslime", "magma_cube"),
			entityAlias("minecartrideable", "minecart"),
			entityAlias("polarbear", "polar_bear"),
			entityAlias("minecartfurnace", "furnace_minecart"),
			entityAlias("minecarthopper", "hopper_minecart"),
			entityAlias("entityhorse", "horse"),
			entityAlias("itemframe", "item_frame"),
			entityAlias("shulkerbullet", "shulker_bullet"),
			entityAlias("smallfireball", "small_fireball"),
			entityAlias("spectralarrow", "spectral_arrow"),
			entityAlias("thrownpotion", "potion"),
			entityAlias("minecartspawner", "spawner_minecart"),
			entityAlias("primedtnt", "tnt"),
			entityAlias("minecarttnt", "tnt_minecart"),
			entityAlias("villagergolem", "villager_golem"),
			entityAlias("witherboss", "wither"),
			entityAlias("witherskull", "wither_skull"),
			entityAlias("thrownexpbottle", "xp_bottle"),
			entityAlias("xporb", "xp_orb"),
			entityAlias("pigzombie", "zombie_pigman"),

			tileentityAlias("cauldron", "brewing_stand"),
			tileentityAlias("control", "command_block"),
			tileentityAlias("dldetector", "daylight_detector"),
			tileentityAlias("trap", "dispenser"),
			tileentityAlias("enchanttable", "enchanting_table"),
			tileentityAlias("endgateway", "end_gateway"),
			tileentityAlias("airportal", "end_portal"),
			tileentityAlias("enderchest", "ender_chest"),
			tileentityAlias("flowerpot", "flower_pot"),
			tileentityAlias("recordplayer", "jukebox"),
			tileentityAlias("mobspawner", "mob_spawner"),
			tileentityAlias("music", "noteblock"),
			tileentityAlias("structure", "structure_block"),

	};

	public static final NodeReplacer[] REPLACERS = {

			CommandReplacer.of("ban <name> <reason>", "reason",
					args -> new TextArgumentNode(args.get(args.get("reason")))
							.withTag("ban.reason")
							.withTag(translatable)),

			CommandReplacer.of("ban-ip <name> <reason>", "reason",
					args -> new TextArgumentNode(args.get(args.get("reason")))
							.withTag("ban.reason")
							.withTag(translatable)),

			CommandReplacer.of("blockdata <x> <y> <z> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("tileentity")
							.withTag("tileentity.*")),

			CommandReplacer.of("clear <player> <item> <data> <maxCount> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("itemtag")
							.withTag("itemtag." + toUniqueName(args.get("item")))),

			CommandReplacer.of("entitydata <entity> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("entity")
							.withTag("entity.*")),

			CommandReplacer.of("execute <entity> <x> <y> <z> <clause>", "clause",
					args -> {
						String clause = args.get("clause");
						return new TextArgumentNode(clause)
								.withTag(clause.startsWith("detect ") ? "execute.clause" : "command");
					}),

			CommandReplacer.of("execute.clause", "detect <x2> <y2> <z2> <block> <state> <command>", "command",
					args -> new TextArgumentNode(args.get("command"))
							.withTag("command")),

			CommandReplacer.of("fill <x1> <y1> <z1> <x2> <y2> <z2> <block> <state> <oldBlockHandling> <dataTag>", "dataTag",
					args -> {
						String dataTag = args.get("dataTag");
						if (args.get("oldBlockHandling").equals("replace")) {
							return new TextArgumentNode(dataTag);
						} else {
							return constructNBT(dataTag)
									.withTag("tileentity")
									.withTag("tileentity.*");
						}
					}),

			CommandReplacer.of("give <player> <item> <amount> <data> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("itemtag")
							.withTag("itemtag." + toUniqueName(args.get("item")))),

			CommandReplacer.of("kick <player> <reason>", "reason",
					args -> new TextArgumentNode(args.get(args.get("reason")))
							.withTag("kick.reason")
							.withTag(translatable)),

			CommandReplacer.of("me <action>", "action",
					args -> new TextArgumentNode(args.get(args.get("action")))
							.withTag("me.action")
							.withTag(translatable)),

			CommandReplacer.of("replaceitem block <x> <y> <z> <slot> <item> <amount> <data> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("itemtag")
							.withTag("itemtag." + toUniqueName(args.get("item")))),

			CommandReplacer.of("replaceitem entity <selector> <slot> <item> <amount> <data> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("itemtag")
							.withTag("itemtag." + toUniqueName(args.get("item")))),

			CommandReplacer.of("say <message>", "message",
					args -> new TextArgumentNode(args.get("message"))
							.withTag("say.message")
							.withTag(translatable)),

			CommandReplacer.of("setblock <x> <y> <z> <block> <state> <oldBlockHandling> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("tileentity")
							.withTag("tileentity.*")),

			CommandReplacer.of("summon <entity> <x> <y> <z> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("entity")
							.withTag("entity." + toUniqueName(args.get("entity")))),

			CommandReplacer.of("tell <player> <message>", "message",
					args -> new TextArgumentNode(args.get("message"))
							.withTag("tell.message")
							.withTag(translatable)),

			CommandReplacer.of("msg <player> <message>", "message",
					args -> new TextArgumentNode(args.get("message"))
							.withTag("tell.message")
							.withTag(translatable)),

			CommandReplacer.of("w <player> <message>", "message",
					args -> new TextArgumentNode(args.get("message"))
							.withTag("tell.message")
							.withTag(translatable)),

			CommandReplacer.of("tellraw <player> <message>", "message",
					args -> new TextArgumentNode(args.get("message"))
							.withTag("text")),

			CommandReplacer.of("testfor <player> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("entity")
							.withTag("entity.*")
							.withTag("store.player")),

			CommandReplacer.of("testforblock <x> <y> <z> <block> <state> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("tileentity")
							.withTag("tileentity.*")),

			CommandReplacer.of("title <player> <clause>", "clause",
					args -> new TextArgumentNode(args.get("clause"))
							.withTag("title.clause")),

			CommandReplacer.of("title.clause", "title <message>", "message",
					args -> constructJson(args.get("message"))
							.withTag("msg")),

			CommandReplacer.of("title.clause", "subtitle <message>", "message",
					args -> constructJson(args.get("message"))
							.withTag("msg")),

			CommandReplacer.of("title.clause", "actionbar <message>", "message",
					args -> constructJson(args.get("message"))
							.withTag("msg")),

			CommandReplacer.of("scoreboard <clause>", "clause",
					args -> new TextArgumentNode(args.get("clause"))
							.withTag("scoreboard.clause")),

			CommandReplacer.of("scoreboard.clause", "objectives <clause>", "clause",
					args -> new TextArgumentNode(args.get("clause"))
							.withTag("scoreboard.clause.objectives")),

			CommandReplacer.of("scoreboard.clause", "players <clause>", "clause",
					args -> new TextArgumentNode(args.get("clause"))
							.withTag("scoreboard.clause.players")),

			CommandReplacer.of("scoreboard.clause", "teams <clause>", "clause",
					args -> new TextArgumentNode(args.get("clause"))
							.withTag("scoreboard.clause.teams")),

			CommandReplacer.of("scoreboard.clause.objectives", "add <name> <criteria> <display_name>", "display_name",
					args -> new TextArgumentNode(args.get("display_name"))
							.withTag(translatable)),

			CommandReplacer.of("scoreboard.clause.players", "set <player> <objective> <score> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("entity")
							.withTag("entity.*")
							.withTag("store.player")),

			CommandReplacer.of("scoreboard.clause.players", "add <player> <objective> <count> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("entity")
							.withTag("entity.*")
							.withTag("store.player")),

			CommandReplacer.of("scoreboard.clause.players", "remove <player> <objective> <count> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("entity")
							.withTag("entity.*")
							.withTag("store.player")),

			CommandReplacer.of("scoreboard.clause.teams", "add <name> <display_name>", "display_name",
					args -> new TextArgumentNode(args.get("display_name"))
							.withTag(translatable)),

			TextReplacer.of(NodeMatcher.of("(rawmsg)"),
					(origin, json) -> {
						Node node = TreeConstructor.constructJson(json)
								.withTag("msg");
						configureToStringAlgorithm(origin, node);
						return node;
					}),

			TextReplacer.of(NodeMatcher.of("(hover_event.show_item)/value"),
					json -> TreeConstructor.constructNBT(json)
							.withTag("item")),

			TextReplacer.of(NodeMatcher.of("(hover_event.show_entity)/value"),
					json -> TreeConstructor.constructNBT(json)
							.withTag("entity")),

			TextReplacer.of(NodeMatcher.of("(quoted_msg)"),
					(origin, quoted) -> {
						String text = StringEscapeUtils.unescapeJson(quoted.substring(1, quoted.length() - 1));
						Node node = new TextArgumentNode(text)
								.withTag(translatable);
						configureToStringAlgorithm(origin, node);
						return node;
					},
					node -> {
						String text = ((ArgumentNode) node).toArgumentString();
						if ("gson".equals(node.properties().get("json.to_string.algorithm"))) {
							return JSONObject._use_gson_toString(() -> JSONObject.quote(text));
						}
						return JSONObject.quote(text);
					}),

	};

	private static Predicate<Node> compoundMatches(Predicate<NBTCompound> condition) {
		return node -> {
			if (node instanceof NBTNode) {
				NBT nbt = ((NBTNode) node).nbt;
				if (nbt instanceof NBTCompound) {
					return condition.test((NBTCompound) nbt);
				}
			}
			return false;
		};
	}

	private static <R> Function<Node, R> toCompound(Function<NBTCompound, R> func) {
		return node -> func.apply(((NBTCompound) ((NBTNode) node).nbt));
	}

	private static <R> Function<Node, R> toJson(Function<JSONObject, R> func) {
		return node -> func.apply(((JSONObject) ((JsonNode) node).json));
	}

	private static TagMarker entityAlias(String alias, String id) {
		return new TagMarker(NodeMatcher.of("(entity." + alias + ")").or(NodeMatcher.of("(entity." + toUniqueName(alias) + ")")), "entity." + toUniqueName(id));
	}

	private static TagMarker tileentityAlias(String alias, String id) {
		return new TagMarker(NodeMatcher.of("(tileentity." + alias + ")").or(NodeMatcher.of("(tileentity." + toUniqueName(alias) + ")")), "tileentity." + toUniqueName(id));
	}

	private static String toUniqueName(String name) {
		name = name.toLowerCase();
		if (name.indexOf(':') == -1) name = "minecraft:" + name;
		return name;
	}

	private static void configureToStringAlgorithm(Node origin, Node node) {
		if (origin.hasTag("sign.text.auto_generated"))
			node.properties().put("json.to_string.algorithm", "gson");
	}

	private MinecraftRules() {}

}
