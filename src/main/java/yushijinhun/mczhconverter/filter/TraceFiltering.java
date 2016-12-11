package yushijinhun.mczhconverter.filter;

import java.util.List;
import java.util.Stack;
import yushijinhun.mczhconverter.trace.CompoundChild;
import yushijinhun.mczhconverter.trace.ListChild;
import yushijinhun.mczhconverter.trace.NodeTrace;
import yushijinhun.mczhconverter.trace.RootTag;

public final class TraceFiltering {

	private static final String[] FILTERS = {
			"/Data/Version/**",
			"/Data/GameRules/**",
			"/Data/generatorName",
			"/Data/generatorOptions",
			"/Forge/**",
			"/FML/**",
			"/bukkit/**",
			"/Level/TileTicks/i",
			"/data/Features/**",
			"/data/PlayerScores/Name",
			"/data/PlayerScores/Objective",
			"/data/Objectives/CriteriaName",
			"/data/Objectives/RenderType",
			"/data/Objectives/Name",
			"/data/Villages/Players/Name",
			"/data/Teams/TeamColor",
			"/data/Teams/Players",
			"/data/Teams/CollisionRule",
			"/data/Teams/DeathMessageVisibility",
			"Patterns/Pattern",
			"EntityId",
			"LastOutput",
			"LootTable",
			"OwnerUUID",
			"Lock",
			"ExtraType",
			"Attributes/Name",
			"Owner/**",
			"SkullOwner/**",
			"HandItems/id",
			"Equipment/id",
			"HurtBy",
			"Block",
			"Motive",
			"NpcHurtSound",
			"CloakTexture",
			"GlowTexture",
			"NpcStepSound",
			"Texture",
			"SkinUrl",
			"NpcDeathSound",
			"Frequency/freq",
			"tag/backpack-UID",
			"tag/CanDestroy",
			"tag/CanPlaceOn",
			"EntityClass",
			"NpcAngrySound",
			"FiringSound",
			"NpcIdleSound",
			"id",
			"Type",
			"ScriptLanguage",
			"AttributeName",
			"LinkedNpcName",
			"BardSong",
			"NpcInteractLines/Lines/Song",
			"tag/Potion",
			"Attributes/Modifiers/Name",
	};

	private static final TraceFilter[] filter_instances;

	static {
		filter_instances = new TraceFilter[FILTERS.length];
		for (int i = 0; i < FILTERS.length; i++)
			filter_instances[i] = new TraceFilter(FILTERS[i]);
	}

	public static boolean shouldIgnore(String str, Stack<NodeTrace> trace) {
		if (str.trim().isEmpty()) return true;
		List<String> target = TraceFilter.convertTrace(trace);
		for (TraceFilter filter : filter_instances)
			if (filter.matches(target))
				return true;
		return false;
	}

	public static String toStringTrace(List<NodeTrace> trace) {
		StringBuilder sb = new StringBuilder();
		for (NodeTrace node : trace) {
			if (node instanceof RootTag) {
				sb.append(((RootTag) node).source);
			} else if (node instanceof CompoundChild) {
				sb.append("/").append(((CompoundChild) node).key);
			} else if (node instanceof ListChild) {
				sb.append("[").append(((ListChild) node).idx).append("]");
			}
		}
		return sb.toString();
	}

}
