package com.sss.wearable.Classes;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Interest.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static Database instance = null;

    public static Database getMainInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context, Database.class, "mealsDatabase")
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    public static Database getInstance() {
        return instance;
    }

    public abstract InterestDao interestDao();
}
