/*
 * *
 *  * All Rights Reserved.
 *  * Created by Suzn on 2018.
 *  * suzanparajuli@gmail.com
 *
 */

package com.nepal.adversify.ui.manage;

import com.generic.appbase.domain.dto.ReviewInfo;
import com.generic.appbase.mapper.Mapper;
import com.nepal.adversify.data.entity.DiscountEntity;
import com.nepal.adversify.data.entity.LocationEntity;
import com.nepal.adversify.data.entity.MerchantEntity;
import com.nepal.adversify.data.entity.OpeningEntity;
import com.nepal.adversify.data.entity.RateEntity;
import com.nepal.adversify.data.entity.ReviewEntity;
import com.nepal.adversify.data.entity.SpecialOfferEntity;
import com.nepal.adversify.domain.model.DiscountModel;
import com.nepal.adversify.domain.model.LocationModel;
import com.nepal.adversify.domain.model.MerchantModel;
import com.nepal.adversify.domain.model.OfferModel;
import com.nepal.adversify.domain.model.OpeningModel;
import com.nepal.adversify.domain.model.RateModel;
import com.nepal.adversify.domain.model.ReviewModel;
import com.nepal.adversify.mapper.DiscountEntityToDiscountModelMapper;
import com.nepal.adversify.mapper.LocationEntityToLocationModelMapper;
import com.nepal.adversify.mapper.MerchantEntityToMerchantModelMapper;
import com.nepal.adversify.mapper.OfferEntityToOfferModelMapper;
import com.nepal.adversify.mapper.OpeningEntityToOpeningModelMapper;
import com.nepal.adversify.mapper.RateEntityToRateModelMapper;
import com.nepal.adversify.mapper.ReviewEntityToReviewModelMapper;
import com.nepal.adversify.mapper.ReviewInfoToReviewModelMapper;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MerchantBinderModule {

    @Binds
    abstract Mapper<MerchantEntity, MerchantModel> bindsMerchantEntityToMerchantModel(MerchantEntityToMerchantModelMapper mapper);

    @Binds
    abstract Mapper<DiscountEntity, DiscountModel> bindsDiscountEntityToDiscountModel(DiscountEntityToDiscountModelMapper mapper);

    @Binds
    abstract Mapper<SpecialOfferEntity, OfferModel> bindsOfferEntityToOfferModel(OfferEntityToOfferModelMapper mapper);

    @Binds
    abstract Mapper<LocationEntity, LocationModel> bindsLocationEntityToLocationModel(LocationEntityToLocationModelMapper mapper);

    @Binds
    abstract Mapper<OpeningEntity, OpeningModel> bindsOpeningEntityToOpeningModel(OpeningEntityToOpeningModelMapper mapper);

    @Binds
    abstract Mapper<RateEntity, RateModel> bindsRateEntityToRateModel(RateEntityToRateModelMapper mapper);

    @Binds
    abstract Mapper<ReviewEntity, ReviewModel> bindsReviewEntityToReviewModel(ReviewEntityToReviewModelMapper mapper);

    @Binds
    abstract Mapper<ReviewInfo, ReviewModel> bindsReviewInfoToReviewModel(ReviewInfoToReviewModelMapper mapper);

}
