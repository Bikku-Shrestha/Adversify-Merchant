package com.nepal.adversify.data;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class PreferenceHelper {

    private static final String KEY_MERCHANT_CATEGORY_ID = "key_merchant_category_id";

    private SharedPreferences mSharedPreferences;

    @Inject
    public PreferenceHelper(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public int getMerchantCategoryId() {
        return mSharedPreferences.getInt(KEY_MERCHANT_CATEGORY_ID, -1);
    }

    public void setKeyMerchantCategoryId(int categoryId) {
        mSharedPreferences.edit().putInt(KEY_MERCHANT_CATEGORY_ID, categoryId).apply();
    }

    public boolean isMerchantCategoryIdAvailable() {
        return getMerchantCategoryId() >= 0;
    }


}
