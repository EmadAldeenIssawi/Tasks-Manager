package com.tasksmanager.v2;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;

public  class ImageScalar extends AsyncTask<File, Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    private final ImageView view;
    private int height;
    private int width;

    public ImageScalar(ImageView view) {
        this.view=view;
    }

    protected void onPreExecute() {
        width=view.getWidth();
        height=view.getHeight();
    }

    private int getExifRotation(File imageFile) {
        if (imageFile == null) return 0;
        try {
            ExifInterface exif = new ExifInterface(imageFile);
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    protected Bitmap doInBackground(File... file) {
        Bitmap bm= BitmapFactory.decodeFile(file[0].getAbsolutePath());
        Bitmap rotatedBitmap;
        int orientation = getExifRotation(file[0]);
        Matrix matrix = new Matrix();
        if(orientation!=0) {
            matrix.postRotate(orientation);
            rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        }
        else {
            rotatedBitmap=bm;
        }
        return Bitmap.createScaledBitmap(rotatedBitmap,  width, height,true);
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }
}