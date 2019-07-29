package com.abotstudio.apps.rememberanything.mindit.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.abotstudio.apps.rememberanything.mindit.PeriodicTask;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;

@Database(entities = {Post.class}, version = 1)
public abstract class PostsDatabase extends RoomDatabase {

    private static PostsDatabase INSTANCE;

    private final static List<Post> POSTS = Arrays.asList(
            new Post("Revise anything", "Add the things you want to revise by setting notification hourly. See the demo by clicking switch", 1, false, new PeriodicWorkRequest.Builder(PeriodicTask.class, 1, TimeUnit.MINUTES).build()));


    public abstract PostDao postDao();

    private static final Object sLock = new Object();

    public static PostsDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        PostsDatabase.class, "Posts.db")
                        .allowMainThreadQueries()
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                Executors.newSingleThreadExecutor().execute(
                                        () -> getInstance(context).postDao().saveAll(POSTS));
                            }
                        })
                        .build();
            }
            return INSTANCE;
        }
    }
}