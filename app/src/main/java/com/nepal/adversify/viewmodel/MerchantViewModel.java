package com.nepal.adversify.viewmodel;

import android.app.Application;
import android.net.Uri;

import com.generic.appbase.domain.dto.DetailMerchantInfo;
import com.generic.appbase.domain.dto.DiscountInfo;
import com.generic.appbase.domain.dto.OpeningInfo;
import com.generic.appbase.domain.dto.PayloadData;
import com.generic.appbase.domain.dto.PreviewMerchantInfo;
import com.generic.appbase.domain.dto.SpecialOfferInfo;
import com.generic.appbase.ui.BaseViewModel;
import com.nepal.adversify.data.repository.MerchantRepository;
import com.nepal.adversify.domain.model.MerchantModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class MerchantViewModel extends BaseViewModel {

    final private MutableLiveData<Request> request = new MutableLiveData<>();
    private final MutableLiveData<Uri> selectedImage = new MutableLiveData<>();
    private MerchantRepository mMerchantRepository;
    private final LiveData<MerchantModel> merchantMutableLiveData = Transformations.switchMap(request,
            input -> mMerchantRepository.loadMerchantData()
    );

    public MerchantViewModel(@NonNull Application application,
                             MerchantRepository mMerchantRepository) {
        super(application);
        this.mMerchantRepository = mMerchantRepository;

    }

    public void loadMerchantData() {
        if (merchantMutableLiveData.getValue() == null)
            request.setValue(new Request());
    }

    public LiveData<MerchantModel> getMerchantLiveData() {
        return merchantMutableLiveData;
    }

    public MutableLiveData<Uri> getSelectedImage() {
        return selectedImage;
    }

    public void updateData(MerchantModel merchantModel) {
        mMerchantRepository.update(merchantModel);
    }

    public void updateLocation(double latitude, double longitude) {
        mMerchantRepository.updateLocation(latitude, longitude);
    }

    @Override
    protected void onCleared() {
        mMerchantRepository.onCleared();
        super.onCleared();
    }

    public PreviewMerchantInfo getPreviewMerchantData() {
        MerchantModel merchantModel = merchantMutableLiveData.getValue();
        PreviewMerchantInfo merchantInfo = new PreviewMerchantInfo();
        merchantInfo.title = merchantModel.title;
        merchantInfo.address = merchantModel.address;
        merchantInfo.contact = merchantModel.contact;
        if (merchantModel.discountModel != null)
            merchantInfo.discount = merchantModel.discountModel.title;
        if (merchantModel.offerModel != null)
            merchantInfo.specialOffer = merchantModel.offerModel.title;
        if (merchantModel.image != null) {
            merchantInfo.previewImage = merchantModel.image.getLastPathSegment();
            merchantInfo.dataType = PayloadData.MERCHANT_PREVIEW_INFO_WITH_IMAGE;
        } else {
            merchantInfo.dataType = PayloadData.MERCHANT_PREVIEW_INFO_WITHOUT_IMAGE;

        }

        return merchantInfo;
    }

    public DetailMerchantInfo getDetailMerchantInfo() {
        MerchantModel merchantModel = merchantMutableLiveData.getValue();
        DetailMerchantInfo merchantInfo = new DetailMerchantInfo();
        merchantInfo.title = merchantModel.title;
        merchantInfo.address = merchantModel.address;
        merchantInfo.contact = merchantModel.contact;
        merchantInfo.website = merchantModel.website;
        merchantInfo.description = merchantModel.description;
        if (merchantModel.discountModel != null) {
            merchantInfo.discountInfo = new DiscountInfo();
            merchantInfo.discountInfo.title = merchantModel.discountModel.title;
            merchantInfo.discountInfo.description = merchantModel.discountModel.description;
        }
        if (merchantModel.offerModel != null) {
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
            merchantInfo.previewImage = merchantModel.image.getLastPathSegment();
        }
        merchantInfo.dataType = PayloadData.MERCHANT_DETAIL_INFO;

        return merchantInfo;
    }
}
