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

import java.util.ArrayList;


public class DailyTasksPage  extends TasksOperations {

    private static ArrayList<Object> dailyMission= new ArrayList<>();//the arraylist that has missions
    private ProgressBar dailyProgressBar;// the progress bar that view how much did the user process of all the missions
    private Button removeButtonDaily;// the remove trash button which removes the selected missions
    private Button clearAllButtonDaily;// the clear all button which removes all the missions
    private static int numberDailyMissionChecked =0;// number of marked missions
    private static ArrayList<Integer> booleanDCB =new ArrayList<>();// help array for the checked boxes when 0 refers to unchecked and 1 the opposite
    private  LinearLayout linearLayout;

    /**
     * initialize the components and download the data from the main AppData map
     */
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_daily_tasks_page);
            initializeComponent();
            dailyMission= downloadTasksData("dailyMission");
            booleanDCB=downloadBooleansData("booleanDCB");


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
        removeMissions("remove",linearLayout,dailyMission,booleanDCB,dailyProgressBar);
        updateButtonsVisibility(dailyMission,removeButtonDaily,clearAllButtonDaily);
        updateAppData("booleanDCB","dailyMission",dailyMission,booleanDCB);
        alarmManager(dailyMission,dailyProgressBar);

    }
    /**
     * clearAllDailySelectedMissions clear All button which will remove all the Missions
     * Remove all Mission by calling removeMissions("clear") with the order clear referring to the removing all missions
     * update Buttons Visibility
     * upload App Data
     * @param v: View? clear all button
     */
    public void clearAllDailySelectedMissions(View v){
        removeMissions("clear",linearLayout,dailyMission,booleanDCB,dailyProgressBar);
        updateButtonsVisibility(dailyMission,removeButtonDaily,clearAllButtonDaily);
        updateAppData("booleanDCB","dailyMission",dailyMission,booleanDCB);
        alarmManager(dailyMission,dailyProgressBar);
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

    /**
     * go back to the main activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    /**
     * add event description to dailyMission array
     * update the data in the Main.appData
     * update buttons visibility based on the data
     * update the progress bar based on the dailyMission arraylist size
     * update the checkboxes status based on booleanDCB data
     */
    @Override
    protected void onResume() {
        super.onResume();
        addTaskDescription(dailyMission,getIntent().getStringExtra("eventDescription"),getIntent());
        updateAppData("booleanDCB","dailyMission",dailyMission,booleanDCB);
        updateButtonsVisibility(dailyMission,removeButtonDaily,clearAllButtonDaily);
        updateProgressBar(dailyMission,dailyProgressBar,numberDailyMissionChecked);
        updateCheckBoxes(linearLayout,booleanDCB,dailyMission, new DailyMissionCheck());
        alarmManager(dailyMission,dailyProgressBar);
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
                numberDailyMissionChecked= updateBooleans(linearLayout,booleanDCB);
                updateProgressBar(dailyMission,dailyProgressBar,numberDailyMissionChecked);
                updateAppData("booleanDCB","dailyMission",dailyMission,booleanDCB);
                alarmManager(dailyMission,dailyProgressBar);
            }
        }
}




