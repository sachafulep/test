package com.sss.wearable.Classes;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface InterestDao {
    @Query("SELECT * FROM interests")
    List<Interest> getAll();

    @Insert
    void insert(Interest interest);

    @Query("SELECT count(*) FROM interests")
    int getCount();

    @Update()
    void updateInterest(List<Interest> interest);
}
