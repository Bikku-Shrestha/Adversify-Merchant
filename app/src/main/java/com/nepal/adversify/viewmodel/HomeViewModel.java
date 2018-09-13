package com.nepal.adversify.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import com.generic.appbase.ui.BaseViewModel;
import com.generic.appbase.utils.CommonUtils;
import com.nepal.adversify.R;
import com.nepal.adversify.data.BaseMerchantInfo;
import com.nepal.adversify.data.MerchantInfo;
import com.nepal.adversify.data.UserInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public final class HomeViewModel extends BaseViewModel {

    private final MutableLiveData<UserInfo> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<MerchantInfo> merchantLiveData = new MutableLiveData<>();
    private final MutableLiveData<Set<String>> connectedClientLiveData = new MutableLiveData<>();

    private final Set<String> mConnectedClients = new HashSet<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void addConnectedClient(String clientId) {
        if (mConnectedClients.add(clientId)) {
            connectedClientLiveData.setValue(mConnectedClients);
        }
    }

    public void removeConnectedClient(String endpointId) {
        if (mConnectedClients.contains(endpointId)) {
            mConnectedClients.remove(endpointId);
            connectedClientLiveData.setValue(mConnectedClients);
        }
    }

    public void loadUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.id = UUID.randomUUID().toString();
        userInfo.username = "smartbahun";
        userInfo.password = "12345";

        userLiveData.setValue(userInfo);
    }

    @SuppressLint("CheckResult")
    public void loadMerchantInfo() {
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.title = "Beijing Garden Chinese Restaurant";
        merchantInfo.subtitle = "Chinese Restaurant";
        merchantInfo.contact = "985-1035000";
        merchantInfo.location = "Kathmandu 44600";
        merchantInfo.website = "https://bggrdnchres.com";
        merchantInfo.description = "Cosy · Casual · Good for kids";
        merchantInfo.previewImage = CommonUtils.drawableToByteArray(getApplication().getApplicationContext(),
                R.drawable.dummy_merchant_preview);
        merchantInfo.specialOffer = "Momo";
        merchantInfo.specialOfferDescription = "Delicious momo";
        merchantInfo.discount = "25%";
        merchantInfo.discountDescription = "Get 25% off on momos and chowmein";

        merchantLiveData.setValue(merchantInfo);
    }

    public UserInfo getUserInfo() {
        return userLiveData.getValue();
    }

    public MerchantInfo getMerchantInfo() {
        return merchantLiveData.getValue();
    }

    public void setMerchantInfo(final MerchantInfo merchantInfo) {
        this.merchantLiveData.setValue(merchantInfo);
    }

    public BaseMerchantInfo getInitialPayload() {
        return merchantLiveData.getValue();
    }

    public MutableLiveData<MerchantInfo> getMerchantLiveData() {
        return merchantLiveData;
    }

    public MutableLiveData<UserInfo> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<Set<String>> getConnectedClient() {
        return connectedClientLiveData;
    }

}
