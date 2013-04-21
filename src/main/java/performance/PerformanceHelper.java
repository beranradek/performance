package performance;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerformanceHelper {

	public static void main(String ... args) {
		ExecutorService executorService = null;
		try {
			final URL url = new URL(
				"http://localhost:9100/performance/async?operationDurationMs=3000");
			final Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						callPage(url);
					} catch (Exception ex) {
						System.err.println(ex.getMessage() + ": "
								+ stackTraceToString(ex));

					}
				}
			};
			System.out.println("Running...");
			executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors());
			long i = 0L;
			while (true) {
				executorService.execute(runnable);
				if (i % 1000 == 0) {
					System.out.println("Pause " + i);
					Thread.sleep(10);
				}
				i++;
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		} finally {
			if (executorService != null) {
				executorService.shutdown();
			}
		}
	}

	public static void callPage(URL url) {
		URLConnection conn = null;
		InputStream is = null;
		try {
			conn = url.openConnection();
			conn.setConnectTimeout(CONNECTION_TIMEOUT_MS);
			conn.setReadTimeout(READ_TIMEOUT_MS);
			is = conn.getInputStream();
			is.read();
		} catch (IOException ex) {
			System.err.println(ex.getMessage() + ": " + stackTraceToString(ex));
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					// Ignored
				}
			}
		}
	}

	private static String stackTraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	private static final int READ_TIMEOUT_MS = 100;
	private static final int CONNECTION_TIMEOUT_MS = 100;
}
