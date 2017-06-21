package org.to2mbn.maptranslator.impl.nbt.parse;

public class NBTVersionConfig {

	private NBTVersion inputVersion;
	private NBTVersion outputVersion;

	public NBTVersionConfig(NBTVersion inputVersion, NBTVersion outputVersion) {
		this.inputVersion = inputVersion;
		this.outputVersion = outputVersion;
	}

	public NBTVersion getInputVersion() {
		return inputVersion;
	}

	public NBTVersion getOutputVersion() {
		return outputVersion;
	}

}
