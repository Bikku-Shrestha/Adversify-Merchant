package com.nepal.adversify.ui.manage;


import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.generic.appbase.ui.BaseFragment;
import com.google.android.material.button.MaterialButton;
import com.nepal.adversify.R;
import com.nepal.adversify.domain.model.MerchantModel;
import com.nepal.adversify.viewmodel.MerchantViewModel;
import com.nepal.adversify.viewmodel.MerchantViewModelFactory;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageFragment extends BaseFragment {

    @Inject
    MerchantViewModelFactory merchantViewModelFactory;

    //Basic Info Views
    private AppCompatTextView mNameTextView;
    private AppCompatTextView mAddressTextView;
    private AppCompatTextView mContactTextView;
    private AppCompatTextView mWebsiteTextView;
    private AppCompatImageView mImagePreviewImageView;

    //Description views
    private AppCompatTextView mDescriptionTextView;

    //Opening info views
    private AppCompatTextView mSundayTextView;
    private AppCompatTextView mMondayTextView;
    private AppCompatTextView mTuesdayTextView;
    private AppCompatTextView mWednesdayTextView;
    private AppCompatTextView mThurssdayTextView;
    private AppCompatTextView mFridayTextView;
    private AppCompatTextView mSaturdayTextView;

    //Discount info views
    private AppCompatTextView mDiscountTitleTextView;
    private AppCompatTextView mDiscountDescriptionTextView;

    //Deals info views
    private AppCompatTextView mDealsTitleTextView;
    private AppCompatTextView mDealsDescriptionTextView;

    private MerchantViewModel mMerchantViewModel;

    public ManageFragment() {
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

        MaterialButton updateButton = view.findViewById(R.id.update_button);
        MaterialButton deleteButton = view.findViewById(R.id.delete_button);
        updateButton.setOnClickListener((v) -> {
            Timber.d("Update button clicked");
            NavOptions navOptions = new NavOptions.Builder()
                    .setEnterAnim(R.anim.enter_from_right)
                    .setExitAnim(R.anim.exit_to_left)
                    .setPopEnterAnim(R.anim.enter_from_left)
                    .setPopExitAnim(R.anim.exit_to_right)
                    .build();

            Navigation.findNavController(v).navigate(R.id.updateFragment, null, navOptions);
        });
        deleteButton.setOnClickListener((v) -> {
            Timber.d("Delete button clicked");

        });

        mNameTextView = view.findViewById(R.id.name);
        mAddressTextView = view.findViewById(R.id.address);
        mContactTextView = view.findViewById(R.id.contact);
        mWebsiteTextView = view.findViewById(R.id.website);
        mImagePreviewImageView = view.findViewById(R.id.image_preview);
        mDiscountTitleTextView = view.findViewById(R.id.title_discount);
        mDiscountDescriptionTextView = view.findViewById(R.id.discount_description);
        mDealsTitleTextView = view.findViewById(R.id.title_deals);
        mDealsDescriptionTextView = view.findViewById(R.id.deal_description);
        mSundayTextView = view.findViewById(R.id.sunday);
        mMondayTextView = view.findViewById(R.id.monday);
        mTuesdayTextView = view.findViewById(R.id.tuesday);
        mWednesdayTextView = view.findViewById(R.id.wednesday);
        mThurssdayTextView = view.findViewById(R.id.thursday);
        mFridayTextView = view.findViewById(R.id.friday);
        mSaturdayTextView = view.findViewById(R.id.saturday);
        mDescriptionTextView = view.findViewById(R.id.description);

        return view;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_manage;
    }

    private void observeData() {
        Timber.d("Observing livedata");

        mMerchantViewModel.getMerchantLiveData().observe(this, data -> {
            if (data == null) return;
            Timber.d("Merchant title: %s", data.title);
            fillBasicInfo(data);
            if (data.openingModel != null)
                fillOpeningInfo(data);
            if (data.discountModel != null)
                fillDiscountInfo(data);
            if (data.offerModel != null)
                fillOfferInfo(data);
        });
    }

    private void loadData() {
        Timber.d("loadData");
        mMerchantViewModel.loadMerchantData();
    }

    private void fillOfferInfo(MerchantModel data) {
        mDealsTitleTextView.setText(data.offerModel.title);
        mDealsDescriptionTextView.setText(data.offerModel.description);
    }

    private void fillDiscountInfo(MerchantModel data) {
        mDiscountTitleTextView.setText(data.discountModel.title);
        mDiscountDescriptionTextView.setText(data.discountModel.description);
    }

    private void fillOpeningInfo(MerchantModel data) {
        mSundayTextView.setText(data.openingModel.sunday);
        mMondayTextView.setText(data.openingModel.monday);
        mTuesdayTextView.setText(data.openingModel.tuesday);
        mWednesdayTextView.setText(data.openingModel.wednesday);
        mThurssdayTextView.setText(data.openingModel.thursday);
        mFridayTextView.setText(data.openingModel.friday);
        mSaturdayTextView.setText(data.openingModel.saturday);
    }

    private void fillBasicInfo(MerchantModel data) {
        mNameTextView.setText(data.title);
        mAddressTextView.setText(data.address);
        mContactTextView.setText(data.contact);
        mWebsiteTextView.setText(data.website);
        mDescriptionTextView.setText(data.description);
        if (data.image != null) {
            mImagePreviewImageView.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(data.image)
                    .into(mImagePreviewImageView);
        } else
            mImagePreviewImageView.setVisibility(View.INVISIBLE);
    }

}
