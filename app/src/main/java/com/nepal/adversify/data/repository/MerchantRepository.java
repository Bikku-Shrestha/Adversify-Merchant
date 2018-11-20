package com.nepal.adversify.data.repository;


import android.net.Uri;
import android.text.TextUtils;

import com.generic.appbase.domain.dto.Category;
import com.generic.appbase.domain.dto.Location;
import com.generic.appbase.utils.rx.SchedulerProvider;
import com.nepal.adversify.data.dao.DiscountDAO;
import com.nepal.adversify.data.dao.LocationDAO;
import com.nepal.adversify.data.dao.MerchantDAO;
import com.nepal.adversify.data.dao.OfferDAO;
import com.nepal.adversify.data.dao.OpeningDAO;
import com.nepal.adversify.data.dao.RateDAO;
import com.nepal.adversify.data.dao.ReviewDAO;
import com.nepal.adversify.data.entity.DiscountEntity;
import com.nepal.adversify.data.entity.LocationEntity;
import com.nepal.adversify.data.entity.MerchantEntity;
import com.nepal.adversify.data.entity.OpeningEntity;
import com.nepal.adversify.data.entity.ReviewEntity;
import com.nepal.adversify.data.entity.SpecialOfferEntity;
import com.nepal.adversify.domain.model.DiscountModel;
import com.nepal.adversify.domain.model.LocationModel;
import com.nepal.adversify.domain.model.MerchantModel;
import com.nepal.adversify.domain.model.OfferModel;
import com.nepal.adversify.domain.model.OpeningModel;
import com.nepal.adversify.domain.model.ReviewModel;
import com.nepal.adversify.mapper.DiscountEntityToDiscountModelMapper;
import com.nepal.adversify.mapper.LocationEntityToLocationModelMapper;
import com.nepal.adversify.mapper.MerchantEntityToMerchantModelMapper;
import com.nepal.adversify.mapper.OfferEntityToOfferModelMapper;
import com.nepal.adversify.mapper.OpeningEntityToOpeningModelMapper;
import com.nepal.adversify.mapper.ReviewEntityToReviewModelMapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class MerchantRepository {

    private final MerchantDAO mMerchantDAO;
    private final OpeningDAO mOpeningDAO;
    private final DiscountDAO mDiscountDAO;
    private final OfferDAO mOfferDAO;
    private final LocationDAO mLocationDAO;
    private final ReviewDAO mReviewDAO;
    private final RateDAO mRateDAO;
    private final SchedulerProvider mScheduler;
    private CompositeDisposable mDisposable;
    private MerchantEntityToMerchantModelMapper fromMerchantEntityToMerchantModelMapper;
    private DiscountEntityToDiscountModelMapper fromDiscountEntityToDiscountModelMapper;
    private OfferEntityToOfferModelMapper fromOfferEntityToOfferModelMapper;
    private LocationEntityToLocationModelMapper fromLocationEntityToLocationModelMapper;
    private OpeningEntityToOpeningModelMapper fromOpeningEntityToOpeningModelMapper;
    private ReviewEntityToReviewModelMapper fromReviewEntityToReviewModelMapper;

    @Inject
    public MerchantRepository(MerchantDAO merchantDAO, OpeningDAO mOpeningDAO,
                              DiscountDAO mDiscountDAO, OfferDAO mOfferDAO,
                              LocationDAO mLocationDAO, ReviewDAO mReviewDAO,
                              RateDAO mRateDAO, SchedulerProvider mScheduler,
                              CompositeDisposable mDisposable,
                              MerchantEntityToMerchantModelMapper fromMerchantEntityToMerchantModelMapper,
                              DiscountEntityToDiscountModelMapper fromDiscountEntityToDiscountModelMapper,
                              OfferEntityToOfferModelMapper fromOfferEntityToOfferModelMapper,
                              LocationEntityToLocationModelMapper fromLocationEntityToLocationModelMapper,
                              OpeningEntityToOpeningModelMapper fromOpeningEntityToOpeningModelMapper,
                              ReviewEntityToReviewModelMapper fromReviewEntityToReviewModelMapper
    ) {
        this.mMerchantDAO = merchantDAO;
        this.mOpeningDAO = mOpeningDAO;
        this.mDiscountDAO = mDiscountDAO;
        this.mOfferDAO = mOfferDAO;
        this.mLocationDAO = mLocationDAO;
        this.mReviewDAO = mReviewDAO;
        this.mRateDAO = mRateDAO;
        this.mScheduler = mScheduler;
        this.mDisposable = mDisposable;
        this.fromMerchantEntityToMerchantModelMapper = fromMerchantEntityToMerchantModelMapper;
        this.fromDiscountEntityToDiscountModelMapper = fromDiscountEntityToDiscountModelMapper;
        this.fromOfferEntityToOfferModelMapper = fromOfferEntityToOfferModelMapper;
        this.fromLocationEntityToLocationModelMapper = fromLocationEntityToLocationModelMapper;
        this.fromOpeningEntityToOpeningModelMapper = fromOpeningEntityToOpeningModelMapper;
        this.fromReviewEntityToReviewModelMapper = fromReviewEntityToReviewModelMapper;
        Timber.d("Initialised!");
    }

    public LiveData<MerchantModel> loadMerchantData() {
        Timber.d("loadMerchantData");
        return Transformations.map(mMerchantDAO.get(), input -> fromMerchantEntityToMerchantModelMapper.from(input));
    }

    public LiveData<DiscountModel> loadDiscountData() {
        Timber.d("loadDiscountData");
        return Transformations.map(mDiscountDAO.get(), input -> fromDiscountEntityToDiscountModelMapper.from(input));
    }

    public LiveData<OfferModel> loadOfferData() {
        Timber.d("loadOfferData");
        return Transformations.map(mOfferDAO.get(), input -> fromOfferEntityToOfferModelMapper.from(input));
    }

    public LiveData<OpeningModel> loadOpeningData() {
        Timber.d("loadOpeningData");
        return Transformations.map(mOpeningDAO.get(), input -> fromOpeningEntityToOpeningModelMapper.from(input));
    }

    public LiveData<LocationModel> loadLocationData() {
        Timber.d("loadLocationData");
        return Transformations.map(mLocationDAO.get(), input -> fromLocationEntityToLocationModelMapper.from(input));
    }

    public LiveData<Integer> loadAverageRating() {
        Timber.d("loadAverageRating");
        return mReviewDAO.getAverageRating();
    }

    public LiveData<List<ReviewModel>> loadReviewData() {
        Timber.d("loadReviewData");
        return Transformations.map(mReviewDAO.get(), input -> {
            List<ReviewModel> reviewModels = new ArrayList<>();
            for (ReviewEntity entity : input) {
                reviewModels.add(fromReviewEntityToReviewModelMapper.from(entity));
            }

            return reviewModels;
        });
    }

    public LiveData<MerchantModel> loadCombinedMerchantInfo() {
        return Transformations.map(mMerchantDAO.getCombined(), input -> {

            if (input == null) return null;

            MerchantModel model = new MerchantModel();

            model.id = input.id;
            model.title = input.title;
            model.address = input.address;
            model.website = input.website;
            model.contact = input.contact;
            model.description = input.description;
            model.category = Category.valueOf(input.category);
            model.image = TextUtils.isEmpty(input.image) ? null : Uri.parse(input.image);

            if (input.discountId != 1) {
                Timber.d("discount available in database with id:%d", input.discountId);
                model.discountModel = new DiscountModel();
                model.discountModel.id = input.discountId;
                model.discountModel.title = input.discountTitle;
                model.discountModel.description = input.discountDescription;
                model.hasDiscount = true;
            } else {
                model.hasDiscount = false;
            }

            if (input.offerId != 1) {
                model.offerModel = new OfferModel();
                model.offerModel.id = input.offerId;
                model.offerModel.title = input.offerTitle;
                model.offerModel.description = input.offerDescription;
                model.hasOffer = true;
            } else {
                model.hasOffer = false;
            }

            if (input.openingId != 1) {
                model.openingModel = new OpeningModel();
                model.openingModel.id = input.openingId;
                model.openingModel.sunday = input.sunday;
                model.openingModel.monday = input.monday;
                model.openingModel.tuesday = input.tuesday;
                model.openingModel.wednesday = input.wednesday;
                model.openingModel.thursday = input.thursday;
                model.openingModel.friday = input.friday;
                model.openingModel.saturday = input.saturday;
            }

            if (input.locationId != 1) {
                model.location = new Location();
                model.location.lat = input.lat;
                model.location.lon = input.lon;
            }

            return model;

        });
    }

    public Completable update(MerchantModel merchantModel) {

        return Completable.fromAction(() -> {

            MerchantEntity merchantEntity = new MerchantEntity();

            LocationEntity locationEntity = new LocationEntity();
            if (merchantModel.location != null) {
                locationEntity.id = 2;
                locationEntity.lat = merchantModel.location.lat;
                locationEntity.lon = merchantModel.location.lon;

                Timber.d("inserting location");
                mLocationDAO.insert(locationEntity);
                merchantEntity.location = locationEntity.id;
            } else {
                merchantEntity.location = 1;
            }

            DiscountEntity discountEntity = new DiscountEntity();
            if (merchantModel.discountModel != null) {
                discountEntity.id = 2;
                discountEntity.title = merchantModel.discountModel.title;
                discountEntity.description = merchantModel.discountModel.description;

                Timber.d("inserting discount");
                mDiscountDAO.insert(discountEntity);
                merchantEntity.discount = discountEntity.id;
            } else {
                merchantEntity.discount = 1;
            }

            SpecialOfferEntity offerEntity = new SpecialOfferEntity();
            if (merchantModel.offerModel != null) {
                offerEntity.id = 2;
                offerEntity.title = merchantModel.offerModel.title;
                offerEntity.description = merchantModel.offerModel.description;

                Timber.d("inserting offer");
                mOfferDAO.insert(offerEntity);
                merchantEntity.offer = offerEntity.id;
            } else {
                merchantEntity.offer = 1;
            }

            OpeningEntity openingEntity = new OpeningEntity();
            if (merchantModel.openingModel != null) {
                openingEntity.id = 2;
                openingEntity.sunday = merchantModel.openingModel.sunday;
                openingEntity.monday = merchantModel.openingModel.monday;
                openingEntity.tuesday = merchantModel.openingModel.tuesday;
                openingEntity.wednesday = merchantModel.openingModel.wednesday;
                openingEntity.thursday = merchantModel.openingModel.thursday;
                openingEntity.friday = merchantModel.openingModel.friday;
                openingEntity.saturday = merchantModel.openingModel.saturday;

                Timber.d("inserting opening");
                mOpeningDAO.insert(openingEntity);
                merchantEntity.opening = openingEntity.id;
            } else {
                merchantEntity.opening = 1;
            }

            merchantEntity.id = 1;
            merchantEntity.title = merchantModel.title;
            merchantEntity.address = merchantModel.address;
            merchantEntity.contact = merchantModel.contact;
            merchantEntity.website = merchantModel.website;
            merchantEntity.category = merchantModel.category.name();
            merchantEntity.description = merchantModel.description;
            merchantEntity.image = merchantModel.image == null ? "" : merchantModel.image.toString();

            mMerchantDAO.insert(merchantEntity);
        }).subscribeOn(mScheduler.io())
                .observeOn(mScheduler.ui());


    }

    public void updateLocation(double latitude, double longitude) {
        Completable.fromAction(() -> {
            LocationEntity locationEntity = new LocationEntity();
            locationEntity.id = 2;
            locationEntity.lat = latitude;
            locationEntity.lon = longitude;

            mLocationDAO.update(locationEntity);
        }).subscribeOn(mScheduler.io())
                .observeOn(mScheduler.ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("location updated on database");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error updating location");
                    }
                });
    }

    public void onCleared() {
        mDisposable.dispose();
    }

    public void addReviewData(ReviewModel reviewModel) {
        Completable.fromAction(() -> {
            mReviewDAO.insert(fromReviewEntityToReviewModelMapper.to(reviewModel));
        }).subscribeOn(mScheduler.io())
                .observeOn(mScheduler.ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("Review added to database");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error adding review");
                    }
                });
    }

    public void delete() {
        Completable.fromAction(() -> {
            mMerchantDAO.delete();
            mLocationDAO.delete();
            mDiscountDAO.delete();
            mOfferDAO.delete();
            mOpeningDAO.delete();
        }).subscribeOn(mScheduler.io())
                .observeOn(mScheduler.ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("Info deleted from database");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error deleting info");
                    }
                });
    }
}
