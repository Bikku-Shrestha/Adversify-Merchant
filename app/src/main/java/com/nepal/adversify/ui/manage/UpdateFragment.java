package com.nepal.adversify.ui.manage;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.generic.appbase.domain.dto.Location;
import com.generic.appbase.manager.GPSLocationManager;
import com.generic.appbase.ui.BaseFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nepal.adversify.R;
import com.nepal.adversify.domain.model.DiscountModel;
import com.nepal.adversify.domain.model.MerchantModel;
import com.nepal.adversify.domain.model.OfferModel;
import com.nepal.adversify.domain.model.OpeningModel;
import com.nepal.adversify.viewmodel.MerchantViewModel;
import com.nepal.adversify.viewmodel.MerchantViewModelFactory;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateFragment extends BaseFragment {

    private static final int OPEN_DOCUMENT_CODE = 2;

    @Inject
    MerchantViewModelFactory merchantViewModelFactory;

    //Basic information views
    private TextInputEditText mNameInputEditText;
    private TextInputEditText mAddressInputEditText;
    private TextInputEditText mContactInputEditText;
    private TextInputEditText mWebsiteInputEditText;
    private TextInputEditText mDescriptionInputEditText;
    private AppCompatImageView mPreviewImageView;

    //Opening information views
    private TextInputEditText mSundayInputEditText;
    private TextInputEditText mMondayInputEditText;
    private TextInputEditText mTuesdayInputEditText;
    private TextInputEditText mWednesdayInputEditText;
    private TextInputEditText mThursdayInputEditText;
    private TextInputEditText mFridayInputEditText;
    private TextInputEditText mSaturdayInputEditText;

    //Discount information views
    private TextInputEditText mDiscountTitleInputEditText;
    private TextInputEditText mDiscountDescriptionInputEditText;

    //Offer information views
    private TextInputEditText mOfferTitleInputEditText;
    private TextInputEditText mOfferDescriptionInputEditText;

    private AppCompatImageButton mImagePickerButton;
    private AppCompatImageButton mImageClearButton;
    private MaterialButton mUpdateButton;

    private ProgressBar mProgressBar;

    private MerchantViewModel mMerchantViewModel;

    public UpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMerchantViewModel = ViewModelProviders.of(getActivity(), merchantViewModelFactory).get(MerchantViewModel.class);
        observeData();
        loadData();
    }

    @Override
    protected View onViewReady(View view, Bundle savedInstanceState) {

        Timber.d("onViewReady");

        Toolbar mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        NavigationUI.setupWithNavController(mToolbar, Navigation.findNavController(view));

        mUpdateButton = view.findViewById(R.id.update_button);
        mImagePickerButton = view.findViewById(R.id.image_picker_button);
        mImageClearButton = view.findViewById(R.id.image_clear_button);
        mUpdateButton.setOnClickListener((v) -> {
            Timber.d("Update button clicked");
            updateData(view);

        });
        mImagePickerButton.setOnClickListener((v) -> {
            Timber.d("Image picker button clicked");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, OPEN_DOCUMENT_CODE);

        });
        mImageClearButton.setOnClickListener((v) -> {
            Timber.d("Image clear button clicked");
            mMerchantViewModel.getSelectedImage().setValue(null);
        });

        mNameInputEditText = view.findViewById(R.id.input_name);
        mAddressInputEditText = view.findViewById(R.id.input_address);
        mContactInputEditText = view.findViewById(R.id.input_conatct);
        mWebsiteInputEditText = view.findViewById(R.id.input_website);
        mDescriptionInputEditText = view.findViewById(R.id.input_description);
        mPreviewImageView = view.findViewById(R.id.image_preview);

        mSundayInputEditText = view.findViewById(R.id.input_sunday);
        mMondayInputEditText = view.findViewById(R.id.input_monday);
        mTuesdayInputEditText = view.findViewById(R.id.input_tuesday);
        mWednesdayInputEditText = view.findViewById(R.id.input_wednesday);
        mThursdayInputEditText = view.findViewById(R.id.input_thursday);
        mFridayInputEditText = view.findViewById(R.id.input_friday);
        mSaturdayInputEditText = view.findViewById(R.id.input_saturday);

        mDiscountTitleInputEditText = view.findViewById(R.id.input_discount_title);
        mDiscountDescriptionInputEditText = view.findViewById(R.id.input_discount_desc);

        mOfferTitleInputEditText = view.findViewById(R.id.input_offer_title);
        mOfferDescriptionInputEditText = view.findViewById(R.id.input_offer_desc);

        mProgressBar = view.findViewById(R.id.progress);

        return view;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_update;
    }

    private void observeData() {
        Timber.d("Observing livedata");

        mMerchantViewModel.getMerchantLiveData().observe(this, data -> {
            if (data == null) return;

            fillBasicInfo(data);
            if (data.openingModel != null)
                fillOpeningInfo(data);
            if (data.discountModel != null)
                fillDiscountInfo(data);
            if (data.offerModel != null)
                fillOfferInfo(data);


        });
        mMerchantViewModel.getSelectedImage().observe(this, data -> {
            if (data == null) {
                mPreviewImageView.setBackgroundDrawable(null);
                mImageClearButton.setVisibility(View.INVISIBLE);
                mPreviewImageView.setVisibility(View.INVISIBLE);
            } else {
                mImageClearButton.setVisibility(View.VISIBLE);
                mPreviewImageView.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(data)
                        .into(mPreviewImageView);
            }
        });
    }

    private void fillOfferInfo(MerchantModel data) {
        mOfferTitleInputEditText.setText(data.offerModel.title);
        mOfferDescriptionInputEditText.setText(data.offerModel.description);
    }

    private void fillDiscountInfo(MerchantModel data) {
        mDiscountTitleInputEditText.setText(data.discountModel.title);
        mDiscountDescriptionInputEditText.setText(data.discountModel.description);
    }

    private void fillOpeningInfo(MerchantModel data) {
        mSundayInputEditText.setText(data.openingModel.sunday);
        mMondayInputEditText.setText(data.openingModel.monday);
        mTuesdayInputEditText.setText(data.openingModel.tuesday);
        mWednesdayInputEditText.setText(data.openingModel.wednesday);
        mThursdayInputEditText.setText(data.openingModel.thursday);
        mFridayInputEditText.setText(data.openingModel.friday);
        mSaturdayInputEditText.setText(data.openingModel.saturday);
    }

    private void fillBasicInfo(MerchantModel data) {
        mNameInputEditText.setText(data.title);
        mAddressInputEditText.setText(data.address);
        mContactInputEditText.setText(data.contact);
        mWebsiteInputEditText.setText(data.website);
        mDescriptionInputEditText.setText(data.description);
        if (data.image != null) {
            mPreviewImageView.setVisibility(View.VISIBLE);
            mImageClearButton.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(data.image)
                    .into(mPreviewImageView);
        }
    }

    private void loadData() {
        Timber.d("loadData");
        mMerchantViewModel.loadMerchantData();
    }

    private void updateData(View view) {
        final String name = mNameInputEditText.getEditableText().toString().trim();
        final String address = mAddressInputEditText.getEditableText().toString().trim();
        final String contact = mContactInputEditText.getEditableText().toString().trim();
        final String website = mWebsiteInputEditText.getEditableText().toString().trim();
        final String description = mDescriptionInputEditText.getEditableText().toString().trim();
        final Uri image = mMerchantViewModel.getSelectedImage().getValue();

        final String sunday = mSundayInputEditText.getEditableText().toString().trim();
        final String monday = mMondayInputEditText.getEditableText().toString().trim();
        final String tuesday = mTuesdayInputEditText.getEditableText().toString().trim();
        final String wednesday = mWednesdayInputEditText.getEditableText().toString().trim();
        final String thursday = mThursdayInputEditText.getEditableText().toString().trim();
        final String friday = mFridayInputEditText.getEditableText().toString().trim();
        final String saturday = mSaturdayInputEditText.getEditableText().toString().trim();

        final String discountTitle = mDiscountTitleInputEditText.getEditableText().toString().trim();
        final String discountDescription = mDiscountDescriptionInputEditText.getEditableText().toString().trim();
        final String offerTitle = mOfferTitleInputEditText.getEditableText().toString().trim();
        final String offerDescription = mOfferDescriptionInputEditText.getEditableText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            mNameInputEditText.requestFocus();
            mNameInputEditText.setError("Name cannot be empty!");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            mAddressInputEditText.requestFocus();
            mAddressInputEditText.setError("Address cannot be empty!");
            return;
        }

        if (TextUtils.isEmpty(contact)) {
            mContactInputEditText.requestFocus();
            mContactInputEditText.setError("Contact cannot be empty!");
            return;
        }

        if (!TextUtils.isEmpty(website)) {
            if (!Patterns.WEB_URL.matcher(website).matches()) {
                mWebsiteInputEditText.requestFocus();
                mWebsiteInputEditText.setError("Website format in not correct!");
                return;
            }
        }

        if (TextUtils.isEmpty(description)) {
            mDescriptionInputEditText.requestFocus();
            mDescriptionInputEditText.setError("Description cannot be empty!");
            return;
        }

        if (TextUtils.isEmpty(sunday) || TextUtils.isEmpty(monday) || TextUtils.isEmpty(tuesday) || TextUtils.isEmpty(wednesday) ||
                TextUtils.isEmpty(thursday) || TextUtils.isEmpty(friday) || TextUtils.isEmpty(saturday)) {
            showToast("One or more opening hour fields is empty!");
            return;
        }

        if (!TextUtils.isEmpty(discountTitle)) {
            if (TextUtils.isEmpty(discountDescription)) {
                mDiscountDescriptionInputEditText.requestFocus();
                mDiscountDescriptionInputEditText.setError("Description cannot be empty!");
                return;
            }
        } else {
            if (!TextUtils.isEmpty(discountDescription)) {
                mDiscountTitleInputEditText.requestFocus();
                mDiscountTitleInputEditText.setError("Title cannot be empty!");
                return;
            }
        }

        if (!TextUtils.isEmpty(offerTitle)) {
            if (TextUtils.isEmpty(offerDescription)) {
                mOfferDescriptionInputEditText.requestFocus();
                mOfferDescriptionInputEditText.setError("Description cannot be empty!");
                return;
            }
        } else {
            if (!TextUtils.isEmpty(offerDescription)) {
                mOfferTitleInputEditText.requestFocus();
                mOfferTitleInputEditText.setError("Title cannot be empty!");
                return;
            }
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mUpdateButton.setVisibility(View.INVISIBLE);
        GPSLocationManager.getLocation(getContext(), location -> {

            MerchantModel merchantModel = new MerchantModel();
            merchantModel.title = name;
            merchantModel.address = address;
            merchantModel.contact = contact;
            merchantModel.website = website;
            merchantModel.image = image;
            merchantModel.description = description;

            merchantModel.openingModel = new OpeningModel();
            merchantModel.openingModel.sunday = sunday;
            merchantModel.openingModel.monday = monday;
            merchantModel.openingModel.tuesday = tuesday;
            merchantModel.openingModel.wednesday = wednesday;
            merchantModel.openingModel.thursday = thursday;
            merchantModel.openingModel.friday = friday;
            merchantModel.openingModel.saturday = saturday;

            if (!(TextUtils.isEmpty(discountTitle) && TextUtils.isEmpty(discountDescription))) {
                merchantModel.discountModel = new DiscountModel();
                merchantModel.discountModel.title = discountTitle;
                merchantModel.discountModel.description = discountDescription;
            }

            if (!(TextUtils.isEmpty(offerTitle) && TextUtils.isEmpty(offerDescription))) {
                merchantModel.offerModel = new OfferModel();
                merchantModel.offerModel.title = offerTitle;
                merchantModel.offerModel.description = offerDescription;
            }

            merchantModel.location = new Location();
            merchantModel.location.lat = location.getLatitude();
            merchantModel.location.lon = location.getLongitude();

            mMerchantViewModel.updateData(merchantModel);
            mProgressBar.setVisibility(View.GONE);
            mUpdateButton.setVisibility(View.VISIBLE);
            showToast("Updated successfully!");
            Navigation.findNavController(view).navigateUp();
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == OPEN_DOCUMENT_CODE && resultCode == RESULT_OK) {
            if (resultData != null) {
                // this is the image selected by the user
                mMerchantViewModel.getSelectedImage().setValue(resultData.getData());
            }
        }
    }


}
