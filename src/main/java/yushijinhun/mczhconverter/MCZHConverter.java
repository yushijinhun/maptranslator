package yushijinhun.mczhconverter;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MCZHConverter {

	private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	private Set<Future<?>> futures = Collections.synchronizedSet(new HashSet<Future<?>>());

	public void add(File file) {
		if (file.isFile()) {
			futures.add(pool.submit(new ConvertingTask(file)));
		} else if (file.isDirectory()) {
			for (String child : file.list()) {
				add(new File(file, child));
			}
		}
	}

	public void await() {
		for (Future<?> future : futures) {
			try {
				future.wait();
			} catch (Exception e) {
				continue;
			}
		}
	}

	public void shutdown() {
		pool.shutdown();
	}

	private class ConvertingTask implements Runnable {

		private File file;

		public ConvertingTask(File file) {
			this.file = file;
		}

		@Override
		public void run() {
			System.err.printf("[INFO] Coverting %s\n", file.getPath());
			FileConverter converter = FileConverters.getCoverterByFileName(file.getPath());
			converter.convert(file);
		}
	}

}
