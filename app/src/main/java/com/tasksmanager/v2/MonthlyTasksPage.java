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

public class MonthlyTasksPage extends TasksOperations {


    private static  ArrayList<Object> monthlyMission = new ArrayList<>();//the arraylist that has missions
    private ProgressBar monthlyProgressBar;// the progress bar that view how much did the user process of all the missions
    private Button removeButtonMonthly;// the remove trash button which removes the selected missions
    private Button clearAllButtonMonthly;// the clear all button which removes all the missions
    private static int numberMonthlyMissionChecked =0;// number of marked missions
    private static ArrayList<Integer> booleanMCB =new ArrayList<>();// help array for the checked boxes when 0 refers to unchecked and 1 the opposite
    private  LinearLayout linearLayout;

    /**
     * initialize the components and download the data from the main AppData map
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_tasks_page);
        initializeComponent();
        monthlyMission= downloadTasksData("monthlyMission");
        booleanMCB=downloadBooleansData("booleanMCB");
    }

    /**
     * initializeComponent which initialize the component and set the progressBar to the max value
     * set the onCheckListener for the checkBoxes and call monthlyMissionCheck() for every check
     */
    @SuppressLint("CutPasteId")
    private void initializeComponent() {
        monthlyProgressBar = findViewById(R.id.MonthlyProgressBar);
        removeButtonMonthly =findViewById(R.id.removeMonthlyMissions);
        clearAllButtonMonthly =findViewById(R.id.clearAllMonthlyButton);
        linearLayout=(LinearLayout) ((ScrollView) findViewById(R.id.monthlyTasksScrollview)).getChildAt(0);
        monthlyProgressBar.setMax(100);
        for (int i = 0; i < linearLayout.getChildCount() ; i++) {
            CheckBox c =(CheckBox) linearLayout.getChildAt(i);
            c.setOnCheckedChangeListener(new MonthlyMissionCheck());
        }
    }

    /**
     * removeMonthlySelectedMissions trash button which will remove the checked Missions
     * Remove the selected Mission by calling removeMissions("remove") with the order Remove referring to the removing selected missions only
     * update Buttons Visibility
     * upload App Data
     * @param v: View? Trash button
     */
    public void removeMonthlySelectedMissions(View v){
        removeMissions("remove",linearLayout,monthlyMission,booleanMCB,monthlyProgressBar);
        updateButtonsVisibility(monthlyMission,removeButtonMonthly,clearAllButtonMonthly);
        updateAppData("booleanMCB","monthlyMission",monthlyMission,booleanMCB);
        alarmManager(monthlyMission,monthlyProgressBar);
    }

    /**
     * clearAllMonthlySelectedMissions clear All button which will remove all the Missions
     * Remove all Mission by calling removeMissions("clear") with the order clear referring to the removing all missions
     * update Buttons Visibility
     * upload App Data
     * @param v: View? clear all button
     */
    public void clearAllMonthlySelectedMissions(View v){
        removeMissions("clear",linearLayout,monthlyMission,booleanMCB,monthlyProgressBar);
        updateButtonsVisibility(monthlyMission,removeButtonMonthly,clearAllButtonMonthly);
        updateAppData("booleanMCB","monthlyMission",monthlyMission,booleanMCB);
        alarmManager(monthlyMission,monthlyProgressBar);
    }

    /**
     * MonthlyMissionPageInsertGo Add Mission button function which goes to the insert Page
     * @param v: View Add Mission button
     */
    public void monthlyMissionPageInsertGo(View v){
        Intent i = new Intent(this, TasksInsert.class);
        i.putExtra("sender","MonthlyTasksPage");
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
     * add event description to monthlyMission array
     * update the data in the Main.appData
     * update buttons visibility based on the data
     * update the progress bar based on the monthlyMission arraylist size
     * update the checkboxes status based on booleanMCB data
     */
    @Override
    protected void onResume() {
        super.onResume();
        addTaskDescription(monthlyMission,getIntent().getStringExtra("eventDescription"),getIntent());
        updateAppData("booleanMCB","monthlyMission",monthlyMission,booleanMCB);
        updateButtonsVisibility(monthlyMission,removeButtonMonthly,clearAllButtonMonthly);
        updateProgressBar(monthlyMission,monthlyProgressBar,numberMonthlyMissionChecked);
        updateCheckBoxes(linearLayout,booleanMCB,monthlyMission, new MonthlyMissionCheck());
        alarmManager(monthlyMission,monthlyProgressBar);
    }


    class MonthlyMissionCheck implements CompoundButton.OnCheckedChangeListener{
        /**
         * monthlyMissionCheck function which will work for every time the user check any mission
         * update the values in booleanMCB
         * update the progressBar
         * upload the current data
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            numberMonthlyMissionChecked=updateBooleans(linearLayout,booleanMCB);
            updateProgressBar(monthlyMission,monthlyProgressBar,numberMonthlyMissionChecked);
            updateAppData("booleanMCB","monthlyMission",monthlyMission,booleanMCB);
            alarmManager(monthlyMission,monthlyProgressBar);
            checkboxChangeColor(linearLayout);
        }
    }
}