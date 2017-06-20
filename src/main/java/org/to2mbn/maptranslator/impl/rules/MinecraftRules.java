package org.to2mbn.maptranslator.impl.rules;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeJson;
import static org.to2mbn.maptranslator.impl.json.process.JsonTreeConstructor.constructJson;
import static org.to2mbn.maptranslator.impl.nbt.process.NBTTreeConstructor.constructNBT;
import static org.to2mbn.maptranslator.rules.RulesConstants.translatable;
import static org.to2mbn.maptranslator.rules.RulesConstants.normalize_space;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import org.to2mbn.maptranslator.impl.json.parse.JSONArray;
import org.to2mbn.maptranslator.impl.json.parse.JSONObject;
import org.to2mbn.maptranslator.impl.json.tree.JsonNode;
import org.to2mbn.maptranslator.impl.nbt.parse.NBT;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTCompound;
import org.to2mbn.maptranslator.impl.nbt.parse.NBTString;
import org.to2mbn.maptranslator.impl.nbt.tree.NBTNode;
import org.to2mbn.maptranslator.process.ArgumentParseException;
import org.to2mbn.maptranslator.process.CommandReplacer;
import org.to2mbn.maptranslator.process.NodeMatcher;
import org.to2mbn.maptranslator.process.NodeReplacer;
import org.to2mbn.maptranslator.process.TagMarker;
import org.to2mbn.maptranslator.process.TextReplacer;
import org.to2mbn.maptranslator.rules.RulesProvider;
import org.to2mbn.maptranslator.tree.ArgumentNode;
import org.to2mbn.maptranslator.tree.Node;
import org.to2mbn.maptranslator.tree.TextArgumentNode;

public class MinecraftRules implements RulesProvider {

	private static final TagMarker[] MARKERS = {

			tag("(store.level)/Data/Player", "store.player"),
			tag("(store.player)/SelectedItem", "item"),
			tag("(store.player)/Inventory/*", "item"),
			tag("(store.player)/EnderItems/*", "item"),
			tag("(store.player)/RootVehicle/Entity", "entity"),
			tag("(store.chunk)/Level/Entities/*", "entity"),
			tag("(store.chunk)/Level/TileEntities/*", "tileentity"),
			tag("(store.structure)/entities/*/nbt", "entity"),
			tag("(store.structure)/blocks/*/nbt", "tileentity"),
			tag("(store.loottable)/pools/*", "lt.pool"),
			tag("(lt.pool)/entries/*", "lt.entry"),
			tag("(lt.entry)/functions/*", "lt.function"),
			extendTagJson("lt.function", "function"),
			tag("(store.advancements)/display/title", "msg", "advancement.title"),
			tag("(store.advancements)/display/description", "msg", "advancement.description"),
			tag("(store.advancements)/criteria/*", "advancement.criteria", "advancement.criteria.*"),
			extendTagJson("advancement.criteria", "trigger"),

			tag("(advancement.criteria.minecraft:bred_animals)/conditions/child", "predicate.entity"),
			tag("(advancement.criteria.minecraft:bred_animals)/conditions/parent", "predicate.entity"),
			tag("(advancement.criteria.minecraft:bred_animals)/conditions/partner", "predicate.entity"),
			tag("(advancement.criteria.minecraft:consume_item)/conditions/item", "predicate.item"),
			tag("(advancement.criteria.minecraft:cured_zombie_villager)/conditions/villager", "predicate.entity"),
			tag("(advancement.criteria.minecraft:cured_zombie_villager)/conditions/zombie", "predicate.entity"),
			tag("(advancement.criteria.minecraft:enchanted_item)/conditions/item", "predicate.item"),
			tag("(advancement.criteria.minecraft:entity_hurt_player)/conditions/damage", "predicate.damage"),
			tag("(advancement.criteria.minecraft:entity_killed_player)/conditions/entity", "predicate.entity"),
			tag("(advancement.criteria.minecraft:entity_killed_player)/conditions/killing_blow", "predicate.damage_type"),
			tag("(advancement.criteria.minecraft:inventory_changed)/conditions/items/*", "predicate.item"),
			tag("(advancement.criteria.minecraft:item_durability_changed)/conditions/item", "predicate.item"),
			tag("(advancement.criteria.minecraft:placed_block)/conditions/item", "predicate.item"),
			tag("(advancement.criteria.minecraft:player_hurt_entity)/conditions/damage", "predicate.damage"),
			tag("(advancement.criteria.minecraft:player_killed_entity)/conditions/entity", "predicate.entity"),
			tag("(advancement.criteria.minecraft:player_killed_entity)/conditions/killing_blow", "predicate.damage_type"),
			tag("(advancement.criteria.minecraft:summoned_entity)/conditions/entity", "predicate.entity"),
			tag("(advancement.criteria.minecraft:tame_animal)/conditions/entity", "predicate.entity"),
			tag("(advancement.criteria.minecraft:used_totem)/conditions/item", "predicate.item"),
			tag("(advancement.criteria.minecraft:villager_trade)/conditions/item", "predicate.item"),
			tag("(advancement.criteria.minecraft:villager_trade)/conditions/villager", "predicate.entity"),

			tag("(predicate.damage)/direct_entity", "predicate.entity"),
			tag("(predicate.damage)/source_entity", "predicate.entity"),
			tag("(predicate.damage)/type", "predicate.damage_type"),
			tag("(predicate.damage_type)/direct_entity", "predicate.entity"),
			tag("(predicate.damage_type)/source_entity", "predicate.entity"),

			new TagMarker(NodeMatcher.of("(store.mcfunction)/*").and(
					node -> node.getText()
							.map(line -> !line.trim().startsWith("#"))
							.orElse(false)),
					"command"),

			tag("(entity)/Riding", "entity"),
			tag("(entity)/Passengers/*", "entity"),
			extendTagNbt("entity", "id"),
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
			tag("(entity)/Items/*", "item"),
			tag("(entity)/Inventory/*", "item"),
			tag("(entity)/Equipment/*", "item"),
			tag("(entity)/HandItems/*", "item"),
			tag("(entity)/ArmorItems/*", "item"),
			tag("(entity.minecraft:villager)/Offers/Recipes/*/buy", "item"),
			tag("(entity.minecraft:villager)/Offers/Recipes/*/buyB", "item"),
			tag("(entity.minecraft:villager)/Offers/Recipes/*/sell", "item"),
			tag("(entity.minecraft:item)/Item", "item"),
			tag("(entity.minecraft:item_frame)/Item", "item"),
			tag("(entity.minecraft:llama)/DecorItem", "item"),
			tag("(entity.minecraft:potion)/Potion", "item"),
			tag("(entity.minecraft:horse)", "horse.entity"),
			tag("(entity.minecraft:donkey)", "horse.entity"),
			tag("(entity.minecraft:mule)", "horse.entity"),
			tag("(entity.minecraft:zombie_horse)", "horse.entity"),
			tag("(entity.minecraft:skeleton_horse)", "horse.entity"),
			tag("(horse.entity)/ArmorItem", "item"),
			tag("(horse.entity)/SaddleItem", "item"),
			tag("(entity.minecraft:falling_block)/TileEntityData", "tileentity", "tileentity.*"),

			extendTagNbt("tileentity", "id"),
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
			tag("(tileentity.minecraft:jukebox)/RecordItem", "item"),
			tag("(tileentity)/Items/*", "item"),

			tag("(item)/tag", "itemtag"),
			new TagMarker(NodeMatcher.of("(item)/tag").and(node -> compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID)).test(node.parent())), node -> toCompound(nbt -> "itemtag." + toUniqueName(nbt.getString("id"))).andThen(Collections::singleton).apply(node.parent())),
			tag("(itemtag.minecraft:armor_stand)/EntityTag", "entity"),
			tag("(itemtag.minecraft:spawn_egg)/EntityTag", "entity"),
			tag("(itemtag)/BlockEntityTag", "tileentity", "tileentity.*"),

			tag("(entity.minecraft:spawner_minecart)", "spawner"),
			tag("(tileentity.minecraft:mob_spawner)", "spawner"),
			tag("(spawner)/SpawnPotentials/*/Entity", "entity"),
			tag("(spawner)/SpawnPotentials/*/Properties", "entity", "entity.*"),
			tag("(spawner)/SpawnData", "entity", "entity.*"),

			tag("(entity.minecraft:commandblock_minecart)/Command", "command"),
			tag("(tileentity.minecraft:command_block)/Command", "command"),

			tag("(itemtag)/AttributeModifiers/*", "modifier"),
			tag("(modifier)/Name", "modifier_name"),
			tag("(entity)/Attributes/*", "attribute"),
			tag("(attribute)/Modifiers/*", "modifier"),

			tag("(itemtag.minecraft:written_book)", "book.itemtag"),
			tag("(itemtag.minecraft:writable_book)", "book.itemtag"),
			tag("(book.itemtag)/pages/*", "book.page", "text"),
			tag("(tileentity.minecraft:sign)/Text1", "text"),
			tag("(tileentity.minecraft:sign)/Text2", "text"),
			tag("(tileentity.minecraft:sign)/Text3", "text"),
			tag("(tileentity.minecraft:sign)/Text4", "text"),
			new TagMarker(NodeMatcher.of("(text)").and(node -> {
				while (node != null) {
					if (!(node instanceof NBTNode) && node.parent() != null)
						return false;
					node = node.parent();
				}
				return true;
			}), "text.auto_generated"),

			new TagMarker(NodeMatcher.of("(text)").and(node -> node.getText().map(text -> {
				if (text.trim().isEmpty() ||
						text.equals("\"\"") ||
						text.equals("null"))
					return false;
				else
					return true;
			}).orElse(false)), node -> {
				String txt = node.getText().get();
				boolean isJson;
				try {
					constructJson(txt);
					isJson = true;
				} catch (ArgumentParseException e) {
					isJson = false;
				}
				if (isJson) return singleton("rawmsg");
				if (txt.startsWith("\"") && txt.endsWith("\"")) return singleton("quoted_msg");
				return singleton(translatable);
			}),

			new TagMarker(NodeMatcher.of("(msg)"), node -> {
				Object obj = ((JsonNode) node).json;
				if (obj instanceof JSONObject) {
					return singleton("msg.obj");
				} else if (obj instanceof JSONArray) {
					return singleton("msg.array");
				} else if (obj instanceof String) {
					return singleton(translatable);
				} else {
					return emptySet();
				}
			}),

			tag("(msg.array)/*", "msg"),
			tag("(msg.obj)/extra/*", "msg"),
			tag("(msg.obj)/clickEvent", "click_event"),
			tag("(msg.obj)/hoverEvent", "hover_event"),
			extendTagJson("click_event", "action"),
			extendTagJson("hover_event", "action"),
			new TagMarker(NodeMatcher.of("(click_event.run_command)/value").and(node -> node.getText().map(text -> !text.trim().isEmpty()).orElse(false)), node -> singleton(node.getText().get().startsWith("/") ? "command" : translatable)),
			tag("(click_event.run_command)/value(command)", normalize_space),
			tag("(hover_event.show_text)/value", "hover_text", "msg"),

			tag("(store.level)/Data/LevelName", "level.name", translatable),
			tag("(store.scoreboard)/data/Objectives/*/DisplayName", "objective.displayname", translatable),
			tag("(store.scoreboard)/data/Teams/*/DisplayName", "team.displayname", translatable),
			tag("(itemtag)/display/Name", "itemdisplay.name", translatable),
			tag("(itemtag)/display/Lore/*", "itemdisplay.lore", translatable),
			tag("(itemtag.minecraft:written_book)/author", "book.author", translatable),
			tag("(itemtag.minecraft:written_book)/title", "book.title", translatable),
			tag("(entity)/CustomName", "entitydisplay.name", translatable),
			tag("(tileentity)/CustomName", "blockdisplay.name", translatable),
			tag("(tileentity)/Lock", translatable),
			tag("(hover_event.show_entity.entity)/id", translatable),
			tag("(hover_event.show_entity.entity)/name", translatable),
			tag("(hover_event.show_entity.entity)/type", translatable),
			tag("(click_event.suggest_command)/value", "suggest_command", translatable),
			tag("(click_event.open_url)/value", "url", translatable),
			tag("(click_event.open_file)/value", "file", translatable),
			tag("(msg.obj)/text", translatable),
	};

	private static final NodeReplacer[] REPLACERS = {

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

			CommandReplacer.of("scoreboard.clause.players", "tag <player> <clause>", "clause",
					args -> new TextArgumentNode(args.get("clause"))
							.withTag("scoreboard.clause.players.tag")),

			CommandReplacer.of("scoreboard.clause.players.tag", "add <tagName> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("entity")
							.withTag("entity.*")
							.withTag("store.player")),

			CommandReplacer.of("scoreboard.clause.players.tag", "remove <tagName> <dataTag>", "dataTag",
					args -> constructNBT(args.get("dataTag"))
							.withTag("entity")
							.withTag("entity.*")
							.withTag("store.player")),

			TextReplacer.of(NodeMatcher.of("(rawmsg)"),
					(origin, json) -> {
						Node node = constructJson(json)
								.withTag("msg");
						configureToStringAlgorithm(origin, node);
						return node;
					}),

			TextReplacer.of(NodeMatcher.of("(hover_event.show_item)/value"),
					nbt -> constructNBT(nbt)
							.withTag("item")),

			TextReplacer.of(NodeMatcher.of("(hover_event.show_entity)/value"),
					nbt -> constructNBT(nbt)
							.withTag("hover_event.show_entity.entity")),

			TextReplacer.of(NodeMatcher.of("(quoted_msg)"),
					(origin, quoted) -> {
						String text = unescapeJson(quoted.substring(1, quoted.length() - 1));
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

			TextReplacer.of(NodeMatcher.of("(lt.function.minecraft:set_nbt)/tag"),
					nbt -> constructNBT(nbt)
							.withTag("itemtag")
							.withTag("itemtag.*")),

			TextReplacer.of(NodeMatcher.of("(predicate.entity)/nbt"),
					(node, nbt) -> constructNBT(nbt)
							.withTag("entity")
							.withTag("entity." + optionalToUniqueName(jsonSibling(node, "type")))),

			TextReplacer.of(NodeMatcher.of("(predicate.item)/nbt"),
					(node, nbt) -> constructNBT(nbt)
							.withTag("itemtag")
							.withTag("itemtag." + optionalToUniqueName(jsonSibling(node, "item")))),
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

	private static Predicate<Node> jsonObjectMatches(Predicate<JSONObject> condition) {
		return node -> {
			if (node instanceof JsonNode) {
				Object json = ((JsonNode) node).json;
				if (json instanceof JSONObject) {
					return condition.test((JSONObject) json);
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
		if (origin.hasTag("text.auto_generated"))
			node.properties().put("json.to_string.algorithm", "gson");
	}

	private static TagMarker extendTagJson(String tag, String key) {
		return new TagMarker(
				NodeMatcher.of("(" + tag + ")")
						.and(jsonObjectMatches(json -> json.has(key))),
				toJson(json -> tag + "." + toUniqueName(json.get(key).toString())).andThen(Collections::singleton));
	}

	private static TagMarker extendTagNbt(String tag, String key) {
		return new TagMarker(NodeMatcher.of("(" + tag + ")")
				.and(compoundMatches(nbt -> nbt.containsKey(key, NBTString.ID))),
				toCompound(nbt -> tag + "." + toUniqueName(nbt.getString(key))).andThen(Collections::singleton));
	}

	private static TagMarker tag(String expression, String... tags) {
		return new TagMarker(NodeMatcher.of(expression), tags);
	}

	private static String jsonSibling(Node node, String key) {
		return ((JSONObject) ((JsonNode) node.parent()).json).optString(key, null);
	}

	private static String optionalToUniqueName(String name) {
		if (name == null) return "*";
		return toUniqueName(name);
	}

	@Override
	public Collection<? extends TagMarker> getTagMarkers() {
		return asList(MARKERS);
	}

	@Override
	public Collection<? extends NodeReplacer> getNodeReplacers() {
		return asList(REPLACERS);
	}

}
