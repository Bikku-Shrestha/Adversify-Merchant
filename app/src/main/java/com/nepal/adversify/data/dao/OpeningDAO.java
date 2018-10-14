package com.nepal.adversify.data.dao;

import com.nepal.adversify.data.entity.OpeningEntity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface OpeningDAO {

    @Query("SELECT * FROM opening_info LIMIT 1")
    OpeningEntity get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OpeningEntity openingEntity);

    @Update
    void update(OpeningEntity openingEntity);

    @Delete
    void delete(OpeningEntity openingEntity);

}
