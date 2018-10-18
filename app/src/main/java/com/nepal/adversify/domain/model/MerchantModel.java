package com.nepal.adversify.domain.model;

import android.net.Uri;

import com.generic.appbase.domain.dto.Category;
import com.generic.appbase.domain.dto.Location;

import java.util.List;

public class MerchantModel {

    public int id;
    public String title;
    public String address;
    public String contact;
    public String website;
    public Category category;
    public Uri image;
    public String description;

    public int rating;

    public boolean hasDiscount;
    public boolean hasOffer;
    public OpeningModel openingModel = null;
    public DiscountModel discountModel = null;
    public OfferModel offerModel = null;
    public List<ReviewModel> reviewModels;

    public Location location;

}
