package com.nepal.adversify.data.dao;

import com.nepal.adversify.data.entity.CompositeMerchantEntity;
import com.nepal.adversify.data.entity.MerchantEntity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MerchantDAO {

    @Query("SELECT m.id,m.title,m.contact,m.website,m.address,m.description,m.category, m.image, "
            + "oi.id as opening_id, oi.sunday,oi.monday,oi.tuesday,oi.wednesday, oi.thursday, oi.friday, oi.saturday, "
            + "d.id as discount_id, d.title as discount_title, d.description as discount_description, "
            + "o.id as offer_id, o.title as offer_title, o.description as offer_description, "
            + "l.id as location_id, l.lat, l.lon "
            + "FROM merchant m "
            + "LEFT JOIN opening_info oi ON oi.id = m.opening_hour "
            + "LEFT JOIN location l ON l.id = m.location "
            + "LEFT JOIN discount d ON d.id = m.discount "
            + "LEFT JOIN offer o ON o.id = m.offer "
            + "WHERE m.id = 1")
    LiveData<CompositeMerchantEntity> getCombined();

    @Query("SELECT * FROM merchant limit 1")
    LiveData<MerchantEntity> get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MerchantEntity merchantEntity);

    @Update
    void update(MerchantEntity merchantEntity);

    @Delete
    void delete(MerchantEntity merchantEntity);

}
