package cz.cvut.rutkodan.bakalarka;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CameraStream {
	private InputStream din;
	private URLConnection ucon;
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
			this.ucon = url.openConnection();			
			this.din = ucon.getInputStream();
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
			/*
			 * for (int i = 0; i < 100000; i++) { fs.append((char)din.read()); }
			 */

			name = fs.toString();
			din.close();
			Bitmap bm = BitmapFactory.decodeByteArray(ab, 0, ab.length);
			return bm;
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			try {
				din.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getName() {
		return name;

	}
}
