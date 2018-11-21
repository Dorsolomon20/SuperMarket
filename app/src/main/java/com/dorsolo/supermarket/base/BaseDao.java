package com.dorsolo.supermarket.base;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * BaseDao provides basic functionality to each Dao that extends it, it contains 3 repeated methods:
 * Insert, Delete, Update.
 *
 * @param <T> The value passed by the child Dao
 */
@Dao
public interface BaseDao<T> {

    /**
     * Insert the given obj to the table by creating a new record
     *
     * @param t Generic obj
     */
    @Insert(onConflict = REPLACE)
    void insertRecord(T t);

    /**
     * Delete a record based on the given obj
     *
     * @param t Generic obj
     */
    @Delete
    void deleteRecord(T t);

    /**
     * Update the record based on the given obj
     *
     * @param t Generic obj
     */
    @Update(onConflict = REPLACE)
    void updateRecord(T t);

}
