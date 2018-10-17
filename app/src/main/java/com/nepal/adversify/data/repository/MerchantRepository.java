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
import com.nepal.adversify.data.entity.DiscountEntity;
import com.nepal.adversify.data.entity.LocationEntity;
import com.nepal.adversify.data.entity.MerchantEntity;
import com.nepal.adversify.data.entity.OpeningEntity;
import com.nepal.adversify.data.entity.SpecialOfferEntity;
import com.nepal.adversify.domain.model.DiscountModel;
import com.nepal.adversify.domain.model.MerchantModel;
import com.nepal.adversify.domain.model.OfferModel;
import com.nepal.adversify.domain.model.OpeningModel;

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
    private final SchedulerProvider mScheduler;
    private CompositeDisposable mDisposable;

    @Inject
    public MerchantRepository(MerchantDAO merchantDAO, OpeningDAO mOpeningDAO,
                              DiscountDAO mDiscountDAO, OfferDAO mOfferDAO,
                              LocationDAO mLocationDAO, SchedulerProvider mScheduler,
                              CompositeDisposable mDisposable) {
        this.mMerchantDAO = merchantDAO;
        this.mOpeningDAO = mOpeningDAO;
        this.mDiscountDAO = mDiscountDAO;
        this.mOfferDAO = mOfferDAO;
        this.mLocationDAO = mLocationDAO;
        this.mScheduler = mScheduler;
        this.mDisposable = mDisposable;
        Timber.d("Initialised!");
    }

    public LiveData<MerchantModel> loadMerchantData() {
        Timber.d("Load mercahnt data");
        return Transformations.map(mMerchantDAO.get(), input -> {
            if (input == null) {
                Timber.d("empty database");
                return null;
            }
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
                model.discountModel = new DiscountModel();
                model.discountModel.id = input.discountId;
                model.discountModel.title = input.discountTitle;
                model.discountModel.description = input.discountDescription;
            }

            if (input.offerId != 1) {
                model.offerModel = new OfferModel();
                model.offerModel.id = input.offerId;
                model.offerModel.title = input.offerTitle;
                model.offerModel.description = input.offerDescription;
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
}
