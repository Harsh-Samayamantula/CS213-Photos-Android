package com.example.android85.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class BitmapSerialized implements Serializable {
	private Bitmap bitmap;

	public BitmapSerialized(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		oos.writeInt(byteArray.length);
		oos.write(byteArray);
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		int bufferLength = ois.readInt();
		byte[] byteArray = new byte[bufferLength];
		int pos = 0;
		do {
			int read = ois.read(byteArray, pos, bufferLength - pos);
			if (read != -1) {
				pos += read;
			} else {
				break;
			}
		} while (pos < bufferLength);
		bitmap = BitmapFactory.decodeByteArray(byteArray, 0, bufferLength);
	}
}
