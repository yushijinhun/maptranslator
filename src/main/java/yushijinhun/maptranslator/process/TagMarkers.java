package yushijinhun.maptranslator.process;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import yushijinhun.maptranslator.nbt.NBT;
import yushijinhun.maptranslator.nbt.NBTCompound;
import yushijinhun.maptranslator.nbt.NBTString;
import yushijinhun.maptranslator.tree.NBTNode;
import yushijinhun.maptranslator.tree.Node;
import yushijinhun.maptranslator.tree.NodeMatcher;
import yushijinhun.maptranslator.tree.TagMarker;

public final class TagMarkers {

	public static final String T_STR = "localizable_string";

	public static final TagMarker[] MARKERS = {
			new TagMarker(NodeMatcher.of("(store.level)/Data/LevelName"), "level.name", T_STR),

			new TagMarker(NodeMatcher.of("(store.scoreboard)/data/Objectives/*/DisplayName"), "objective.displayname", T_STR),
			new TagMarker(NodeMatcher.of("(store.scoreboard)/data/Teams/*/DisplayName"), "team.displayname", T_STR),

			new TagMarker(NodeMatcher.of("(item)").and(compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID))), toCompound(nbt -> "item." + nbt.getString("id").toLowerCase()).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(entity)").and(compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID))), toCompound(nbt -> "entity." + nbt.getString("id").toLowerCase()).andThen(Collections::singleton)),
			new TagMarker(NodeMatcher.of("(tileentity)").and(compoundMatches(nbt -> nbt.containsKey("id", NBTString.ID))), toCompound(nbt -> "tileentity." + nbt.getString("id").toLowerCase()).andThen(Collections::singleton)),
			
			new TagMarker(NodeMatcher.of("(item)/tag/display/Name"), "item._displayname", T_STR),
			new TagMarker(NodeMatcher.of("(item)/tag/display/Lore/*"), "item._lore", T_STR),

			new TagMarker(NodeMatcher.of("(item.minecraft:written_book)"), "item._book"),
			new TagMarker(NodeMatcher.of("(item.minecraft:writable_book)"), "item._book"),
			new TagMarker(NodeMatcher.of("(item._book)/tag/pages/*"), "book.page", T_STR),
			new TagMarker(NodeMatcher.of("(item.minecraft:written_book)/tag/author"), "book.author", T_STR),
			new TagMarker(NodeMatcher.of("(item.minecraft:written_book)/tag/title"), "book.title", T_STR),

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

			new TagMarker(NodeMatcher.of("(item.minecraft:armor_stand)/tag/EntityTag"), "entity"),
			new TagMarker(NodeMatcher.of("(item.minecraft:spawn_egg)/tag/EntityTag"), "entity"),
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
			new TagMarker(NodeMatcher.of("(item)/tag/BlockEntityTag"), "tileentity"),

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

	private TagMarkers() {}

}
