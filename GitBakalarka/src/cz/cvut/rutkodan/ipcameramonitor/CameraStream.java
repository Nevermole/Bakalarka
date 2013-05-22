package cz.cvut.rutkodan.ipcameramonitor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import cz.cvut.rutkodan.ipcameramonitor.activities.CameraViewsActivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class CameraStream {
	private String name;
	private URL url;

	public CameraStream(String url) {
		super();
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public Bitmap getData() {
		Bitmap bm = null;
		InputStream din = null;
		try {
			din = url.openStream();
			StringBuilder sb = new StringBuilder();
			int s = 0;
			int s0 = 0;

			int i = 0;
			byte[] header = new byte[100];
			while ((s = din.read()) != 216 || s0 != 255) {
				header[i] = (byte) s0;
				sb.append((char) s);

				i++;
				s0 = s;
				// System.out.println(s+" "+(char)s);
			}
			// System.out.println("found end of frame");
			Properties props = new Properties();
			props.load(new ByteArrayInputStream(header));
			// System.out.println(props);
			int lenght = Integer.parseInt((String) props.get("Content-Length"));
			// System.out.println(lenght);
			byte[] ab = new byte[lenght];
			ab[0] = (byte) 255;
			ab[1] = (byte) 216;
			byte b = (byte) din.read();
			CameraViewsActivity.dataUsed += i + lenght;
			for (i = 2; i < lenght - 2; i++) {
				ab[i] = b;
				b = (byte) (din.read());

				sb.append(b + " ");
			}
			BitmapFactory.Options options = new Options();
			options.inMutable = true;
			options.inPreferQualityOverSpeed = false;
			options.inDither = true;
			options.outMimeType = (String) props.get("Content-Type");
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			bm = BitmapFactory.decodeByteArray(ab, 0, ab.length, options);
			if (bm == null) {
				/*
				 * sb.append("\ndone\n"); for (int j = 0; j < 10; j++) { b =
				 * (byte) (din.read()); sb.append(b + " " + (char) b + ","); }
				 * System.err.println(sb.toString());
				 */
			} else {
				/*
				 * sb.append("\ndone\n"); for (int j = 0; j < 10; j++) { b =
				 * (byte) (din.read()); sb.append(b+" "+(char)b+","); }
				 * System.out.println(sb.toString());
				 */
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			try {
				if (din != null) {
					din.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bm;
	}

	public String getName() {
		return name;
	}
}