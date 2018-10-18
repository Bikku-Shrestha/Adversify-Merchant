package com.nepal.adversify.viewmodel;

import android.app.Application;

import com.generic.appbase.ui.BaseViewModel;
import com.nepal.adversify.domain.model.ClientModel;

import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public final class HomeViewModel extends BaseViewModel {

    private final MutableLiveData<TreeMap<String, ClientModel>> connectedClientLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> statusLiveData = new MutableLiveData<>();

    private final TreeMap<String, ClientModel> mConnectedClients = new TreeMap<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void addConnectedClient(String clientId, ClientModel clientModel) {
        if (!mConnectedClients.containsKey(clientId)) {
            mConnectedClients.put(clientId, clientModel);
            connectedClientLiveData.setValue(mConnectedClients);
        }
    }

    public void removeConnectedClient(String endpointId) {
        if (mConnectedClients.containsKey(endpointId)) {
            mConnectedClients.remove(endpointId);
            connectedClientLiveData.setValue(mConnectedClients);
        }
    }


    public MutableLiveData<TreeMap<String, ClientModel>> getConnectedClient() {
        return connectedClientLiveData;
    }

    public MutableLiveData<String> getStatusLiveData() {
        return statusLiveData;
    }

    @Override
    protected void onCleared() {
        statusLiveData.setValue(null);
        super.onCleared();
    }
}
