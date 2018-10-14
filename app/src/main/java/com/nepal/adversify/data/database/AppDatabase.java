package com.nepal.adversify.data.database;

import com.nepal.adversify.BuildConfig;
import com.nepal.adversify.data.dao.DiscountDAO;
import com.nepal.adversify.data.dao.LocationDAO;
import com.nepal.adversify.data.dao.MerchantDAO;
import com.nepal.adversify.data.dao.OfferDAO;
import com.nepal.adversify.data.dao.OpeningDAO;
import com.nepal.adversify.data.entity.DiscountEntity;
import com.nepal.adversify.data.entity.LocationEntity;
import com.nepal.adversify.data.entity.MerchantEntity;
import com.nepal.adversify.data.entity.OpeningEntity;
import com.nepal.adversify.data.entity.SpecialOfferEntity;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MerchantEntity.class, DiscountEntity.class, OpeningEntity.class, SpecialOfferEntity.class,
        LocationEntity.class},
        version = BuildConfig.VERSION_CODE)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MerchantDAO merchantDAO();

    public abstract DiscountDAO discountDAO();

    public abstract OfferDAO offerDAO();

    public abstract OpeningDAO openingDAO();

    public abstract LocationDAO locationDAO();

}
