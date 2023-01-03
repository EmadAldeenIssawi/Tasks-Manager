package com.tasksmanager.v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;


public class DailyTasksPage extends AppCompatActivity {

    private static ArrayList dailyMission= new ArrayList<>();//the arraylist that has missions
    private ProgressBar dailyProgressBar;// the progress bar that view how much did the user process of all the missions
    private Button removeButtonDaily;// the remove trash button which removes the selected missions
    private Button clearAllButtonDaily;// the clear all button which removes all the missions
    private int numberDailyMissionChecked =0;// number of marked missions
    private static final ArrayList<Integer> booleanDCB =new ArrayList<>();// help array for the checked boxes when 0 refers to unchecked and 1 the opposite
    private  LinearLayout linearLayout;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_daily_tasks_page);
            //initialize the components and download the data from the main AppData map
            initializeComponent();
            downloadDailyMissionsData();


        }
    /**
     * upload the activity data booleanDCB and dailyMission to thr main map appData
     * save the data by calling saveAppData()
     */
    private void updateAppData(){
        ArrayList<Integer> kkk = (ArrayList<Integer>) booleanDCB.clone();
        if(!MainActivity.appData.containsKey("booleanDCB")) {
            MainActivity.appData.put("booleanDCB", kkk);
        } else{
            MainActivity.appData.replace("booleanDCB",kkk);
        }
        if(!MainActivity.appData.containsKey("dailyMission")) {
            MainActivity.appData.put("dailyMission", dailyMission);
        } else{
            MainActivity.appData.replace("dailyMission",dailyMission);
        }
        MainActivity m = new MainActivity();
        m.saveAppData();
    }
    /**
     * initializeComponent which initialize the component and set the progressBar to the max value
     * set the onCheckListener for the checkBoxes and call dailyMissionCheck() for every check
     */
    @SuppressLint("CutPasteId")
    private void initializeComponent() {
        dailyProgressBar = findViewById(R.id.DailyProgressBar);
        removeButtonDaily=findViewById(R.id.removeDailyMissions);
        clearAllButtonDaily=findViewById(R.id.clearAllDailyButton);
        linearLayout=(LinearLayout) ((ScrollView) findViewById(R.id.dailyTasksScrollview)).getChildAt(0);
        dailyProgressBar.setMax(100);

        for (int i = 0; i <linearLayout.getChildCount() ; i++) {
            CheckBox c =(CheckBox) linearLayout.getChildAt(i);
            c.setOnCheckedChangeListener(new DailyMissionCheck());
        }
    }
    /**
     * removeDailySelectedMissions trash button which will remove the checked Missions
     * Remove the selected Mission by calling removeMissions("remove") with the order Remove referring to the removing selected missions only
     * update Buttons Visibility
     * upload App Data
     * @param v: View? Trash button
     */
    public void removeDailySelectedMissions(View v){
        removeMissions("remove");
        updateButtonsVisibility();
        updateAppData();
        alarmManager();

    }
    /**
     * clearAllDailySelectedMissions clear All button which will remove all the Missions
     * Remove all Mission by calling removeMissions("clear") with the order clear referring to the removing all missions
     * update Buttons Visibility
     * upload App Data
     * @param v: View? clear all button
     */
    public void clearAllDailySelectedMissions(View v){
        removeMissions("clear");
        updateButtonsVisibility();
        updateAppData();
        alarmManager();


    }
    /**
     * update the checkBoxes Check state based on the the array list bb contain if its 1 change to true otherwise to false
     */
    private void updateCheckBoxes() {
        linearLayout.removeAllViews();
        ArrayList<Integer> bb = new ArrayList<Integer>();
        for (int i = 0; i < booleanDCB.size() ; i++) {
            bb.add(i, booleanDCB.get(i));
        }
        for (int i = 0; i <dailyMission.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setOnCheckedChangeListener(new DailyMissionCheck());
            linearLayout.addView(checkBox);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setText(getDailyMission().get(i));
            checkBox.setChecked(false);
            try{
                if(bb.get(i)==1) checkBox.setChecked(true);

            }catch(Exception ignored){}
        }
    }
    /**
     * removeMissions which either will remove selected missions or all mission based on the parameter which refers to the order
     * if the order remove and the checkBox is checked so remove the text from the dailyMission Arraylist
     * if the order is clear so remove the text anyway
     * set the checkBox visibility and check status to false a and the empty the text
     * set the rest of the dailyMission missions in the checkBoxes and make the Visible
     * reset the booleanDCB values to zeros referring that the select process is done
     * make the progressbar zero
     * @param doFun: String the order as a String
     */
    private void removeMissions(String doFun) {
        for (int j=0; j< linearLayout.getChildCount();j++) {
            CheckBox c =(CheckBox) linearLayout.getChildAt(j);
            if ((c.isChecked() && Objects.equals(doFun, "remove")) || Objects.equals(doFun, "clear")) {
                dailyMission.remove(c.getText().toString());
            }
            c.setVisibility(View.INVISIBLE);
            c.setChecked(false);
            c.setText("");
        }
        for (int i=0;i<dailyMission.size();i++ ) {
            CheckBox checkBox =(CheckBox) linearLayout.getChildAt(i) ;
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setText(getDailyMission().get(i));
        }
        booleanDCB.clear();
        dailyProgressBar.setProgress(0);


    }
    /**
     * updateProgressBar function which update the progressBar based on the how many mission are checked off all mission and make it as percentage
     */
    private void updateProgressBar() {
        if(dailyMission.size()>0) {
            double dailyMissionProgressPercent =(double) ((double) numberDailyMissionChecked / (double) dailyMission.size()) * 100;
            // the current progressbar present
            dailyProgressBar.setProgress((int) dailyMissionProgressPercent);
        }
        else{
            dailyProgressBar.setProgress(0);
        }
    }
    /**
     * update the removeButton and Clear all button visibility based on it there any mission inserted or not
     */
    private void updateButtonsVisibility() {
        if (dailyMission.size() > 0) {
            removeButtonDaily.setVisibility(View.VISIBLE);
            clearAllButtonDaily.setVisibility(View.VISIBLE) ;
        } else {
            removeButtonDaily.setVisibility(View.INVISIBLE);
            clearAllButtonDaily.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * updateBooleansDCB function which update booleanDCB values based on if the checkBoxes is checked or not -> checked =1 otherwise =0
     */
    private void updateBooleansDCB() {
        booleanDCB.clear();
        numberDailyMissionChecked = 0;
        for (int j=0;j<linearLayout.getChildCount();j++) {
            CheckBox c =(CheckBox) linearLayout.getChildAt(j);

            if (c.isChecked() ) {
                numberDailyMissionChecked++;
                booleanDCB.add(j,1);
            } else {
                booleanDCB.add(j,0);
            }
        }
    }
    /**
     * downloadDailyMissionsData function which download the data from the main appData map for this activity and set it into the activity variables booleanDCB,dailyMission
     */
    private void downloadDailyMissionsData() {
        if(MainActivity.appData.containsKey("booleanDCB")||MainActivity.appData.containsKey("dailyMission")){
            for (int i = 0; i < booleanDCB.size() ; i++) {
                Object d = Objects.requireNonNull(MainActivity.appData.get("booleanDCB")).get(i);
                double dd=Double.parseDouble(d.toString());
                booleanDCB.set(i, (int) dd);
            }
            dailyMission=MainActivity.appData.get("dailyMission");
        }
    }
    /**
     * DailyMissionPageInsertGo Add Mission button function which goes to the insert Page
     * @param v: View Add Mission button
     */
    public void DailyMissionPageInsertGo(View v){
        Intent i = new Intent(this, TasksInsert.class);
        i.putExtra("sender","DailyTasksPage");
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent incomingIntent = getIntent();
        String descriptionTemp = incomingIntent.getStringExtra("eventDescription");
        if(descriptionTemp!=null){
            dailyMission.add(descriptionTemp);
            incomingIntent.removeExtra("eventDescription");

        }
        updateAppData();
        updateButtonsVisibility();
        updateProgressBar();
        updateCheckBoxes();
        alarmManager();


    }


    private void alarmManager(){
        if(dailyMission.isEmpty()|| dailyProgressBar.getProgress()==100){
            MainActivity m = new MainActivity();
            m.stopAlarmManager(MainActivity.alarmManagerDaily,1);
        }
        else if(!dailyMission.isEmpty() && dailyProgressBar.getProgress()!=100){
            MainActivity m = new MainActivity();
            m.resumeAlarmManager(MainActivity.alarmManagerDaily,1);
        }
    }
    public static ArrayList<String> getDailyMission() {
        return dailyMission;
    }

        class DailyMissionCheck implements CompoundButton.OnCheckedChangeListener{
            /**
             * dailyMissionCheck function which will work for every time the user check any mission
             * update the values in booleanDCB
             * update the progressBar
             * upload the current data
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateBooleansDCB();
                updateProgressBar();
                updateAppData();
                alarmManager();

            }
        }
    }




