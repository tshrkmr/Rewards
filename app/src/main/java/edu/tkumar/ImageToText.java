package edu.tkumar;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageToText{

    private Bitmap bitmap;

    public ImageToText(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String toBase64(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        String imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imageString64;
    }
}
