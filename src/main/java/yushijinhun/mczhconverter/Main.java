package yushijinhun.mczhconverter;

import java.io.File;

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
