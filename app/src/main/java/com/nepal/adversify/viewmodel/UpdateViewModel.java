package com.nepal.adversify.viewmodel;

import android.app.Application;
import android.net.Uri;

import com.generic.appbase.ui.BaseViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class UpdateViewModel extends BaseViewModel {

    private final MutableLiveData<Uri> selectedImage = new MutableLiveData<>();

    public UpdateViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Uri> getSelectedImage() {
        return selectedImage;
    }

}
