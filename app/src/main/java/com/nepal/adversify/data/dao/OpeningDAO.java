package com.nepal.adversify.data.dao;

import com.nepal.adversify.data.entity.OpeningEntity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface OpeningDAO {

    @Query("SELECT * FROM opening_info LIMIT 1")
    LiveData<OpeningEntity> get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OpeningEntity openingEntity);

    @Update
    void update(OpeningEntity openingEntity);

    @Query("DELETE FROM opening_info")
    void delete();

}
