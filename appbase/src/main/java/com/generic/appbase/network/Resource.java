package com.generic.appbase.network;

import com.generic.appbase.exception.AppException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.generic.appbase.network.Resource.Status.CACHED;
import static com.generic.appbase.network.Resource.Status.CACHED_LOADING;
import static com.generic.appbase.network.Resource.Status.ERROR;
import static com.generic.appbase.network.Resource.Status.FRESH;
import static com.generic.appbase.network.Resource.Status.NETWORK_LOADING;

public class Resource<T> {
    private final Status status;
    private final T data;
    private final AppException exception;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable AppException exception) {
        this.status = status;
        this.data = data;
        this.exception = exception;
    }

    public static <T> Resource<T> error(AppException exception, @Nullable T data) {
        return new Resource<>(ERROR, data, exception);
    }

    public static <T> Resource<T> cached(@Nullable T data) {
        return new Resource<>(CACHED, data, null);
    }

    public static <T> Resource<T> fresh(@Nullable T data) {
        return new Resource<>(FRESH, data, null);
    }

    public static <T> Resource<T> cachedLoading(@Nullable T data) {
        return new Resource<>(CACHED_LOADING, data, null);
    }

    public static <T> Resource<T> networkLoading(@Nullable T data) {
        return new Resource<>(NETWORK_LOADING, data, null);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public AppException getException() {
        return exception;
    }

    public enum Status {
        CACHED, FRESH, ERROR, CACHED_LOADING, NETWORK_LOADING
    }
}
