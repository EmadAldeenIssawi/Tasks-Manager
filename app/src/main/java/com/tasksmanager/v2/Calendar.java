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
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Calendar extends AppCompatActivity {
    private CalendarView calendarview; // the calendar that the user use
    private  Month  selectedMonth; //the current selected month by the user
    private LocalDateTime datee; // current local time
    private CalendarEventInsert calendarEventInsert; // call for the CalendarEventsInsert class
    private ArrayList<Integer> checkVariables= new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    private ArrayList<Integer> helpCheckVariables=  new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));
    private int selectClick = 0 ;// help variable used in edit button function
    private Boolean restored=false; // help variable knowing that the screen is rotated and the restore function activated
    private  static LocalDate selectedDate;////the current selected date by the user
    public LocalDate getSelectedDate() {
        return selectedDate;
    }
    private Boolean thereCheckBoxChecked=false;
    private TextView eventsTextView,daysTextView;
    private  LinearLayout linearLayout2;//thescroll view that contains events description
    private  LinearLayout linearLayoutEventsDays; // the table layout that contains the date that contains events



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        create();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }


    /**
     * create function which will work at the opening or restoring the activity
     * will initialize the components
     * load the data from the shared preferences
     * set the onDateChangeListener for the calendarView
     * update the visibility for the button based on the current data
     */
    private void create() {
        initializeComponents();
        downloadAllData();
        calendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                LocalDate date = LocalDate.of(i,i1+1,i2);
                selectedDate=date;
                selectedMonth=date.getMonth();
                showEvents( );
                if(calendarEventInsert.monthlyEventsDaysTextViews.containsKey(selectedDate.getMonth())){
                    updateEventsDaysColor();
                }
                else {
                    emptyEventsDates();
                }
                updateButtonsVisibility();
            }
        });
    }

    /**
     * onSaveInstanceState function saving some data helping with restore process
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectVariable",selectClick);
        outState.putIntegerArrayList("checkVariables",checkVariables);
    }

    /**
     * onRestoreInstanceState function
     * call the Create function
     * load the data using the Bundle
     * change the selectClick value to 0 which will be used in selectButton function to make it as already clicked
     */
     @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        create();

        Object loadSelectVar = savedInstanceState.get("selectVariable");
        selectClick= (int)loadSelectVar ;
        //val loadCheckVariables = savedInstanceState.get("checkVariables")
        helpCheckVariables = (ArrayList<Integer>) savedInstanceState.get("checkVariables");

        if(selectClick==1) {
            selectClick = 0;
            restored = true;
        }
    }

    /**
     * initialize global variable
     * set an onCheck listener for each checkbox in the events tablelayout which will change visibility for Remove button based on if there any check box is checked or not
     */
    private void initializeComponents() {
        calendarview = (CalendarView) findViewById(R.id.calendarView2);
        eventsTextView=findViewById(R.id.eventTextView);
        daysTextView=findViewById(R.id.daysTextView);
        linearLayout2=(LinearLayout) ((ScrollView) findViewById(R.id.eventsScrollView)).getChildAt(0);
        linearLayoutEventsDays= (LinearLayout) ((ScrollView) findViewById(R.id.scrollViewEventsDays)).getChildAt(0);
        long l = calendarview.getDate();
        datee = LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
        selectedDate = LocalDate.from(datee);
        //val selectedDate2=selectedDate
        //selectedMonth = selectedDate2.month
        selectedMonth = selectedDate.getMonth();
        /*for (int i=0; i<linearLayout.getChildCount();i++) {
            TableRow r = (TableRow)linearLayout.getChildAt(i);
            CheckBox c = (CheckBox) r.getChildAt(0);
            c.setOnCheckedChangeListener(new EventCheck());

        }

         */
    }

    /**
     * empty the events on the events table first
     * check if the hash table for the events has a key as the selected date
     * if yes so print each event for the selected date in the text views which are in the events table
     */
    private void showEvents() {
        emptyEvents();
        if (calendarEventInsert.eventsOnDate.containsKey(selectedDate)) {
            String s = "";
            int ind=0;
            for (Event e : calendarEventInsert.eventsOnDate.get(selectedDate)
            ) {
                TableRow row = new TableRow(this);

                TextView textView = new TextView(this);
                s = "* "+e.date.toString() + "    " + e.Desccription + "    (" + e.time.toString() + ")\n";
                textView.setText(s);

                CheckBox checkBox = new CheckBox(this);
                checkBox.setOnCheckedChangeListener(new EventCheck());
              //TextViewCompat.setAutoSizeTextTypeWithDefaults(textView,TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                //row.addView(checkBox,0);
                //row.addView(textView,1);
                //row.addView(checkBox,100,100);
                //row.addView(textView,textView.getMaxWidth(),textView.getHeight());

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.FILL_PARENT);
                row.setLayoutParams(params);
                row.addView(checkBox,0);
                row.addView(textView,1);
                //row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                 //       TableRow.LayoutParams.MATCH_PARENT));
                linearLayout2.addView(row);

            }

        }
    }

    /**
     * select each row in the table that has date that has events
     * select each text view in each row
     * fill the text view text with the current number from the current index in the arraylist from the hashtable monthlyEventsDaysTextViews with key selected month
     * check if the current day of the current date bigger than the number in the textView
     * if yes so change the color of the number to grey which will mean that the date has passed
     * if no change it to the original color
     */
    private void updateEventsDaysColor() {
        emptyEventsDates();
        for (int i=0; i<calendarEventInsert.monthlyEventsDaysTextViews.get(selectedDate.getMonth()).size();i++) {
            TextView textView = new TextView(this);
            linearLayoutEventsDays.addView(textView,i);

                textView.setText(Integer.toString(calendarEventInsert.monthlyEventsDaysTextViews.get(selectedDate.getMonth()).get(i)));

                if(Integer.parseInt(String.valueOf(datee.getDayOfMonth()))>calendarEventInsert.monthlyEventsDaysTextViews.get(selectedDate.getMonth()).get(i)){
                    textView.setTextColor(Color.DKGRAY);
                } else {
                    textView.setTextColor(Color.BLUE);
                }

        }
    }

    /**
     * if the selected month has any day that has event in it so show the clear all button which remove all the events in all days in a selected month
     * if the selected day of the month has any events so show Remove all button which remove all events in a selected day
     * if the selected day of the month has any events so show Edit button which let the user mark any event
     * if there are no events in a selected day so change Remove All button and Edit visibility yo InVisible
     * if the selected month has no day that has event in it so change visibility for Clear all button, Remove All button and Edit button to Invisible
     */
    private void updateButtonsVisibility() {
        if(calendarEventInsert.monthlyEventsDaysTextViews.get(selectedMonth)!=null) {
            findViewById(R.id.clearAll).setVisibility(View.VISIBLE);
            daysTextView.setVisibility(View.VISIBLE);
            if (calendarEventInsert.eventsOnDate.get(selectedDate) != null) {
                findViewById(R.id.selectEvents).setVisibility(View.VISIBLE);
                findViewById(R.id.RemoveAllEvents).setVisibility(View.VISIBLE);
                eventsTextView.setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.selectEvents).setVisibility(View.INVISIBLE);
                findViewById(R.id.RemoveAllEvents).setVisibility(View.INVISIBLE);
                eventsTextView.setVisibility(View.INVISIBLE);

            }
        }

        else{
            findViewById(R.id.clearAll).setVisibility(View.INVISIBLE);
            findViewById(R.id.selectEvents).setVisibility(View.INVISIBLE);
            findViewById(R.id.RemoveAllEvents).setVisibility(View.INVISIBLE);
            daysTextView.setVisibility(View.INVISIBLE);
            eventsTextView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * select the two textViews in each row in the table that has the dates that has events
     * remove the numbers from the selected textViews
     */
    private void emptyEventsDates() {
        linearLayoutEventsDays.removeAllViews();
    }

    /**
     * select the textView in each row in the table that has events
     * remove the text from the selected textViews
     * select the checkBox in each row in the table that has events
     * change the selected checkBoxes Check-states to false
     * change the selected checkBoxes visibility to Invisible
     */
    private void emptyEvents() {
        /*for (int i = 0; i <linearLayout.getChildCount() ; i++) {
            TableRow row = (TableRow) linearLayout.getChildAt(i);
            TextView textView = (TextView) row.getChildAt(1);
            CheckBox checkBox =(CheckBox) row.getChildAt(0);
            checkBox.setChecked(false);
            checkBox.setVisibility(View.INVISIBLE);
            textView.setText("");
        }

         */
        linearLayout2.removeAllViews();
    }


    /**
     * select Button function
     * if there are no events to select so make toast says that no events to select
     * if there are events so if its the first time the user click the button so change each check box in each row visibility to Visible
     * increase the selectClick by one which will
     * change the select Button color to grey mean that the user has already clicked once'
     * if the user clicked the button for the second time so each check box in each row visibility change to Invisible
     * selectClick will decrease by one returning to the default state of the variable
     * then change the color og the select Button to the original color means that the button is not clicked yet
     * @param view Select Button
     */
    public void selectEvents(View view) {
        if(calendarEventInsert.eventsOnDate.get(selectedDate).size()==0){
            Toast.makeText(this,"No events to edit",Toast.LENGTH_LONG).show();
        } else {
            if (selectClick == 0) {
                for (int i = 0; i < linearLayout2.getChildCount(); i++) {
                    TableRow row = (TableRow) linearLayout2.getChildAt(i);
                    CheckBox cb = (CheckBox) row.getChildAt(0);

                    cb.setVisibility(View.VISIBLE);
                }

                selectClick++;
                findViewById(R.id.selectEvents).setBackgroundColor(Color.DKGRAY);
                if(thereCheckBoxChecked)findViewById(R.id.removeEvents).setVisibility(View.VISIBLE);


            } else if (selectClick == 1) {
                for (int i = 0; i < linearLayout2.getChildCount(); i++) {
                    TableRow row = (TableRow) linearLayout2.getChildAt(i);
                    CheckBox cb = (CheckBox) row.getChildAt(0);

                    cb.setVisibility(View.INVISIBLE);
                }
                selectClick--;
                findViewById(R.id.selectEvents).setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.naivy, null)); //without theme
                findViewById(R.id.removeEvents).setVisibility(View.INVISIBLE);

            }
        }
    }

    /**
     * RemoveAll button function
     * if the selected day has events so remove all the events by removing the arrayList in the hash table eventsOnDate with key the selected date
     * update the text in the textViews by calling showEvents()
     * remove the number of the selected day by removing it from the arrayList in the hash table monthlyEventsDaysTextViews with key the selected month
     * empty the table that has dates which has events by calling emptyEventsDates()
     * update the textview text in the table that has dates which has events by calling updateEventsDaysColor()
     * change checkboxes in the table that has events visibility to Invisible with Remove all button and trash button and select button  also with changing select button color to the original color
     *@param v Remove all button
     */
    public void removeAllEvents(View v) {
        if(calendarEventInsert.eventsOnDate.get(selectedDate)!=null) {
            calendarEventInsert.eventsOnDate.remove(selectedDate);
        }
        showEvents();
        int day=  calendarEventInsert.monthlyEventsDaysTextViews.get(selectedDate.getMonth()).indexOf(selectedDate.getDayOfMonth());
        calendarEventInsert.monthlyEventsDaysTextViews.get(selectedDate.getMonth()).remove(day);


        emptyEventsDates();
        updateEventsDaysColor();
        /*for (int i = 0; i < linearLayout.getChildCount(); i++) {
            TableRow row = (TableRow) linearLayout.getChildAt(i);
            CheckBox cb = (CheckBox) row.getChildAt(0);
            cb.setVisibility(View.INVISIBLE);
        }

         */
        emptyEvents();
        findViewById(R.id.selectEvents).setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.naivy, null)); //without theme
        findViewById(R.id.removeEvents).setVisibility(View.INVISIBLE);
        findViewById(R.id.RemoveAllEvents).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectEvents).setVisibility(View.INVISIBLE);


        uploadAppData();

    }

    /**
     * trash button function
     * count how many check boxes are checked with help of numChecked variable
     * if there is only one event in the selected day or all checked boxes are checked so remove all the events for the selected day by calling removeAllEvents(vn)
     * but if the selected day is the only day in the month that has events so remove all the events by calling clearAllEvents(vn)
     * if not all the events from the textViews from each row in the table that has events are checked and they are more than one so remove the events only that has checked checked boxes
     * and remove the events from the array list from the hash table eventsOnDate with key selectedDate
     * update the textViews in the table that has events by calling showEvents()
     * call the select button function to turn of the button
     * @param view Trash button
     */
    public void removeEvents(View view) {
       int numChecked =0;
        for (int i = 0; i < linearLayout2.getChildCount(); i++) {
            TableRow r = (TableRow) linearLayout2.getChildAt(i);
            CheckBox c = (CheckBox) r.getChildAt(0);
            TextView t = (TextView) r.getChildAt(1);
            if (c.isChecked() && t.getText()!="") numChecked++;
        }
        if (((calendarEventInsert.eventsOnDate.get(selectedDate).size()==1)&&(numChecked!=0)) || numChecked ==calendarEventInsert.eventsOnDate.get(selectedDate).size()) {
            View vn = null;
            if (calendarEventInsert.monthlyEventsDaysTextViews.get(selectedMonth).size()==1
            ) {
                clearAllEvents(vn);
            } else {
                removeAllEvents(vn);
            }
        } else {
            for (int i = 0; i < linearLayout2.getChildCount(); i++) {
                TableRow r = (TableRow) linearLayout2.getChildAt(i);
                CheckBox c = (CheckBox) r.getChildAt(0);
                TextView t = (TextView) r.getChildAt(1);
                if (c.isChecked() && t.getText()!="") {
                    int ii=i;
                    calendarEventInsert.eventsOnDate.get(selectedDate).remove(ii);
                    linearLayout2.removeViewAt(i);
                }
            }
            showEvents();
            View vr = null;
            selectEvents(vr);
        }
        updateButtonsVisibility();
        uploadAppData();


    }

    /**
     * clear all button function
     * if the array list from the hashtable monthlyEventsDaysTextViews with key selected month is not null
     * remove all array lists that has events for each date that has events in it from its hash Table eventsOnDate
     * empty the textViews text from the table that has events by calling emptyEvents()
     * empty the textViews text from the table that has  dates  by calling emptyEventsDates()
     * change select events button color to the original color
     * change Remove all, clear all, select events and trash button visibility to Invisible
     * @param view clear all events button
     */
    public void clearAllEvents(View view) {
        if (calendarEventInsert.monthlyEventsDaysTextViews.get(selectedMonth)!=null) {
            for (int dayInt : calendarEventInsert.monthlyEventsDaysTextViews.get(selectedMonth)
            ) {
                LocalDate d = LocalDate.of(selectedDate.getYear(), selectedMonth, dayInt);

                if (calendarEventInsert.eventsOnDate.get(d) != null) {
                    calendarEventInsert.eventsOnDate.remove(d);
                }
            }
            calendarEventInsert.monthlyEventsDaysTextViews.remove(selectedMonth);
        }
        emptyEventsDates();
        emptyEvents();
        findViewById(R.id.selectEvents).setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.naivy, null)); //without theme

        findViewById(R.id.removeEvents).setVisibility(View.INVISIBLE);
        findViewById(R.id.clearAll).setVisibility(View.INVISIBLE);
        findViewById(R.id.RemoveAllEvents).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectEvents).setVisibility(View.INVISIBLE);


        uploadAppData();
    }

    /**
     * Add event function
     * create and start intent which goes to the calendarEventsInsert Activity with extra data which is selectedDate as a string
     * @param view add event button
     */
    public void addEventButton(View view) {
        Intent cE = new Intent(this,CalendarEventInsert.class);
        cE.putExtra("date",selectedDate.toString());
        startActivity(cE);
    }

    /**
     * load monthly Events Days TextViews data function
     * will download the data arrayList that has numbers refers to days that has events from the given Map which comes from the shared preferences
     * set the data in the monthlyEventsDaysTextViews HashMap<Month, ArrayList<Int>> that has the ArrayList (which has the numbers of the days that contains events) for each month
     * @param a: Map<*, *> map comes from the application shared preferences
     */
    private void downloadMonthlyEventsDaysTextViews(Map a) {
        for (Object m : a.keySet()){
            Month month = Month.valueOf(String.valueOf(m));
            ArrayList<Integer> arrayList = (ArrayList<Integer>) a.get(m);
            ArrayList<Integer> arrayList1 = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                Object o = arrayList.get(i);
                int s = ((Number) o).intValue();

                arrayList1.add(s);
            }
            if (calendarEventInsert.monthlyEventsDaysTextViews.containsKey(month)) {
                Collections.copy(calendarEventInsert.monthlyEventsDaysTextViews.get(month), arrayList1);
            } else {
                calendarEventInsert.monthlyEventsDaysTextViews.put(month, arrayList1);
            }
        }
    }

    /**
     * downloadEventsOnDates which download each event which will be the parameter as Map with keys (Description,time,date)
     * set the event in the right date if there is already list in the specific date
     * otherwise create an ArrayList<Event> and add the event and then add the new events List into the eventsOnDate <LocalDate, ArrayList<Event>>
     * @param ll: Map<*, *> the Map which is the event with keys (Description,time,date)
     */
    private void loadEventsOnDates(Map ll){
        Event e = new Event();
        e.Desccription = String.valueOf(ll.get("Desccription"));
        String ds = String.valueOf(ll.get("date"));
        e.date = LocalDate.parse(ds);
        String ts = String.valueOf(ll.get("time"));
        e.time = LocalTime.parse(ts);
        if (calendarEventInsert.eventsOnDate.containsKey(e.date)) {
            calendarEventInsert.eventsOnDate.get(e.date).add(e);
        } else {
            ArrayList<Event> ev = new ArrayList<>();
            ev.add(e);
            calendarEventInsert.eventsOnDate.put(e.date, ev);
        }
    }



    /**
     * downloadAllData function which will download all the data from the main HashMap appData and set data into the application variables
     * for every event in the downloaded list with key "eventsOnDateEvents", the event will be set in the right place in the eventsOnDate hashmap by calling downloadEventsOnDates(event) for every event
     * download the map ["monthlyEventsDaysTextViews"][0] which contains the arraylist that has number to dates that has events in it
     * set the map in monthlyEventsDaysTextViews hashmap using downloadMonthlyEventsDaysTextViews(map)
     */
    private void downloadAllData() {
        if (MainActivity.appData.containsKey("eventsOnDateEvents") && calendarEventInsert.eventsOnDate.isEmpty()&&MainActivity.appData.get("eventsOnDateEvents") != null) {

            for (int i = 0; i < MainActivity.appData.get("eventsOnDateEvents").size(); i++) {
                try {
                    LinkedTreeMap ll = (LinkedTreeMap) MainActivity.appData.get("eventsOnDateEvents").get(i);
                    loadEventsOnDates(ll);

                }catch (Exception e){
                    HashMap ll = (HashMap) MainActivity.appData.get("eventsOnDateEvents").get(i);
                    loadEventsOnDates(ll);
                }
            }

        }
        if (MainActivity.appData.containsKey("monthlyEventsDaysTextViews") && calendarEventInsert.monthlyEventsDaysTextViews.isEmpty()&&MainActivity.appData.get("monthlyEventsDaysTextViews") != null) {

            try {
                LinkedTreeMap a = (LinkedTreeMap) MainActivity.appData.get("monthlyEventsDaysTextViews").get(0);
                downloadMonthlyEventsDaysTextViews(a);

            } catch (Exception e) {
                HashMap a = (HashMap) MainActivity.appData.get("monthlyEventsDaysTextViews").get(0);
                downloadMonthlyEventsDaysTextViews(a);
            }
        }
    }
    /**
     * create an arraylist<Event> and add all events for each day
     * put the arrayList in the main appdata Hashmap <String, ArrayList>
     * create an ArrayList<HashMap<Month, ArrayList<Int>>> to add monthlyEventsDaysTextViews HashMap
     * put the second ArrayList in the main appdata Hashmap <String, ArrayList>
     * upload the data into the shared preferences using saveAppData()
     */
    private void uploadAppData() {
        ArrayList<Event> events =new ArrayList<>();
        for (LocalDate d :calendarEventInsert.eventsOnDate.keySet()) {///////////////////////////**************************
            for (int i = 0; i <calendarEventInsert.eventsOnDate.get(d).size() ; i++) {
                events.add(calendarEventInsert.eventsOnDate.get(d).get(i));
            }
        }
        if(!MainActivity.appData.containsKey("eventsOnDateEvents"))
            MainActivity.appData.put("eventsOnDateEvents",events);
        else MainActivity.appData.replace("eventsOnDateEvents",events);
        ArrayList<HashMap<Month,ArrayList<Integer>>> a = new ArrayList<>();
        a.add(calendarEventInsert.monthlyEventsDaysTextViews);
        if(!MainActivity.appData.containsKey("monthlyEventsDaysTextViews"))MainActivity.appData.put("monthlyEventsDaysTextViews",a) ;
        else MainActivity.appData.replace("monthlyEventsDaysTextViews",a);

        MainActivity m = new MainActivity();
        m.saveAppData();
    }

    /**
     * OnResume function
     * upload App Data for this important date which are the two hashMaps monthlyEventsDaysTextViews,eventsOnDate using uploadAppData()
     * update Buttons Visibility based on the current data in the activity using updateButtonsVisibility()
     * Show the events on the screen using showEvents()
     * if there any day that has events in it so update the color of the days based on if the date is passed or in the future
     * otherwise empty the numbers
     * if the restored is true so show the checkBoxes for the events and restore the check mark
     */
    @Override
    protected void onResume() {
        super.onResume();
        uploadAppData();
        updateButtonsVisibility();
        showEvents();
        if (calendarEventInsert.monthlyEventsDaysTextViews.containsKey(selectedDate.getMonth())) {
            updateEventsDaysColor();
        } else {
            if (calendarEventInsert.monthlyEventsDaysTextViews.get(selectedDate.getMonth())!= null) {
                emptyEventsDates();
            }
        }
        if (restored) {
            View v = null;
            selectEvents(v);
            checkVariables.clear();
            checkVariables.addAll(helpCheckVariables);
            for (int i=0; i<linearLayout2.getChildCount();i++) {
                TableRow r = (TableRow)linearLayout2.getChildAt(i) ;
                CheckBox c =(CheckBox) r.getChildAt(0);
                if (checkVariables.get(i) == 1) c.setChecked (true);
                else if (checkVariables.get(i)== 0) c.setChecked (false);

            }
            restored=false;
        }
    }
    class EventCheck implements CompoundButton.OnCheckedChangeListener {
        /**
         * eventCheck function used in OnCheckListener for check boxes in events layout changing the visibility of the Remove button
         * for loop check every checkbox in the events table and if its checked so the Remove Button will change to visible
         * if never founded a checked check box so the Remove button change to invisible
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            thereCheckBoxChecked=false;
            for (int i = 0; i < linearLayout2.getChildCount(); i++) {
                TableRow r = (TableRow) linearLayout2.getChildAt(i);
                CheckBox c = (CheckBox) r.getChildAt(0);
                if (c.isChecked()) {
                    findViewById(R.id.removeEvents).setVisibility(View.VISIBLE);
                    checkVariables.set(i,1);
                    thereCheckBoxChecked=true;
                } else if (!c.isChecked()) {
                    checkVariables.set(i,0);
                }

            }
            if (!thereCheckBoxChecked) {
                findViewById(R.id.removeEvents).setVisibility(View.INVISIBLE);
            }

        }
    }
}
