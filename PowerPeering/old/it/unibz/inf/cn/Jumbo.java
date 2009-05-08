package old.it.unibz.inf.cn;

import java.io.PrintStream;

public class Jumbo {
	
	private static final PrintStream LOG = System.out;
	private static final PrintStream ERR_LOG = System.err;

	private static final String DATA_PATH = "jumbo/";
	private static final String TMP_DIR = "jumbo/tmp/";
	
	public static void main(String[] args) {

	}
	
	public static String getDataPath() {
		return DATA_PATH;
	}
	
	public static String getTmpDir() {
		return TMP_DIR;
	}
	
	public static void errLog(String log) {
		ERR_LOG.println(log);
	}
	
	public static void Log(String log) {
		LOG.println(log);
	}
	
}
