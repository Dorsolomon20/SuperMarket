package com.dorsolo.supermarket.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.dorsolo.supermarket.base.BaseDao;

/**
 * Dao (Data access object) provides preDefined actions available to perform on the database, without exposing
 * the way they are performed.
 */
@Dao
public interface MinUserDao extends BaseDao<MinUser> {

    @Query("SELECT * FROM MinUser")
    MinUser getUser();

    @Query("DELETE FROM MinUser")
    void deleteAll();

}
