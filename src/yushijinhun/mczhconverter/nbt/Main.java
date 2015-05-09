package yushijinhun.mczhconverter.nbt;

import java.io.File;
import yushijinhun.mczhconverter.MCZHConverter;

public class Main {

	public static void main(String[] args) {
		MCZHConverter converter = new MCZHConverter();
		for (String arg : args) {
			converter.add(new File(arg));
		}
		converter.await();
		converter.shutdown();
	}

}
