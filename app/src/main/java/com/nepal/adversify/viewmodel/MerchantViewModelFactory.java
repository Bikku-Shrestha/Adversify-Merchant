/*
 * *
 *  * All Rights Reserved.
 *  * Created by Suzn on 2018.
 *  * suzanparajuli@gmail.com
 *
 */

package com.nepal.adversify.viewmodel;

import android.app.Application;

import com.nepal.adversify.data.repository.MerchantRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MerchantViewModelFactory implements ViewModelProvider.Factory {

    private final Application mApplication;
    private final MerchantRepository mMerchantRepository;

    @Inject
    public MerchantViewModelFactory(Application application, MerchantRepository mMerchantRepository) {
        this.mApplication = application;
        this.mMerchantRepository = mMerchantRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MerchantViewModel.class)) {
            return (T) new MerchantViewModel(mApplication, mMerchantRepository);
        }
        throw new IllegalArgumentException("Wrong ViewModel class");
    }
}
