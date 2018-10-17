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
import io.reactivex.disposables.CompositeDisposable;

public class MerchantViewModelFactory implements ViewModelProvider.Factory {

    private final Application mApplication;
    private final MerchantRepository mMerchantRepository;
    private CompositeDisposable mDisposable;

    @Inject
    public MerchantViewModelFactory(Application application, MerchantRepository mMerchantRepository,
                                    CompositeDisposable mDisposable) {
        this.mApplication = application;
        this.mMerchantRepository = mMerchantRepository;
        this.mDisposable = mDisposable;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MerchantViewModel.class)) {
            return (T) new MerchantViewModel(mApplication, mMerchantRepository, mDisposable);
        }
        throw new IllegalArgumentException("Wrong ViewModel class");
    }
}
