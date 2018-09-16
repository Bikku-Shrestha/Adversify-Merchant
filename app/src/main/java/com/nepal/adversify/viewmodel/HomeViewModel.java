package com.nepal.adversify.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import com.generic.appbase.domain.dto.BaseMerchantInfo;
import com.generic.appbase.domain.dto.MerchantInfo;
import com.generic.appbase.domain.dto.UserInfo;
import com.generic.appbase.ui.BaseViewModel;
import com.generic.appbase.utils.CommonUtils;
import com.nepal.adversify.R;

import java.util.TreeMap;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public final class HomeViewModel extends BaseViewModel {

    private final MutableLiveData<UserInfo> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<MerchantInfo> merchantLiveData = new MutableLiveData<>();
    private final MutableLiveData<TreeMap<String, String>> connectedClientLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> statusLiveData = new MutableLiveData<>();

    private final TreeMap<String, String> mConnectedClients = new TreeMap<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void addConnectedClient(String clientId, String endpointName) {
        if (!mConnectedClients.containsKey(clientId)) {
            mConnectedClients.put(clientId, endpointName);
            connectedClientLiveData.setValue(mConnectedClients);
        }
    }

    public void removeConnectedClient(String endpointId) {
        if (mConnectedClients.containsKey(endpointId)) {
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
        BaseMerchantInfo baseMerchantInfo = new BaseMerchantInfo();
        baseMerchantInfo.title = "Beijing Garden Chinese Restaurant";
        baseMerchantInfo.subtitle = "Chinese Restaurant";
        baseMerchantInfo.contact = "985-1035000";
        baseMerchantInfo.location = "Kathmandu 44600";
        baseMerchantInfo.specialOffer = "Momo";
        baseMerchantInfo.discount = "25%";
        return baseMerchantInfo;
    }

    public MutableLiveData<MerchantInfo> getMerchantLiveData() {
        return merchantLiveData;
    }

    public MutableLiveData<UserInfo> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<TreeMap<String, String>> getConnectedClient() {
        return connectedClientLiveData;
    }

    public MutableLiveData<String> getStatusLiveData() {
        return statusLiveData;
    }

    public void setStatusMessage(String string) {
        statusLiveData.setValue(string);
    }
}
