package com.abotstudio.apps.rememberanything.mindit.db;

import android.arch.persistence.room.TypeConverter;

import com.abotstudio.apps.rememberanything.mindit.PeriodicTask;

import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;


public class PeriodicWorkRequestConverter {


        @TypeConverter
        public PeriodicWorkRequest fromWorkRequest(String string) {

            return string == null ? null : new PeriodicWorkRequest.Builder(PeriodicTask.class, 1, TimeUnit.HOURS).build();

        }

        @TypeConverter
        public String toWorkRequest(PeriodicWorkRequest periodicWorkRequest) {

            return periodicWorkRequest == null ? null : periodicWorkRequest.toString();
        }

}
