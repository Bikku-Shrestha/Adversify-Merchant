package com.nepal.adversify.data.dao;

import com.nepal.adversify.data.entity.DiscountEntity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DiscountDAO {

    @Query("SELECT * FROM discount LIMIT 1")
    LiveData<DiscountEntity> get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiscountEntity discountEntity);

    @Update
    void update(DiscountEntity discountEntity);

    @Delete
    void delete(DiscountEntity discountEntity);

}
