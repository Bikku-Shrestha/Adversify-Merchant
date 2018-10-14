/*
 * *
 *  * All Rights Reserved.
 *  * Created by Suzn on 2018.
 *  * suzanparajuli@gmail.com
 *
 */

package com.nepal.adversify.ui.manage;

import com.nepal.adversify.data.dao.DiscountDAO;
import com.nepal.adversify.data.dao.LocationDAO;
import com.nepal.adversify.data.dao.MerchantDAO;
import com.nepal.adversify.data.dao.OfferDAO;
import com.nepal.adversify.data.dao.OpeningDAO;
import com.nepal.adversify.data.database.AppDatabase;

import dagger.Module;
import dagger.Provides;

@Module
public class MerchantProviderModule {

    @Provides
    MerchantDAO providesMerchantDAO(AppDatabase database) {
        return database.merchantDAO();
    }

    @Provides
    OpeningDAO providesOpeningDAO(AppDatabase database) {
        return database.openingDAO();
    }

    @Provides
    DiscountDAO providesDiscountDAO(AppDatabase database) {
        return database.discountDAO();
    }

    @Provides
    OfferDAO providesDealsDAO(AppDatabase database) {
        return database.offerDAO();
    }

    @Provides
    LocationDAO providesLocationDAO(AppDatabase database) {
        return database.locationDAO();
    }

}
