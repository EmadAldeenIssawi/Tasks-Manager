package com.tasksmanager.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DailyTasksInsert extends AppCompatActivity {

    private EditText editText;
    private String des="";
    private String incomigIntent="";
    Intent incomingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tasks_insert);
        Intent incomingIntent = getIntent();
        String senderTemp = incomingIntent.getStringExtra("sender");
        if(senderTemp!=null){
           incomigIntent=senderTemp;
        }

        // make the window smaller
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width= dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));
        editText=findViewById(R.id.DailyMissionDes);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(incomigIntent.equals("DailyTasksPage")){
            Intent i= new Intent(this,DailyTasksPage.class);
            startActivity(i);
        }else if(incomigIntent.equals("MonthlyTasksPage")){
            Intent i = new Intent(this,MonthlyTasksPage.class);
            startActivity(i);


        }
    }

    /**
     * saveDailyMission Save button Function which save the event
     * Toast "Max 8 Mission" if the user would insert more than 8 missions and finish the function
     * toast "Enter a mission" if the user left the editText empty
     * otherWise set the description to des Variable and add it to the dailyMission arrayList
     * @param v:View? the Save Button
     */
    public void saveDailyMission(View v){

        if(String.valueOf(editText.getText()).isEmpty()){
            Toast.makeText(this,"Enter a mission",Toast.LENGTH_LONG).show();
        }
        else{
            setDes(String.valueOf(editText.getText()));
            if(incomigIntent.equals("DailyTasksPage")){
                Intent i= new Intent(this,DailyTasksPage.class);
                i.putExtra("eventDescription",des);
                startActivity(i);
            }else if(incomigIntent.equals("MonthlyTasksPage")){
                Intent i = new Intent(this,MonthlyTasksPage.class);
                i.putExtra("eventDescription",des);
                startActivity(i);


            }

           // Intent cE2 = new Intent(this,MonthlyTasksPage.class);

            //cE2.putExtra("eventDescription",des);

            //DailyTasksPage.getDailyMission().add(des);


        }

    }

    public void setDes(String des) {
        this.des = des;
    }

}