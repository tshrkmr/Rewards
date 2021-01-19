package edu.tkumar;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageToText implements Runnable{

    private Bitmap bitmap;
    private final CreateProfileActivity createProfileActivity;

    public ImageToText(Bitmap bitmap, CreateProfileActivity createProfileActivity) {
        this.bitmap = bitmap;
        this.createProfileActivity = createProfileActivity;
    }

    @Override
    public void run() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        String imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        createProfileActivity.runOnUiThread(()->createProfileActivity.setImageBytes(imageString64));
    }

//    public String toBase64(){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] byteArray = baos.toByteArray();
//        String imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        return imageString64;
//    }
}
