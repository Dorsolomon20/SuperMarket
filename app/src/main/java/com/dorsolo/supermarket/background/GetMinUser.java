package com.dorsolo.supermarket.background;

import android.content.Context;

import com.dorsolo.supermarket.data.db.AppDatabase;
import com.dorsolo.supermarket.data.db.MinUser;

import java.util.concurrent.Callable;

/**
 *  Get the MinUser from the DB, the first and only record from the
 * 'Users' database 'MinUser' table. Callable<T> Runs on a worker thread but unlike Runnable it allows us to return a value
 */
public class GetMinUser implements Callable<MinUser> {

    private Context context;

    public GetMinUser(Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context is required for opening a connection with the SqLite database");
        this.context = context;
    }

    @Override
    public MinUser call() {
        return AppDatabase.getInstance(context).minUserDao().getUser();
    }
}
