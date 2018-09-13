package com.generic.appbase.network;

import com.generic.appbase.data.model.ApiResponse;
import com.generic.appbase.exception.AppException;
import com.generic.appbase.utils.rx.SchedulerProvider;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private final SchedulerProvider mSchedulerProvider;
    private final CompositeDisposable mCompositeDisposable;

    @MainThread
    public NetworkBoundResource(final SchedulerProvider schedulerProvider, CompositeDisposable mCompositeDisposable) {
        mSchedulerProvider = schedulerProvider;
        this.mCompositeDisposable = mCompositeDisposable;
        result.setValue(Resource.cachedLoading(null));
        final LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.setValue(Resource.cached(data));
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            }

        });


    }

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    // Called to get the cached data from the database
    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    // Called to create the API call.
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected void onFetchFailed() {
    }

    // returns a LiveData that represents the resource
    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        final LiveData<ApiResponse<RequestType>> apiResponse = createCall();

        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.setValue(Resource.networkLoading(null));
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                saveResultAndReInit(response);
            } else {
                onFetchFailed();
                result.addSource(dbSource, newData -> result.setValue(
                        Resource.error(new AppException(response.getError()), newData)));
            }
        });
    }

    @MainThread
    private void saveResultAndReInit(final ApiResponse<RequestType> response) {
        Completable.fromAction(() -> saveCallResult(response.body))
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(loadFromDb(), newData -> result.setValue(Resource.fresh(newData)));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logy.e(e);
                    }
                });
    }
}