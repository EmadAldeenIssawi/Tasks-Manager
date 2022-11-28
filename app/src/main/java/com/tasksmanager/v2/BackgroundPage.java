package com.tasksmanager.v2;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;

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
    private void initialize(){
        userImageView=findViewById(R.id.userImageView);
        startForResult=registerForActivityResult(new ActivityResultContracts.TakePicture(),(it)-> {
            if (it) {
                //Bilden sparad till den plats vi angav i intentet. Bilden kommer bytas ut
                //Då aktiviteten blir synlig
            }
        });
        //Plats för bildfil
        file = new File(getFilesDir(), "mypic.jpg");
    }
    public ImageView getUserImageView() {
        return userImageView;
    }

    public void takePicture(View view) {
        Uri uri;

        // Spara filen i vår local
        // storage med en content-url. Det låter de oss spara filen var som helst utan att behöva
        // bry oss om att kameraappen har rättighet at skriva dit. Dessutom har vi
        // om vi sparar filen på ett ställe vi har koll på kontroll över att ingen
        // annan app sabbar något

        uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", file);

        //Starta aktiviteten
        startForResult.launch(uri);
    }

    /**Se till så att vi visar bilden även efter texrotation. Behöver veta
     * storleken på ImageViewn
     * Därför gjort detta i denna metod istället för onCreate tex då
     * detta värde inte finns tillgängligt då
     */

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (file != null && file.exists() && hasFocus)
            updateImageViewFromFile();
    }



    private void updateImageViewFromFile() {
        ImageView imView = findViewById(R.id.userImageView);

        //Läs in bilden som nu bör finnas där vi sa att den skulle placeras
        new ImageScaler(imView).execute(file);
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
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
        ArrayList<String > kkk =  new ArrayList<String>();
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
        if (order=="selectPicture"){
            if (resultCode == Activity.RESULT_OK && requestCode == 100){

                userImageView.setImageURI(data.getData()); // handle chosen image
                Uri urii=data.getData();
                        selectedImageUri= urii;

            }
        }

    }

}

