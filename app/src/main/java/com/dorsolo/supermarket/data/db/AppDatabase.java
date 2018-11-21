package com.dorsolo.supermarket.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.dorsolo.supermarket.utilities.Constants.DatabaseConstants;

/**
 * AppDatabase is the entry point to the DB, Use the singleton pattern due to the fact that opening a
 * connection is an expensive action.
 */
@Database(entities = {MinUser.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase appDatabase;

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null)
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, DatabaseConstants.DATABASE_NAME).build();
        return appDatabase;
    }

    public abstract MinUserDao minUserDao();

    /**
     * Close the connection to the Database and set the AppDatabase instance to null
     */
    public void closeConnection() {
        close();
        appDatabase = null;
    }
}