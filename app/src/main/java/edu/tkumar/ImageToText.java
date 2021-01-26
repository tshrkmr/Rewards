package edu.tkumar;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageToText{

    private final Bitmap bitmap;
    private String imageString64;
    private static final String TAG = "ImageToText";

    public ImageToText(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String toBase64(){
        int value = 60;
        Log.d(TAG, "toBase64: running" );
        while(value>0) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, value, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            if(imageString64.length()>100000){
                Log.d(TAG, "toBase64: " + imageString64.length());
                value -= 10;
            }else{
                break;
            }
        }
        return imageString64;
    }
}
