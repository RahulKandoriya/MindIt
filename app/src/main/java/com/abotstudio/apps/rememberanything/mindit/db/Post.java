package com.abotstudio.apps.rememberanything.mindit.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import androidx.work.PeriodicWorkRequest;

@Entity
public class Post {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private int timeCount;
    private boolean notification_check;

    @TypeConverters({PeriodicWorkRequestConverter.class})
    private PeriodicWorkRequest periodicWorkRequest;

    public Post() {
    }


    @Ignore
    public Post(String title, String content, int timeCount, boolean notification_check, PeriodicWorkRequest periodicWorkRequest) {
        this.title = title;
        this.content = content;
        this.timeCount = timeCount;
        this.notification_check = notification_check;
        this.periodicWorkRequest = periodicWorkRequest;
    }

    public PeriodicWorkRequest getPeriodicWorkRequest() {
        return periodicWorkRequest;
    }

    public void setPeriodicWorkRequest(PeriodicWorkRequest periodicWorkRequest) {
        this.periodicWorkRequest = periodicWorkRequest;
    }

    public boolean isNotification_check() {


            return notification_check;


    }

    public void setNotification_check(boolean notification_check) {
        this.notification_check = notification_check;
    }

    public int getTimeCount() {

        return timeCount;

    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}