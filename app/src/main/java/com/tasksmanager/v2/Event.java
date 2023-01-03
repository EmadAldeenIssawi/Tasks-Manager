package com.tasksmanager.v2;


import java.time.LocalDate;
import java.time.LocalTime;

public class Event {
    public Event() {}

    LocalDate date;
    String Description;
    LocalTime time;

    public Event(LocalDate date, String eventDescription, LocalTime time) {
        this.date = date;
        this.Description = eventDescription;
        this.time = time;
    }


}
