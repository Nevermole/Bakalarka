package cz.cvut.rutkodan.bakalarka;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CameraStream {
	private InputStream din;
	private String name;
	private URL url;

	public CameraStream(String url) {
		super();
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Bitmap getData() {
		try {			
			this.din = url.openStream();
			StringBuilder fs = new StringBuilder();
			int s = 0;
			boolean ok = true;

			while (ok) {
				if (s == '-' && din.read() == '-' && din.read() == 'm') {
					ok = false;
				}
				s = din.read();
				fs.append((char) s);
			}
			while (s != 'L') {
				s = din.read();
				fs.append((char) s);
			}
			// din.skip(7);
			for (int i = 0; i < 7; i++) {
				fs.append((char) din.read());
			}
			int lenght = 0;
			while (s != '\n') {
				s = din.read();
				fs.append((char) s);
				if (s >= 48 && s <= 57) {
					lenght *= 10;
					lenght += (s - 48);
				}
			}
			byte[] ab = new byte[lenght];
			// din.read();
			// din.read();
			fs.append((char) din.read());
			fs.append((char) din.read());
			byte b = (byte) din.read();
			System.out.println("try");
			for (int i = 0; i < lenght; i++) {
				ab[i] = b;
				fs.append((char) b);
				b = (byte) (din.read());
			}
			name = fs.toString();
			din.close();
			Bitmap bm = BitmapFactory.decodeByteArray(ab, 0, ab.length);			
			if (bm != null) {
				return bm;
			} else {
				return getData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return getData();
		} finally {
			try {
				din.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	public String getName() {
		return name;

	}
}
