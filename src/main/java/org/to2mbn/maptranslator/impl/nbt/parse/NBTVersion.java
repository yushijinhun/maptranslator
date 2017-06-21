package org.to2mbn.maptranslator.impl.nbt.parse;

import java.util.Stack;
import java.util.function.Supplier;

public enum NBTVersion {

	MC_OLD(JsonNBTConverter_MC_OLD.INSTANCE, "<1.12"),
	MC_1_12(JsonNBTConverter_MC_1_12.INSTANCE, "1.12+");

	public static volatile NBTVersionConfig defaultConfig = new NBTVersionConfig(MC_OLD, MC_OLD);

	public final JsonNBTConverter parser;
	private final String name;

	NBTVersion(JsonNBTConverter parser, String name) {
		this.parser = parser;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	// thread local
	private static final ThreadLocal<Stack<NBTVersionConfig>> tlConfig = new ThreadLocal<>();

	public static NBTVersionConfig getCurrentConfig() {
		Stack<NBTVersionConfig> stack = tlConfig.get();
		if (stack == null) return defaultConfig;
		return stack.peek();
	}

	public static <T> T setCurrentConfig(NBTVersionConfig config, Supplier<T> action) {
		Stack<NBTVersionConfig> stack = tlConfig.get();
		if (stack == null) {
			stack = new Stack<>();
			tlConfig.set(stack);
		}
		stack.push(config);
		try {
			return action.get();
		} finally {
			stack.pop();
			if (stack.isEmpty())
				tlConfig.remove();
		}
	}

}
