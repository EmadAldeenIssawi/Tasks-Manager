package com.tasksmanager.v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TasksInsert extends AppCompatActivity {

    private EditText editText;
    private String des="";
    private String incomingIntent ="";

    /**
     * initialize the global variables
     * make window smaller
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_tasks_insert);
        Intent incomingIntent = getIntent();
        String senderTemp = incomingIntent.getStringExtra("sender");
        if(senderTemp!=null){
           this.incomingIntent =senderTemp;
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width= dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));
        editText=findViewById(R.id.DailyMissionDes);
    }

    /**
     * if the user was on DailyTaskPage so go back there
     * otherwise go back to MonthlyTasksPage
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent();
        if(incomingIntent.equals("DailyTasksPage"))
            i= new Intent(this,DailyTasksPage.class);
        else if(incomingIntent.equals("MonthlyTasksPage"))
            i = new Intent(this,MonthlyTasksPage.class);
        startActivity(i);
    }

    /**
     * set the input to the global description
     * @param des event description
     */
    private void setDes(String des) {
        this.des = des;
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
        } else{
            des =String.valueOf(editText.getText()).replace("\n", "");
            setDes(des);
            Intent i=null;
            if(incomingIntent.equals("DailyTasksPage")) i= new Intent(this,DailyTasksPage.class);
            else if(incomingIntent.equals("MonthlyTasksPage")) i = new Intent(this,MonthlyTasksPage.class);
            assert i != null;
            i.putExtra("eventDescription",des);
            startActivity(i);
        }
    }
}