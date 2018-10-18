package com.nepal.adversify.viewmodel;

import android.app.Application;

import com.generic.appbase.domain.dto.DetailMerchantInfo;
import com.generic.appbase.domain.dto.DiscountInfo;
import com.generic.appbase.domain.dto.OpeningInfo;
import com.generic.appbase.domain.dto.PayloadData;
import com.generic.appbase.domain.dto.PreviewMerchantInfo;
import com.generic.appbase.domain.dto.ReviewInfo;
import com.generic.appbase.domain.dto.SpecialOfferInfo;
import com.generic.appbase.ui.BaseViewModel;
import com.generic.appbase.utils.FileUtils;
import com.nepal.adversify.data.repository.MerchantRepository;
import com.nepal.adversify.domain.model.DiscountModel;
import com.nepal.adversify.domain.model.LocationModel;
import com.nepal.adversify.domain.model.MerchantModel;
import com.nepal.adversify.domain.model.OfferModel;
import com.nepal.adversify.domain.model.OpeningModel;
import com.nepal.adversify.domain.model.ReviewModel;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class MerchantViewModel extends BaseViewModel {

    final private MutableLiveData<Request> combinedMerchantRequest = new MutableLiveData<>();
    final private MutableLiveData<Request> merchantRequest = new MutableLiveData<>();
    final private MutableLiveData<Request> discountRequest = new MutableLiveData<>();
    final private MutableLiveData<Request> offerRequest = new MutableLiveData<>();
    final private MutableLiveData<Request> openingRequest = new MutableLiveData<>();
    final private MutableLiveData<Request> locationRequest = new MutableLiveData<>();
    final private MutableLiveData<Request> reviewRequest = new MutableLiveData<>();
    final private MutableLiveData<Request> averageRatingRequest = new MutableLiveData<>();
    private MerchantRepository mMerchantRepository;
    private final CompositeDisposable mDisposable;
    private final LiveData<MerchantModel> combinedMerchantLiveData = Transformations.switchMap(combinedMerchantRequest,
            input -> mMerchantRepository.loadCombinedMerchantInfo()
    );

    private final LiveData<MerchantModel> merchantLiveData = Transformations.switchMap(merchantRequest,
            input -> mMerchantRepository.loadCombinedMerchantInfo()
    );

    private final LiveData<Integer> averageRatingLiveData = Transformations.switchMap(averageRatingRequest,
            input -> mMerchantRepository.loadAverageRating()
    );

    private final LiveData<DiscountModel> discountLiveData = Transformations.switchMap(discountRequest,
            input -> mMerchantRepository.loadDiscountData()
    );

    private final LiveData<OfferModel> offerLiveData = Transformations.switchMap(offerRequest,
            input -> mMerchantRepository.loadOfferData()
    );

    private final LiveData<OpeningModel> openingLiveData = Transformations.switchMap(openingRequest,
            input -> mMerchantRepository.loadOpeningData()
    );

    private final LiveData<LocationModel> locationLiveData = Transformations.switchMap(locationRequest,
            input -> mMerchantRepository.loadLocationData()
    );

    private final LiveData<List<ReviewModel>> reviewsLiveData = Transformations.switchMap(reviewRequest,
            input -> mMerchantRepository.loadReviewData()
    );

    public MerchantViewModel(@NonNull Application application,
                             MerchantRepository mMerchantRepository,
                             CompositeDisposable mDisposable) {
        super(application);
        this.mMerchantRepository = mMerchantRepository;
        this.mDisposable = mDisposable;
    }

    public void loadMerchantData() {
        if (merchantLiveData.getValue() == null)
            merchantRequest.setValue(new Request());
    }

    public void loadCombinedMerchantData() {
        if (combinedMerchantLiveData.getValue() == null)
            combinedMerchantRequest.setValue(new Request());
    }

    public void loadDiscountData() {
        if (discountLiveData.getValue() == null)
            discountRequest.setValue(new Request());
    }

    public void loadOfferData() {
        if (offerLiveData.getValue() == null)
            offerRequest.setValue(new Request());
    }

    public void loadOpeningData() {
        if (openingLiveData.getValue() == null)
            openingRequest.setValue(new Request());
    }


    public void loadAverageRatingData() {
        if (averageRatingLiveData.getValue() == null)
            averageRatingRequest.setValue(new Request());
    }

    public void loadReviewData() {
        if (reviewsLiveData.getValue() == null)
            reviewRequest.setValue(new Request());
    }

    public void updateData(MerchantModel merchantModel, CompletableObserver observer) {
        mMerchantRepository.update(merchantModel)
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(e);
                    }
                });
    }

    public void updateLocation(double latitude, double longitude) {
        mMerchantRepository.updateLocation(latitude, longitude);
    }

    @Override
    protected void onCleared() {
        mDisposable.dispose();
        mMerchantRepository.onCleared();
        super.onCleared();
    }

    public PreviewMerchantInfo getPreviewMerchantData() {
        MerchantModel merchantModel = combinedMerchantLiveData.getValue();
        PreviewMerchantInfo merchantInfo = new PreviewMerchantInfo();
        merchantInfo.title = Objects.requireNonNull(merchantModel).title;
        merchantInfo.address = merchantModel.address;
        merchantInfo.contact = merchantModel.contact;
        merchantInfo.rating = averageRatingLiveData.getValue() == null ? 0 : averageRatingLiveData.getValue();
        if (merchantModel.hasDiscount) {
            Timber.d("has discount");
            merchantInfo.discount = merchantModel.discountModel.title;
        }
        if (merchantModel.hasOffer)
            merchantInfo.specialOffer = merchantModel.offerModel.title;
        if (merchantModel.image != null) {
            merchantInfo.fileName = FileUtils.getExtensionWithName(getApplication(), merchantModel.image);
            merchantInfo.hasFile = true;
        } else {
            merchantInfo.hasFile = false;
        }

        merchantInfo.dataType = PayloadData.MERCHANT_PREVIEW_INFO;

        return merchantInfo;
    }

    public DetailMerchantInfo getDetailMerchantInfo() {
        MerchantModel merchantModel = combinedMerchantLiveData.getValue();
        DetailMerchantInfo merchantInfo = new DetailMerchantInfo();
        merchantInfo.title = Objects.requireNonNull(merchantModel).title;
        merchantInfo.address = merchantModel.address;
        merchantInfo.contact = merchantModel.contact;
        merchantInfo.website = merchantModel.website;
        merchantInfo.description = merchantModel.description;
        merchantInfo.rating = averageRatingLiveData.getValue() == null ? 0 : averageRatingLiveData.getValue();
        if (merchantModel.hasDiscount) {
            merchantInfo.discountInfo = new DiscountInfo();
            merchantInfo.discountInfo.title = merchantModel.discountModel.title;
            merchantInfo.discountInfo.description = merchantModel.discountModel.description;
        }
        if (merchantModel.hasOffer) {
            merchantInfo.specialOfferInfo = new SpecialOfferInfo();
            merchantInfo.specialOfferInfo.title = merchantModel.offerModel.title;
            merchantInfo.specialOfferInfo.description = merchantModel.offerModel.description;
        }
        if (merchantModel.openingModel != null) {
            merchantInfo.openingInfo = new OpeningInfo();
            merchantInfo.openingInfo.sunday = merchantModel.openingModel.sunday;
            merchantInfo.openingInfo.monday = merchantModel.openingModel.monday;
            merchantInfo.openingInfo.tuesday = merchantModel.openingModel.tuesday;
            merchantInfo.openingInfo.wednesday = merchantModel.openingModel.wednesday;
            merchantInfo.openingInfo.thursday = merchantModel.openingModel.thursday;
            merchantInfo.openingInfo.friday = merchantModel.openingModel.friday;
            merchantInfo.openingInfo.saturday = merchantModel.openingModel.saturday;
        }
        if (merchantModel.location != null) {
            merchantInfo.location = merchantModel.location;
        }
        if (merchantModel.image != null) {
            merchantInfo.fileName = FileUtils.getExtensionWithName(getApplication(), merchantModel.image);
            merchantInfo.hasFile = true;
        } else {
            merchantInfo.hasFile = false;
        }

        List<ReviewModel> reviewModelList = reviewsLiveData.getValue();
        if (reviewModelList != null) {
            ReviewModel[] reviewModels = reviewModelList.toArray(new ReviewModel[0]);
            ReviewInfo[] reviewInfos = new ReviewInfo[reviewModels.length];
            for (int i = 0; i < reviewModels.length; i++) {
                ReviewModel reviewModel = reviewModels[i];
                ReviewInfo reviewInfo = new ReviewInfo();
                reviewInfo.clientId = reviewModel.clientId;
                reviewInfo.clientName = reviewModel.clientName;
                reviewInfo.star = reviewModel.star;
                reviewInfo.content = reviewModel.review;
                reviewInfos[i] = reviewInfo;
            }

            merchantInfo.reviewInfos = reviewInfos;
        }

        merchantInfo.dataType = PayloadData.MERCHANT_DETAIL_INFO;

        return merchantInfo;
    }

    public void updateAverageRating(Integer data) {
        MerchantModel merchantModel = combinedMerchantLiveData.getValue();
        if (merchantModel != null) {
            merchantModel.rating = data;
        }
    }

    public void addReviewData(ReviewModel reviewModel) {
        mMerchantRepository.addReviewData(reviewModel);
    }

    public LiveData<Integer> getAverageRatingLiveData() {
        return averageRatingLiveData;
    }

    public LiveData<DiscountModel> getDiscountLiveData() {
        return discountLiveData;
    }

    public LiveData<OfferModel> getOfferLiveData() {
        return offerLiveData;
    }

    public LiveData<OpeningModel> getOpeningLiveData() {
        return openingLiveData;
    }

    public LiveData<LocationModel> getLocationLiveData() {
        return locationLiveData;
    }

    public LiveData<List<ReviewModel>> getReviewsLiveData() {
        return reviewsLiveData;
    }

    public LiveData<MerchantModel> getCombinedMerchantLiveData() {
        return combinedMerchantLiveData;
    }

    public LiveData<MerchantModel> getMerchantLiveData() {
        return merchantLiveData;
    }


}
