package com.tasksmanager.v2;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

public class CalendarEventInsert extends AppCompatActivity {
    private EditText  eventDescription;//the edit text that the user write the event description in it
    private TextView  eventDate; //the textView that shows the selected date
    private TextView  eventTime; //the textView that show the selected time
    private TimePicker  timePicker; // the time Picker that the user use to select time
    public static HashMap<LocalDate, ArrayList<Event>>  eventsOnDate=new HashMap<>(); //hash table that has arraylists which has event and  each arraylist has date as key
    public static HashMap<Month, ArrayList<Integer>> monthlyEventsDaysTextViews=new HashMap<>(); // hashtable that has array lists that has numbers which represents dates that has events and  every array list has Month as a key
    private LocalDate date; // selected date
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_event_insert);

        // initialize the component and get the date from the coming intent  and put it in the eventDate textView and set the timePicker
        intiWidget();
        Intent incomingIntent = getIntent();
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        eventDate.setText(incomingIntent.getStringExtra("date"));
        // set the date variable to the selected date and set onTimeChange Listener which will view the picked time on the timeTextView
        Calendar c = new Calendar();
        date= c.getSelectedDate();

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                eventTime.setText(String.valueOf(timePicker.getHour())+":"+String.valueOf(timePicker.getMinute()));
            }
        });
    }




    /**
     * onSaveInstanceState function which will save the current time to restore it then
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(eventTime.getText()!= "" ) {

            outState.putString("time", (String) eventTime.getText());
        }
    }

    /**
     * onRestoreInstanceState function which restores the time as string and set it in the eventTime textView
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if( savedInstanceState.get("time")!=null) {
            String s = (String) savedInstanceState.get("time");
            eventTime.setText(s);
        }
    }

    /**
     * initialize the components
     */
    private void intiWidget(){
        eventDate=findViewById(R.id.dateInCEI);
        eventDescription=findViewById(R.id.eventDes);
        eventTime=findViewById(R.id.timeInCEI);
    }




    /**
     * Save button function
     * if no time chose make toast with "enter a time"
     * if no description wrote make toast with "Enter an event description"
     * create an event with entered discretion and time
     * add the event to the hashtable eventsOnDate by calling addEvent(e)
     * add the selected day number to the hashtable monthlyEventsDaysTextViews by calling addDayNum()
     */
    public void saveEventdes(View view) {
        String eventDs = eventDescription.getText().toString();
        String h = null;
        String m =null;
        String s = String.valueOf(eventTime.getText());
        int sub= s.indexOf(":");
        if (!s.isEmpty()) {
            h = s.substring(0, sub);
            m = s.substring(sub + 1, s.length());
            if (eventDs.isEmpty()) {
                Toast.makeText(this, "Enter an event description", Toast.LENGTH_LONG).show();
            } else {
                LocalTime timeforEvent = LocalTime.of(Integer.parseInt(h), Integer.parseInt(m));
                Event e = new Event(date, eventDs, timeforEvent);


                addEvent(e);
                addDayNum();
                finish();
            }
        } else {
            Toast.makeText(this, "enter a time!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * if the hashtable eventsOnDate contain the key date which represent selected date
     * add the given event to the array list with key selected date "date"
     * sort the array list with key selected date "date" with a comparator that sort based on the time for each event in the arrayList
     * if if the hashtable eventsOnDate does not contain the key date which represent selected date
     * so create a new ArrayList and add the given event to it and then put it in the hash table eventsOnDate with the selected date "date" as a key
     * @param e the created event to be added
     */
    private void addEvent(Event e){
        if (eventsOnDate.containsKey(date)) {
            eventsOnDate.get(date).add(e);
            eventsOnDate.get(date).sort(new Comparator<Event>() {
                @Override
                public int compare(Event event1, Event event2) {
                    if(event1.time.isAfter(event2.time)) return 1;
                    else if(event1.time.isBefore(event2.time)) return -1;
                    else return 0;
                }
            });
        } else {
            ArrayList<Event> events = new ArrayList<>();
            events.add(e);
            eventsOnDate.put(date, events);
        }
    }

    /**
     * if the hashTable monthlyEventsDaysTextViews contain the key selected month so create a variable tempDay which represents the day from the selected date
     * if the array list that has key selected month which represents the days that has events does not already contain the  selected day  so add the day to it and sort the arrayList after
     * if the hashTable monthlyEventsDaysTextViews  does not contain the key selected month create an arrayList with type of Int and add the selected day to it and then put it in the hash table monthlyEventsDaysTextViews with the selected month as a key
     */
    private void addDayNum(){
        if (monthlyEventsDaysTextViews.containsKey(date.getMonth())) {
            int tempDay = date.getDayOfMonth();
            if (!monthlyEventsDaysTextViews.get(date.getMonth()).contains(tempDay)) {
                monthlyEventsDaysTextViews.get(date.getMonth()).add(date.getDayOfMonth());
                Collections.sort(monthlyEventsDaysTextViews.get(date.getMonth()));
            }


        } else {
            ArrayList<Integer> days = new ArrayList<>();
            days.add(date.getDayOfMonth());
            monthlyEventsDaysTextViews.put(date.getMonth(),days);
        }
    }


}