package com.nepal.adversify.data.dao;

import com.nepal.adversify.data.entity.LocationEntity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface LocationDAO {

    @Query("SELECT * FROM location LIMIT 1")
    LocationEntity get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocationEntity locationEntity);

    @Update
    void update(LocationEntity locationEntity);

    @Delete
    void delete(LocationEntity locationEntity);

}
