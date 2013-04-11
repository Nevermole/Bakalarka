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
		Bitmap bm = null;
		try {
			this.din = this.url.openStream();
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
			System.out.println(fs.toString());
			for (int i = 0; i < lenght; i++) {
				ab[i] = b;
				fs.append((char) b);
				b = (byte) (din.read());
			}
			name = fs.toString();
			bm = BitmapFactory.decodeByteArray(ab, 0, ab.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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



package com.demo.mjpeg.MjpegView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class MjpegInputStream extends DataInputStream {
    private static final String TAG = "MjpegInputStream";

    private final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };
    private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
    private final String CONTENT_LENGTH = "Content-Length";
    private final static int HEADER_MAX_LENGTH = 100;
    private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
    private int mContentLength = -1;

    public MjpegInputStream(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }

    private int getEndOfSeqeunce(DataInputStream in, byte[] sequence) throws IOException {
        int seqIndex = 0;
        byte c;
        for(int i=0; i < FRAME_MAX_LENGTH; i++) {
            c = (byte) in.readUnsignedByte();
            if(c == sequence[seqIndex]) {
                seqIndex++;
                if(seqIndex == sequence.length) {
                    return i + 1;
                }
            } else {
                seqIndex = 0;
            }
        }
        return -1;
    }

    private int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
        int end = getEndOfSeqeunce(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    private int parseContentLength(byte[] headerBytes) throws IOException, NumberFormatException {
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }   

    public Bitmap readMjpegFrame() throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLen = getStartOfSequence(this, SOI_MARKER);
        reset();
        byte[] header = new byte[headerLen];
        readFully(header);
        try {
            mContentLength = parseContentLength(header);
        } catch (NumberFormatException nfe) { 
            nfe.getStackTrace();
            Log.d(TAG, "catch NumberFormatException hit", nfe);
            mContentLength = getEndOfSeqeunce(this, EOF_MARKER); 
        }
        reset();
        byte[] frameData = new byte[mContentLength];
        skipBytes(headerLen);
        readFully(frameData);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
    }
}
