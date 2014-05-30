package cvut.fel.nemetma1.routingService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.joda.time.DateTime;

public class ResourceToFile {

	private static Random random = new Random();

	public static File getFileFromResource(String relativePath) throws IOException {

		InputStream is = ResourceToFile.class.getResourceAsStream(relativePath);
		if (is == null) {
			throw new NullPointerException();
		}
		
		File ftmp = File.createTempFile(
				String.format("cycle_planner_%d_%d", DateTime.now().getMillis(), (int) Math.abs(random.nextInt())),
				".tmp");
		ftmp.deleteOnExit();
		FileOutputStream fout = new FileOutputStream(ftmp);
		System.out.println(relativePath + " " + is + " " + ftmp);

		byte[] buf = new byte[1024];
		int len;
		while ((len = is.read(buf)) != -1) {
			fout.write(buf, 0, len);
		}
		fout.close();

		return ftmp;
	}
}
