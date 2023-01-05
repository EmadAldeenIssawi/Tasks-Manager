package com.tasksmanager.v2;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;



public class MainActivity extends AppCompatActivity {
    public static int setCount=0; // help Variable refers that the user clicked the selectBackground button
    private Window window;
    private View screenView;
    private Button removeBackgroundButton ;
    public static SharedPreferences sharedPreferences;
    public static HashMap<String, ArrayList> appData = new HashMap<>();
    public static AlarmManager alarmManagerDaily;
    public static AlarmManager alarmManagerMonthly;
    public static PendingIntent pendingIntentDaily;
    public static PendingIntent pendingIntentMonthly;
    private Menu menu;
    private MenuItem removeOption;
    public static Intent iD;
    public static Intent iM;
    public static String alarmMassage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //initialize the components and the shared Preferences
        screenView=findViewById(R.id.background);
        window=getWindow();
        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        // load back the appData data from the sharedPreferences and load back the background if is there any
        loadAppData();
        downloadBackGround();
        //clearAppData();

        //initialize the alarm components
        createNotificationChannel();
        alarmMassage="You have unfinished daily task to do!";
        iD = new Intent(MainActivity.this, AlarmManagerReminder.class);
        iD.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmMassage="You have unfinished monthly task to do!";
        iM =new Intent(MainActivity.this,AlarmManagerReminder.class);
        iM.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        pendingIntentDaily = PendingIntent.getBroadcast(MainActivity.this, 0, iD, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        //pendingIntentDaily = PendingIntent.getBroadcast(MainActivity.this, 0, iD, 0);

        pendingIntentMonthly= PendingIntent.getBroadcast(MainActivity.this,0,iM,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManagerDaily = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManagerMonthly=(AlarmManager) getSystemService(Context.ALARM_SERVICE);


    }
    /**
     * download the Image as A string from the appData map
     * decode the string and convert it to Bitmap
     * set the Bitmap into the help ImageView
     * set the background Drawable as the helpImageView Drawable
     */
    @SuppressLint("ResourceType")
    private void downloadBackGround(){
        if(appData.containsKey("backgroundImage")){
            String s = (String) appData.get("backgroundImage").get(0);
            byte[] imageBytes = Base64.getDecoder().decode(s);
            Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ImageView loadedImageView = findViewById(R.id.helpImageView);
            loadedImageView.setVisibility(View.INVISIBLE);
            loadedImageView.setImageBitmap(image);
            screenView.setBackground(loadedImageView.getBackground());
            window.setBackgroundDrawable(loadedImageView.getDrawable());
            if(removeOption!=null)removeOption.setVisible(true);

        }
    }



    /**
     * if the user took a picture or selected a picture from the gallery so set it as a window background
     */
    @SuppressLint("ResourceType")
    private void updateBackground(){
        BackgroundPage backgroundPage =  new BackgroundPage();
        if (backgroundPage.getUserImageView().getDrawable() != null) {
            ImageView im = backgroundPage.getUserImageView();
            screenView.setBackground(im.getBackground());
            window.setBackgroundDrawable(im.getDrawable());
            if(removeOption!=null) removeOption.setVisible(true);

        }
    }

    /**
     * Remove the background image from the sharedPreferences and update the main page
     */
    public void removeBackground(){
        if(appData.containsKey("backgroundImage")){
            appData.remove("backgroundImage");
            saveAppData();
            setContentView(R.layout.activity_main);


        }
    }

    public void stopAlarmManager(AlarmManager alarmManager, int alarm) {
        if (alarm == 1) {
            if (alarmManager != null)
                alarmManager.cancel(pendingIntentDaily);
        }
        else if(alarm==2) {
            if (alarmManager != null)
                alarmManager.cancel(pendingIntentMonthly);
        }
    }

    public void resumeAlarmManager( AlarmManager alarmManager,int alarm) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Calendar calendar = Calendar.getInstance();

            if(alarm==1) {
                calendar.set(Calendar.HOUR_OF_DAY, 16);
                calendar.set(Calendar.MINUTE,1);
                calendar.set(Calendar.SECOND,1);
                calendar.set(Calendar.MILLISECOND,1);

                 alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24, pendingIntentDaily);
                 //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60*3, pendingIntentDaily);
            }
            else if(alarm==2){

                calendar.set(Calendar.DAY_OF_MONTH,25);
                calendar.set(Calendar.HOUR,16 );
                calendar.set(Calendar.MINUTE, 1);
                calendar.set(Calendar.SECOND, 1);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24, pendingIntentMonthly);
            }


        }
    }

    public void createNotificationChannel(){
        CharSequence name = "ReminderChannel";
        String description=" Channel for reminding";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel= new NotificationChannel("channel2",name,importance);
        channel.setDescription(description);
        NotificationManager notificationManager= getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        /* CharSequence name = "ReminderChannel";
        String description=" Channel for reminding";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel= new NotificationChannel("channel2",name,importance);
        Uri sound1 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.sound1);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        channel.setSound(sound1,attributes);

        channel.setDescription(description);
        NotificationManager notificationManager= getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        */
    }
    /**
     * save the application data in sharedPreferences as a HashMap<String, ArrayList<*>>() using Gson
     */
    public void saveAppData(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        GsonBuilder builder= new GsonBuilder()
                .registerTypeAdapter(LocalTime.class,new LocalTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        Gson gson=builder.create();
        String json= gson.toJson(appData);
        editor.putString("data",json);
        editor.apply();
    }


    /**
     * load back the appData data from the sharedPreferences
     */
    public  void loadAppData(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        if(sharedPreferences.getString("data",null)!=null) {
            String json = sharedPreferences.getString("data", null);
            Type type = new TypeToken<HashMap<String,ArrayList>>(){}.getType();
            appData = gson.fromJson(json, type);
            System.out.println("");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backgroundmenu,menu);
        removeOption=menu.getItem(1);
        if(!appData.containsKey("backgroundImage")){
            removeOption.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setBackgroundButtonOption:
                setBackgroundGo();
                return true;
            case R.id.removeBackgroundButtonOption:
                removeBackground();
                removeOption.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }

    }

    /**
     * clear the sharedPreferences
     */
    public void clearAppData(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Calendar  buttons function which goes to the Calendar Activity using intent
     * @param v Calendar button
     */
    public void calenderPageGo(View v){
        Intent i = new Intent(this, com.tasksmanager.v2.Calendar.class);
        startActivity(i);
    }
    /**
     * Daily Mission buttons function which goes to the Daily Missions page Activity using intent
     * @param v Daily mission button
     */
    public void DailyMissionsPageGo(View v){
        Intent i = new Intent(this,DailyTasksPage.class);
        startActivity(i);
    }
    /**
     * Monthly Mission buttons function which goes to the Daily Missions page Activity using intent
     * @param v Daily mission button
     */
    public void monthlyMissionsPageGo(View v){
        Intent i = new Intent(this,MonthlyTasksPage.class);
        startActivity(i);
    }
    /**
     * set Background buttons function which goes to Background Activity using intent
     */
    public void setBackgroundGo(){

        Intent i  =  new Intent(this,BackgroundPage.class);
        startActivity(i);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (setCount!=0) {
            updateBackground();
            setCount=0;
        }
    }
}
