package com.nepal.adversify.data.dao;

import com.nepal.adversify.data.entity.SpecialOfferEntity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface OfferDAO {

    @Query("SELECT * FROM offer LIMIT 1")
    LiveData<SpecialOfferEntity> get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SpecialOfferEntity offerEntity);

    @Update
    void update(SpecialOfferEntity offerEntity);

    @Query("DELETE FROM offer")
    void delete();

}
