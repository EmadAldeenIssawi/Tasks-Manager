package com.tasksmanager.v2;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

public class Event {

    public Event() {

    }

    LocalDate date;
    String Desccription;
    LocalTime time;

    public Event(LocalDate date, String eventDesccription, LocalTime time) {
        this.date = date;
        this.Desccription = eventDesccription;
        this.time = time;
    }


}
