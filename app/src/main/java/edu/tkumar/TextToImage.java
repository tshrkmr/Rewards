package edu.tkumar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

public class TextToImage {

    private String imageString64;

    public TextToImage(String imageString64) {
        this.imageString64 = imageString64;
    }

    public Bitmap textToImage() {
        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return bitmap;
    }
}
