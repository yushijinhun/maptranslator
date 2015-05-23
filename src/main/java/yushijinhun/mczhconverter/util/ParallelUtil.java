package yushijinhun.mczhconverter.util;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class ParallelUtil {

	public static void waitTasks(Collection<Future<?>> tasks) throws InterruptedException {
		for (Future<?> task : tasks) {
			try {
				task.get();
			} catch (ExecutionException e) {
				continue;
			}
		}
	}

	private ParallelUtil() {
	}
}
