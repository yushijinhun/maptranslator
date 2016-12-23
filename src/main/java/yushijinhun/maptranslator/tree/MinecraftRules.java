package yushijinhun.maptranslator.tree;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import yushijinhun.maptranslator.nbt.JsonNBTConverter;
import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTCompound;
import yushijinhun.maptranslator.nbt.NBTString;
import yushijinhun.maptranslator.tree.NBTNode;
import yushijinhun.maptranslator.tree.Node;
import yushijinhun.maptranslator.tree.NodeMatcher;
import yushijinhun.maptranslator.tree.TagMarker;

public final class MinecraftRules {

	public static final String T_STR = "localizable_string";

	public static final TagMarker[] MARKERS = {
			new TagMarker(NodeMatcher.of("(store.level)/Data/LevelName"), "level.name", T_STR),

			new TagMarker(NodeMatcher.of("(store.scoreboard)/data/Objectives/*/DisplayName"), "objective.displayname", T_STR),
			new TagMarker(NodeMatcher.of("(store.scoreboard)/data/Teams/*/DisplayName"), "team.displayname", T_STR),

			new TagMarker(NodeMatcher.of("(item)").and(compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID))), toCompound(nbt -> "item." + nbt.getString("id").toLowerCase()).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(entity)").and(compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID))), toCompound(nbt -> "entity." + nbt.getString("id").toLowerCase()).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(tileentity)").and(compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID))), toCompound(nbt -> "tileentity." + nbt.getString("id").toLowerCase()).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(item)/tag"), "item._.tag"),
			new TagMarker(NodeMatcher.of("(item)/tag").and(node -> compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID)).test(node.parent())), node -> toCompound(nbt -> "item." + nbt.getString("id").toLowerCase() + ".tag").andThen(Collections::singleton).apply(node.parent())),

			new TagMarker(NodeMatcher.of("(item._.tag)/display/Name"), "item._displayname", T_STR),
			new TagMarker(NodeMatcher.of("(item._.tag)/display/Lore/*"), "item._lore", T_STR),

			new TagMarker(NodeMatcher.of("(item.minecraft:written_book.tag)"), "item._book.tag"),
			new TagMarker(NodeMatcher.of("(item.minecraft:writable_book.tag)"), "item._book.tag"),
			new TagMarker(NodeMatcher.of("(item._book.tag)/pages/*"), "book.page", T_STR),
			new TagMarker(NodeMatcher.of("(item.minecraft:written_book.tag)/author"), "book.author", T_STR),
			new TagMarker(NodeMatcher.of("(item.minecraft:written_book.tag)/title"), "book.title", T_STR),

			new TagMarker(NodeMatcher.of("(entity)/CustomName"), "entity._customname", T_STR),

			new TagMarker(NodeMatcher.of("(store.player)/SelectedItem"), "item"),
			new TagMarker(NodeMatcher.of("(store.player)/Inventory/*"), "item"),
			new TagMarker(NodeMatcher.of("(store.player)/EnderItems/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity)/Equipment/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity)/HandItems/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity)/ArmorItems/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity._horse)/ArmorItem"), "item"),
			new TagMarker(NodeMatcher.of("(entity._horse)/SaddleItem"), "item"),
			new TagMarker(NodeMatcher.of("(entity._chest_horse)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity.villager)/Inventory/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity.villager)/Offers/Recipes/*/buy"), "item"),
			new TagMarker(NodeMatcher.of("(entity.villager)/Offers/Recipes/*/buyB"), "item"),
			new TagMarker(NodeMatcher.of("(entity.villager)/Offers/Recipes/*/sell"), "item"),
			new TagMarker(NodeMatcher.of("(entity.item)/Item"), "item"),
			new TagMarker(NodeMatcher.of("(entity.item_frame)/Item"), "item"),
			new TagMarker(NodeMatcher.of("(entity.chest_minecart)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity.hopper_minecart)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity.llama)/DecorItem"), "item"),
			new TagMarker(NodeMatcher.of("(entity.llama)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(entity.potion)/Potion"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity.cauldron)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity.brewing_stand)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity.chest)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity.dispenser)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity.dropper)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity.furnace)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity.hopper)/Items/*"), "item"),
			new TagMarker(NodeMatcher.of("(tileentity.jukebox)/RecordItem"), "item"),

			new TagMarker(NodeMatcher.of("(tileentity.brewing_stand)/CustomName"), "brewing_stand.customname", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.chest)/CustomName"), "chest.customname", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.command_block)/CustomName"), "command_block.customname", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.dispenser)/CustomName"), "dispenser.customname", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.dropper)/CustomName"), "dropper.customname", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.enchanting_table)/CustomName"), "enchanting_table.customname", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.furnace)/CustomName"), "furnace.customname", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.hopper)/CustomName"), "hopper.customname", T_STR),

			new TagMarker(NodeMatcher.of("(tileentity.sign)/Text1"), "sign.text", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.sign)/Text2"), "sign.text", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.sign)/Text3"), "sign.text", T_STR),
			new TagMarker(NodeMatcher.of("(tileentity.sign)/Text4"), "sign.text", T_STR),

			new TagMarker(NodeMatcher.of("(entity)/DeathLootTable"), "loottable"),
			new TagMarker(NodeMatcher.of("(entity.chest_minecart)/LootTable"), "loottable"),
			new TagMarker(NodeMatcher.of("(entity.hopper_minecart)/LootTable"), "loottable"),
			new TagMarker(NodeMatcher.of("(tileentity.chest)/LootTable"), "loottable"),
			new TagMarker(NodeMatcher.of("(tileentity.dispenser)/LootTable"), "loottable"),
			new TagMarker(NodeMatcher.of("(tileentity.dropper)/LootTable"), "loottable"),
			new TagMarker(NodeMatcher.of("(tileentity.hopper)/LootTable"), "loottable"),

			new TagMarker(NodeMatcher.of("(entity.spawner_minecart)"), "spawner"),
			new TagMarker(NodeMatcher.of("(tileentity.mob_spawner)"), "spawner"),
			new TagMarker(NodeMatcher.of("(spawner)/SpawnPotentials/*/Entity"), "entity"),
			new TagMarker(NodeMatcher.of("(spawner)/SpawnData"), "entity"),

			new TagMarker(NodeMatcher.of("(entity.commandblock_minecart)/Command"), "command"),
			new TagMarker(NodeMatcher.of("(tileentity.command_block)/Command"), "command"),

			new TagMarker(NodeMatcher.of("(item.minecraft:armor_stand.tag)/EntityTag"), "entity"),
			new TagMarker(NodeMatcher.of("(item.minecraft:spawn_egg.tag)/EntityTag"), "entity"),
			new TagMarker(NodeMatcher.of("(store.player)/RootVehicle/Entity"), "entity"),
			new TagMarker(NodeMatcher.of("(store.chunk)/Level/Entities/*"), "entity"),
			new TagMarker(NodeMatcher.of("(entity)/Riding"), "entity"),
			new TagMarker(NodeMatcher.of("(entity)/Passengers/*"), "entity"),

			new TagMarker(NodeMatcher.of("(entity.horse)"), "entity._horse"),
			new TagMarker(NodeMatcher.of("(entity.donkey)"), "entity._horse", "entity._chest_horse"),
			new TagMarker(NodeMatcher.of("(entity.mule)"), "entity._horse", "entity._chest_horse"),
			new TagMarker(NodeMatcher.of("(entity.zombie_horse)"), "entity._horse"),
			new TagMarker(NodeMatcher.of("(entity.skeleton_horse)"), "entity._horse"),

			new TagMarker(NodeMatcher.of("(store.chunk)/Level/TileEntities/*"), "tileentity"),
			new TagMarker(NodeMatcher.of("(entity.falling_block)/TileEntityData"), "tileentity"),
			new TagMarker(NodeMatcher.of("(item._.tag)/BlockEntityTag"), "tileentity"),

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

			new CommandReplacer("give <player> <item> <amount> <data> <dataTag>", singletonMap("dataTag", args -> {
				NBTRootNode node = TreeConstructor.construct(JsonNBTConverter.getTagFromJson(args.get("dataTag")));
				node.tags().add("item._.tag");
				String itemName = args.get("item");
				if (!itemName.contains(":")) itemName = "minecraft:" + itemName;
				node.tags().add("item." + itemName + ".tag");
				return node;
			})).toNodeReplacer()

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

	private static TagMarker entityAlias(String alias, String id) {
		return new TagMarker(NodeMatcher.of("(entity." + alias + ")"), "entity." + id);
	}

	private static TagMarker tileentityAlias(String alias, String id) {
		return new TagMarker(NodeMatcher.of("(tileentity." + alias + ")"), "tileentity." + id);
	}

	private static <K, V> Map<K, V> singletonMap(K k, V v) {
		Map<K, V> map = new HashMap<>();
		map.put(k, v);
		return map;
	}

	private MinecraftRules() {}

}
