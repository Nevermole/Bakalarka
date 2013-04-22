package cz.cvut.rutkodan.bakalarka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import cz.cvut.rutkodan.bakalarka.activities.MainActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class CameraStream {
	private String name;
	private URL url;

	public CameraStream(String url) {
		super();
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
			MainActivity.dataUsed += i + lenght;
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
			bm = BitmapFactory.decodeByteArray(ab, 0, ab.length, options);
			if (bm == null) {
				/*sb.append("\ndone\n");
				for (int j = 0; j < 10; j++) {
					b = (byte) (din.read());
					sb.append(b + " " + (char) b + ",");
				}
				System.err.println(sb.toString());*/
			} else {
				/*
				 * sb.append("\ndone\n"); for (int j = 0; j < 10; j++) { b =
				 * (byte) (din.read()); sb.append(b+" "+(char)b+","); }
				 * System.out.println(sb.toString());
				 */
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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

/*
 * 
 * package com.demo.mjpeg.MjpegView;
 * 
 * import java.io.BufferedInputStream; import java.io.ByteArrayInputStream;
 * import java.io.DataInputStream; import java.io.IOException; import
 * java.io.InputStream; import java.util.Properties;
 * 
 * import android.graphics.Bitmap; import android.graphics.BitmapFactory; import
 * android.util.Log;
 * 
 * public class MjpegInputStream extends DataInputStream { private static final
 * String TAG = "MjpegInputStream";
 * 
 * private final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 }; private final
 * byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 }; private final String
 * CONTENT_LENGTH = "Content-Length"; private final static int HEADER_MAX_LENGTH
 * = 100; private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
 * private int mContentLength = -1;
 * 
 * public MjpegInputStream(InputStream in) { super(new BufferedInputStream(in,
 * FRAME_MAX_LENGTH)); }
 * 
 * private int getEndOfSeqeunce(DataInputStream in, byte[] sequence) throws
 * IOException { int seqIndex = 0; byte c; for(int i=0; i < FRAME_MAX_LENGTH;
 * i++) { c = (byte) in.readUnsignedByte(); if(c == sequence[seqIndex]) {
 * seqIndex++; if(seqIndex == sequence.length) { return i + 1; } } else {
 * seqIndex = 0; } } return -1; }
 * 
 * private int getStartOfSequence(DataInputStream in, byte[] sequence) throws
 * IOException { int end = getEndOfSeqeunce(in, sequence); return (end < 0) ?
 * (-1) : (end - sequence.length); }
 * 
 * private int parseContentLength(byte[] headerBytes) throws IOException,
 * NumberFormatException { ByteArrayInputStream headerIn = new
 * ByteArrayInputStream(headerBytes); Properties props = new Properties();
 * props.load(headerIn); return
 * Integer.parseInt(props.getProperty(CONTENT_LENGTH)); }
 * 
 * public Bitmap readMjpegFrame() throws IOException { mark(FRAME_MAX_LENGTH);
 * int headerLen = getStartOfSequence(this, SOI_MARKER); reset(); byte[] header
 * = new byte[headerLen]; readFully(header); try { mContentLength =
 * parseContentLength(header); } catch (NumberFormatException nfe) {
 * nfe.getStackTrace(); Log.d(TAG, "catch NumberFormatException hit", nfe);
 * mContentLength = getEndOfSeqeunce(this, EOF_MARKER); } reset(); byte[]
 * frameData = new byte[mContentLength]; skipBytes(headerLen);
 * readFully(frameData); return BitmapFactory.decodeStream(new
 * ByteArrayInputStream(frameData)); } }
 */
