package com.nepal.adversify.data.dao;

import com.nepal.adversify.data.entity.RateEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RateDAO {

    @Query("SELECT * FROM rate")
    LiveData<List<RateEntity>> get();

    @Query("SELECT AVG(star) FROM rate")
    LiveData<Integer> getAverageRating();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RateEntity rateEntity);

    @Update
    void update(RateEntity rateEntity);

    @Delete
    void delete(RateEntity rateEntity);

}
