package com.nepal.adversify.domain.model;

import android.net.Uri;

import com.generic.appbase.domain.dto.Category;
import com.generic.appbase.domain.dto.Location;

public class MerchantModel {

    public int id;
    public String title;
    public String address;
    public String contact;
    public String website;
    public Category category;
    public Uri image;
    public String description;

    public OpeningModel openingModel;
    public DiscountModel discountModel;
    public OfferModel offerModel;

    public Location location;

}
