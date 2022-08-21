package com.ifu.iforyou;

public class Timetable {
    String date, startTime, endTime, lecturerId, type, location, id;
    public Timetable(String date, String startTime, String endTime, String lecturerId,
                     String type, String location, String id) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lecturerId = lecturerId;
        this.type = type;
        this.location = location;
        this.id = id;

    }

    public String getDate() {
        return date;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getLecturerId() {
        return lecturerId;
    }
    public String getType() {
        return type;
    }
    public String getLocation() {
        return location;
    }
    public String getId() {
        return id;
    }
}
