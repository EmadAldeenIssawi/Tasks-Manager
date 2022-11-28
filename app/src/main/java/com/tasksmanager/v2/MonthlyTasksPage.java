package com.tasksmanager.v2;

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

public class MonthlyTasksPage extends AppCompatActivity {


    private static ArrayList<String> monthlyMission = new ArrayList<>();//the arraylist that has missions
    private ProgressBar MonthlyProgressBar;// the progress bar that view how much did the user process of all the missions
    private Button removeButtonMonthly;// the remove trash button which removes the selected missions
    private Button clearAllButtonMonthly;// the clear all button which removes all the missions
    private static int monthlyMissionProgressPercent =0;// the current progressbar present
    private int numberMonthlyMissionChecked =0;// number of marked missions
    private static int[] bolleansMCB =new int[]{0,0,0,0,0,0,0,0};// help array for the checked boxes when 0 refers to unchecked and 1 the opposite
    private static LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_tasks_page);
        //initialize the components and download the data from the main AppData map
        initializeComponent();
        downloadMonthlyMissionsData();


    }
    /**
     * upload the activity data booleanDCB and monthlyMission to thr main map appData
     * save the data by calling saveAppData()
     */
    private void updateAppData(){
        ArrayList<Integer> kkk = new ArrayList<Integer>();
        for (int i : bolleansMCB
        ) {
            kkk.add(i);
        }
        if(!MainActivity.appData.containsKey("bolleansMCB")) {
            MainActivity.appData.put("bolleansMCB", kkk);
        }
        else{
            MainActivity.appData.replace("bolleansMCB",kkk);

        }
        if(!MainActivity.appData.containsKey("monthlyDission")) {
            MainActivity.appData.put("monthlyMission", monthlyMission);
        }
        else{
            MainActivity.appData.replace("monthlyMission", monthlyMission);
        }
        MainActivity m = new MainActivity();
        m.saveAppData();
    }
    /**
     * initializeComponent which initialize the component and set the progressBar to the max value
     * set the onCheckListener for the checkBoxes and call monthlyMissionCheck() for every check
     */
    private void initializeComponent() {
        MonthlyProgressBar = findViewById(R.id.MonthlyProgressBar);
        removeButtonMonthly =findViewById(R.id.removeMonthlyMissions);
        clearAllButtonMonthly =findViewById(R.id.clearAllMonthlyButton);
        MonthlyProgressBar.setMax(100);
        linearLayout=(LinearLayout) ((ScrollView) findViewById(R.id.monthlyTasksScrollview)).getChildAt(0);

        for (int i = 0; i < linearLayout.getChildCount() ; i++) {

            CheckBox c =(CheckBox) linearLayout.getChildAt(i);
            c.setOnCheckedChangeListener(new MonthlyTasksPage.MonthlyMissionCheck());
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
        removeMissions("remove");
        updateButtonsVisibility();
        updateAppData();
        alarmManager();

    }
    /**
     * clearAllMonthlySelectedMissions clear All button which will remove all the Missions
     * Remove all Mission by calling removeMissions("clear") with the order clear referring to the removing all missions
     * update Buttons Visibility
     * upload App Data
     * @param v: View? clear all button
     */
    public void clearAllMonthlySelectedMissions(View v){
        removeMissions("clear");
        updateButtonsVisibility();
        updateAppData();
        alarmManager();


    }
    /**
     * update the checkBoxes Check state based on the the array list bb contain if its 1 change to true otherwise to false
     */
    private void updateCheckBoxes() {
        ArrayList<Integer> bb = new ArrayList<Integer>();
        for (int i = 0; i < bolleansMCB.length ; i++) {
            bb.add(i, bolleansMCB[i]);
        }
        for (int i = 0; i < monthlyMission.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setOnCheckedChangeListener(new MonthlyMissionCheck());
            linearLayout.addView(checkBox);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setText(getMonthlyMission().get(i));
            if(bb.get(i)==1){
                checkBox.setChecked(true);
            }
            else {
                checkBox.setChecked(false);
            }
        }
    }
    /**
     * removeMissions which either will remove selected missions or all mission based on the parameter which refers to the order
     * if the order remove and the checkBox is checked so remove the text from the monthlyMission Arraylist
     * if the order is clear so remove the text anyway
     * set the checkBox visibility and check status to false a and the empty the text
     * set the rest of the monthlyMission missions in the checkBoxes and make the Visible
     * reset the booleanDCB values to zeros referring that the select process is done
     * make the progressbar zero
     * @param doFun: String the order as a String
     */
    private void removeMissions(String doFun) {
        for (int j = 0; j< linearLayout.getChildCount(); j++) {

            CheckBox c =(CheckBox) linearLayout.getChildAt(j);
            if (c.isChecked() && doFun == "remove") {
                monthlyMission.remove(c.getText().toString());
            } else if (doFun == "clear") {
                monthlyMission.remove(c.getText().toString());
            }
            c.setVisibility(View.INVISIBLE);
            c.setChecked(false);
            c.setText("");
        }
        for (int i = 0; i< monthlyMission.size(); i++ ) {
            CheckBox checkBox =(CheckBox) linearLayout.getChildAt(0) ;
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setText(getMonthlyMission().get(i));
        }
        bolleansMCB = new int[]{0,0,0,0,0,0,0,0};
        MonthlyProgressBar.setProgress(0);
    }
    /**
     * updateProgressBar function which update the progressBar based on the how many mission are checked off all mission and make it as percentage
     */
    private void updateProgressBar() {
        if(monthlyMission.size()>0) {
            double x =(double) ((double) numberMonthlyMissionChecked / (double) monthlyMission.size()) * 100;
            monthlyMissionProgressPercent =(int) x;
            MonthlyProgressBar.setProgress(monthlyMissionProgressPercent);
        }
        else{
            MonthlyProgressBar.setProgress(0);
        }
    }
    /**
     * update the removeButton and Clear all button visibility based on it there any mission inserted or not
     */
    private void updateButtonsVisibility() {
        if (monthlyMission.size() > 0) {
            removeButtonMonthly.setVisibility(View.VISIBLE);
            clearAllButtonMonthly.setVisibility(View.VISIBLE) ;
        } else {
            removeButtonMonthly.setVisibility(View.INVISIBLE);
            clearAllButtonMonthly.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * updateBooleansDCB function which update booleanDCB values based on if the checkBoxes is checked or not -> checked =1 otherwise =0
     */
    private void updateBooleansDCB() {
        numberMonthlyMissionChecked = 0;
        for (int j = 0; j< linearLayout.getChildCount(); j++) {

            CheckBox c =(CheckBox) linearLayout.getChildAt(j);
            if (c.isChecked()) {
                numberMonthlyMissionChecked++;
                bolleansMCB[j]= 1;
            } else {
                bolleansMCB[j] = 0;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    /**
     * downloadMonthlyMissionsData function which download the data from the main appData map for this activity and set it into the activity variables booleanDCB,monthlyMission
     */
    private void downloadMonthlyMissionsData() {
        if(MainActivity.appData.containsKey("bolleansMCB")||MainActivity.appData.containsKey("monthlyMission")){
            for (int i = 0; i < bolleansMCB.length ; i++) {
                Object d = MainActivity.appData.get("bolleansMCB").get(i);
                Double dd=Double.parseDouble(d.toString());
                bolleansMCB[i]= dd.intValue();
            }
            monthlyMission =MainActivity.appData.get("monthlyMission");
        }
    }

    public  ArrayList<String>  getMonthlyMissionsArrayList(){
        return monthlyMission;
    }


    /**
     * MonthlyMissionPageInsertGo Add Mission button function which goes to the insert Page
     * @param v: View Add Mission button
     */
    public void monthlyMissionPageInsertGo(View v){
        Intent i = new Intent(this,DailyTasksInsert.class);
        i.putExtra("sender","MonthlyTasksPage");
        startActivity(i);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent incomingIntent = getIntent();
        String descriptionTemp = incomingIntent.getStringExtra("eventDescription");
        if(descriptionTemp!=null){
            monthlyMission.add(descriptionTemp);
        }
        updateAppData();
        updateButtonsVisibility();
        updateProgressBar();
        updateCheckBoxes();
        alarmManager();


    }
    private void alarmManager(){
        if(monthlyMission.isEmpty()|| MonthlyProgressBar.getProgress()==100){
            MainActivity m = new MainActivity();
            m.stopAlarmManager(MainActivity.alarmManagerMonthly,1);
        }
        else if(!monthlyMission.isEmpty() && MonthlyProgressBar.getProgress()!=100){
            MainActivity m = new MainActivity();
            m.resumeAlarmManager(MainActivity.alarmManagerMonthly,1);
        }
    }
    public static ArrayList<String> getMonthlyMission() {
        return monthlyMission;
    }

    class MonthlyMissionCheck implements CompoundButton.OnCheckedChangeListener{
        /**
         * monthlyMissionCheck function which will work for every time the user check any mission
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