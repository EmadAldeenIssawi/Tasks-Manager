package com.tasksmanager.v2;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;


public class BackgroundPage extends AppCompatActivity {

    private ActivityResultLauncher<Uri> startForResult;
    private File file;
    private String order=""; // which order to be taken (take A picture / select  a picture
    @SuppressLint("StaticFieldLeak")
    private static ImageView userImageView;// the image view which has the selected/taking image in it
    private static Uri selectedImageUri ; // the selected Image Uri

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_page);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width= dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));
        initialize();
    }

    /**
     * initialize the components
     * initialize the file where the image will take a place
     */
    private void initialize(){
        userImageView=findViewById(R.id.userImageView);
        startForResult=registerForActivityResult(new ActivityResultContracts.TakePicture(),(it)-> {});
        file = new File(getFilesDir(), "mypic.jpg");
    }

    /**
     * @return the latest chosen image
     */
    public ImageView getUserImageView() {
        return userImageView;
    }

    /**
     * save file locally
     * storage with URL which let us save the file without considering if the camera has the right to write there
     * @param view TakePicture Button
     */
    public void takePicture(View view) {
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", file);
        startForResult.launch(uri);
    }

    /**
     * function which let us see the image even if the screen rotates
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (file != null && file.exists() && hasFocus)
            updateImageViewFromFile();
    }

    /**
     * read the image which is in the imageView userImageView
     * new scale for the image is called
     */
    private void updateImageViewFromFile() {
        ImageView imView = findViewById(R.id.userImageView);
        new ImageScalar(imView).execute(file);
    }

    /**
     * set background buttons function
     * update the data
     * start the Main activity
     * @param view:View
     */
    public void setBackground(View view){
        updateAppData();
        if (userImageView.getDrawable()!=null){
            MainActivity.setCount++;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    /**
     * encode Image function which converts the Bitmap to String by using the Base64 encoder
     * @param bm: Bitmap the image to be converted
     * @return  Base64.getEncoder().encodeToString(b) the string of the converted image
     */
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(b);
    }
    /**
     * updateAppData function which save the selected/taken image into  our sharedPreferences  as a string after convert the image
     * if its already saved so just replace it with the new current image
     */
    private void updateAppData() {
        Drawable d =userImageView.getDrawable();
        Bitmap bitmapTemp = ((BitmapDrawable)d).getBitmap();
        Bitmap bitmap = Bitmap.createScaledBitmap(bitmapTemp,bitmapTemp.getWidth(),bitmapTemp.getHeight(),false);

        String imageString = encodeImage(bitmap);
        ArrayList<String > kkk =  new ArrayList<>();
        kkk.add(imageString);
        if (!MainActivity.appData.containsKey("backgroundImage")) {
            MainActivity.appData.put("backgroundImage",kkk);
        } else {
            MainActivity.appData.replace("backgroundImage", kkk);
        }
        MainActivity m = new MainActivity();
        m.saveAppData();
    }
    /**
     * select Picture Button function
     * make the String "Order" = selectPicture
     * start the intent which open the gallery
     * @param view:View
     */
    public void selectPicture(View view){
        order="selectPicture";
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*") ;
        startActivityForResult(intent, 100);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
    @Override
    public void onActivityResult(int requestCode ,  int resultCode , Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);
        userImageView.setImageBitmap(null);
        if (Objects.equals(order, "selectPicture")){
            if (resultCode == Activity.RESULT_OK && requestCode == 100){
                userImageView.setImageURI(data.getData());
                selectedImageUri= data.getData();

            }
        }

    }

}

