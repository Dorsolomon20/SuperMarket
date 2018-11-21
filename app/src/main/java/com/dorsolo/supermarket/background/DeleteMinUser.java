package com.dorsolo.supermarket.background;

import android.content.Context;

import com.dorsolo.supermarket.data.db.AppDatabase;

/**
 * Delete the MinUser from the DB
 */
public class DeleteMinUser implements Runnable {

    private Context context;

    public DeleteMinUser(Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context is required for opening a connection with the SqLite database");
        this.context = context;
    }

    @Override
    public void run() {
        AppDatabase.getInstance(context).minUserDao().deleteAll();
    }
}
