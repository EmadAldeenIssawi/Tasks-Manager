package com.tasksmanager.v2;


import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;

public  abstract  class TasksOperations extends AppCompatActivity {
    /**
     *removeMissions which either will remove selected missions or all mission based on the parameter which refers to the order
     * if the order remove and the checkBox is checked so remove the text from the tasks Arraylist
     * if the order is clear so remove the text anyway
     * set the checkBox visibility and check status to false a and the empty the text
     * set the rest of the tasks in the checkBoxes and make the Visible
     * reset the booleans values to zeros referring that the select process is done
     * make the progressbar zero
     * @param doFun the order which will be either "remove" which removes the checked tasks or clear which removes all tasks
     * @param linearLayout which has all the events text views in it
     * @param tasks the arraylist that has the events
     * @param booleans the arraylist that has the values 0 or 1 for every checkbox if it checked or not
     * @param progressBar the progressbar that changes based on how many tasks left to do
     */
    public void removeMissions(String doFun, LinearLayout linearLayout, ArrayList<Object> tasks,ArrayList<Integer> booleans , ProgressBar progressBar) {
        for (int j=0; j< linearLayout.getChildCount();j++) {
            CheckBox c =(CheckBox) linearLayout.getChildAt(j);
            if ((c.isChecked() && Objects.equals(doFun, "remove")) || Objects.equals(doFun, "clear")) {
                tasks.remove(c.getText().toString());
            }
            c.setVisibility(View.INVISIBLE);
            c.setChecked(false);
            c.setText("");
        }
        for (int i=0;i<tasks.size();i++ ) {
            CheckBox checkBox =(CheckBox) linearLayout.getChildAt(i) ;
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setText((CharSequence) tasks.get(i));
        }
        booleans.clear();
        progressBar.setProgress(0);

    }

    /**
     * upload the activity data booleanDCB and dailyMission to thr main map appData
     * save the data by calling saveAppData()
     * @param booleansArrayListName the name of the booleans arraylist
     * @param tasksArrayListName the name of the tasks arraylist
     * @param tasks the arraylist that has the events
     * @param booleans the arraylist that has the values 0 or 1 for every checkbox if it checked or not
     */
    public static void updateAppData(String booleansArrayListName, String tasksArrayListName, ArrayList<Object> tasks, ArrayList<Integer> booleans){
        ArrayList<Integer> kkk = (ArrayList<Integer>) booleans.clone();
        if(!MainActivity.appData.containsKey(booleansArrayListName)) {
            MainActivity.appData.put(booleansArrayListName, kkk);
        } else{
            MainActivity.appData.replace(booleansArrayListName,kkk);
        }
        if(!MainActivity.appData.containsKey(tasksArrayListName)) {
            MainActivity.appData.put(tasksArrayListName, tasks);
        } else{
            MainActivity.appData.replace(tasksArrayListName,tasks);
        }
        MainActivity m = new MainActivity();
        m.saveAppData();
    }

    /**
     * update the checkBoxes Check state based on the the array list bb contain if its 1 change to true otherwise to false
     * @param linearLayout which has all the events text views in it
     * @param booleans the arraylist that has the values 0 or 1 for every checkbox if it checked or not
     * @param tasks the arraylist that has the events
     * @param listener the listener which will all the checkboxes do when they are checked
     */
    public void updateCheckBoxes(LinearLayout linearLayout, ArrayList<Integer> booleans, ArrayList<Object> tasks, CompoundButton.OnCheckedChangeListener listener) {
        linearLayout.removeAllViews();
        ArrayList<Integer> bb = new ArrayList<>();
        for (int i = 0; i < booleans.size() ; i++) {
            bb.add(i, booleans.get(i));
        }
        for (int i = 0; i <tasks.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setOnCheckedChangeListener(listener);
            linearLayout.addView(checkBox);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setText((CharSequence) tasks.get(i));
            checkBox.setChecked(false);
            try{
                if(bb.get(i)==1) checkBox.setChecked(true);

            }catch(Exception ignored){}
        }
    }

    /**
     * turn on or off the alarm based on the progress bar value
     * @param tasks the arraylist that has the events
     * @param progressBar the progressbar that changes based on how many tasks left to do
     */
    public void alarmManager(ArrayList<Object> tasks,ProgressBar progressBar){
        if(tasks.isEmpty()|| progressBar.getProgress()==100){
            MainActivity m = new MainActivity();
            m.stopAlarmManager(MainActivity.alarmManagerDaily,1);
        }
        else if(!tasks.isEmpty() && progressBar.getProgress()!=100){
            MainActivity m = new MainActivity();
            m.resumeAlarmManager(MainActivity.alarmManagerDaily,1);
        }
    }

    /**
     * function which download the data from the main appData map for the tasks ArrayList and return the arraylist
     * @param tasksArrayListName name of the key for the tasks arraylist
     * @return array list including the data from the Main.appData
     */
    public ArrayList<Object> downloadTasksData(String tasksArrayListName) {
        ArrayList<Object> tasks= new ArrayList<>();
        if(MainActivity.appData.containsKey(tasksArrayListName)){
            tasks=MainActivity.appData.get(tasksArrayListName);
        }
        return tasks;
    }

    /**
     * function which download the data from the main appData map for the tasks ArrayList and return the arraylist
     * @param booleansArrayListName name of the key for the booleans arraylist
     * @return array list including the data from the Main.appData
     */
    public ArrayList<Integer> downloadBooleansData(String booleansArrayListName) {
        ArrayList<Integer> booleans = new ArrayList<>();
        if(MainActivity.appData.containsKey(booleansArrayListName)){
            for (int i = 0; i < Objects.requireNonNull(MainActivity.appData.get(booleansArrayListName)).size() ; i++) {
                Object d = Objects.requireNonNull(MainActivity.appData.get(booleansArrayListName)).get(i);
                double dd=Double.parseDouble(d.toString());
                booleans.add(i, (int) dd);
            }
        }
        return booleans;
    }
    /**
     * update the removeButton and Clear all button visibility based on it there any mission inserted or not
     */
    public void updateButtonsVisibility(ArrayList<Object> tasks, Button removeButton, Button clearButton) {
        if (tasks.size() > 0) {
            removeButton.setVisibility(View.VISIBLE);
            clearButton.setVisibility(View.VISIBLE) ;
        } else {
            removeButton.setVisibility(View.INVISIBLE);
            clearButton.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * updateProgressBar function which update the progressBar based on the how many mission are checked off all mission and make it as percentage
     */
    public void updateProgressBar(ArrayList<Object> tasks, ProgressBar progressBar, int numberTasksChecked) {
        if(tasks.size()>0) {
            double dailyMissionProgressPercent = ((double) numberTasksChecked / (double) tasks.size()) * 100;
            progressBar.setProgress((int) dailyMissionProgressPercent);
        }
        else{
            progressBar.setProgress(0);
        }
    }
    /**
     * updateBooleansDCB function which update booleanDCB values based on if the checkBoxes is checked or not -> checked =1 otherwise =0
     */
    public int updateBooleans(LinearLayout linearLayout, ArrayList<Integer> booleans) {
        booleans.clear();
        int numberDailyMissionChecked = 0;
        for (int j=0;j<linearLayout.getChildCount();j++) {
            CheckBox c =(CheckBox) linearLayout.getChildAt(j);

            if (c.isChecked() ) {
                numberDailyMissionChecked++;
                booleans.add(j,1);
            } else {
                booleans.add(j,0);
            }
        }
        return numberDailyMissionChecked;
    }
    public void addTaskDescription(ArrayList<Object> tasks,String descriptionTemp, Intent incomingIntent){
        if(descriptionTemp!=null){
            tasks.add(descriptionTemp);
            incomingIntent.removeExtra("eventDescription");

        }
    }

    /**
     * change checkBox color to gray if it is checked
     * @param linearLayout
     */
    public void checkboxChangeColor (LinearLayout linearLayout){
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            CheckBox c = (CheckBox) linearLayout.getChildAt(i);
            if (c.isChecked()) {
                c.setBackgroundColor(Color.GRAY);
            } else if (!c.isChecked()) {
                c.setBackgroundColor(Integer.parseInt(String.valueOf(0xC3C6C8)));
            }
        }
    }
}
