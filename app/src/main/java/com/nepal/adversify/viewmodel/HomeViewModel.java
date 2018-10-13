package com.nepal.adversify.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import com.generic.appbase.domain.dto.Location;
import com.generic.appbase.domain.dto.MerchantInfo;
import com.generic.appbase.ui.BaseViewModel;
import com.generic.appbase.utils.CommonUtils;
import com.nepal.adversify.R;
import com.nepal.adversify.data.ConnectedClient;

import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public final class HomeViewModel extends BaseViewModel {

    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private final MutableLiveData<MerchantInfo> merchantLiveData = new MutableLiveData<>();
    private final MutableLiveData<TreeMap<String, ConnectedClient>> connectedClientLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> statusLiveData = new MutableLiveData<>();

    private final TreeMap<String, ConnectedClient> mConnectedClients = new TreeMap<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void addConnectedClient(String clientId, ConnectedClient connectedClient) {
        if (!mConnectedClients.containsKey(clientId)) {
            double dis = CommonUtils.calculateDistance(locationLiveData.getValue(),
                    connectedClient.clientInfo.location);
            connectedClient.distance = String.valueOf(dis);
            mConnectedClients.put(clientId, connectedClient);
            connectedClientLiveData.setValue(mConnectedClients);
        }
    }

    public void removeConnectedClient(String endpointId) {
        if (mConnectedClients.containsKey(endpointId)) {
            mConnectedClients.remove(endpointId);
            connectedClientLiveData.setValue(mConnectedClients);
        }
    }

    public void setLocation(double latitude, double longitude) {
        Location location = new Location();
        location.lon = longitude;
        location.lat = latitude;

        MerchantInfo value = merchantLiveData.getValue();
        if (value != null) {
            value.location = location;
            merchantLiveData.setValue(value);
        }

        locationLiveData.setValue(location);
    }

    @SuppressLint("CheckResult")
    public void loadMerchantInfo() {
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.title = "Beijing Garden Chinese Restaurant";
        merchantInfo.subtitle = "Chinese Restaurant";
        merchantInfo.contact = "985-1035000";
        merchantInfo.address = "Kathmandu 44600";
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

    public MutableLiveData<MerchantInfo> getMerchantLiveData() {
        return merchantLiveData;
    }

    public MutableLiveData<TreeMap<String, ConnectedClient>> getConnectedClient() {
        return connectedClientLiveData;
    }

    public MutableLiveData<String> getStatusLiveData() {
        return statusLiveData;
    }

    public void setStatusMessage(String string) {
        statusLiveData.setValue(string);
    }


    public MutableLiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

}
