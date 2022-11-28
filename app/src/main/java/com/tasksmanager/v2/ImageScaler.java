package com.tasksmanager.v2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;

public  class ImageScaler extends AsyncTask<File, Void, Bitmap> {

    private ImageView view;
    private int height;
    private int width;


    public ImageScaler(ImageView view) {
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
            // We only recognize a subset of orientation tag values
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
        //Läs in bilden som nu bör finnas där vi sa att den skulle placeras
        Bitmap bm= BitmapFactory.decodeFile(file[0].getAbsolutePath());
        Bitmap rotatedBitmap;
        int orientation = getExifRotation(file[0]);

        Matrix matrix = new Matrix();
        if(orientation!=0) {
            matrix.postRotate(orientation);
            rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            bm = null;
        }
        else {
            rotatedBitmap=bm;
        }

        //Skala om bilden så att den passar i imageviewn. Observera att getWidth och  getHeight
        //ej kommer ge korrekta värden förrän från onResume
        return Bitmap.createScaledBitmap(rotatedBitmap,  width, height,true);


    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }
}