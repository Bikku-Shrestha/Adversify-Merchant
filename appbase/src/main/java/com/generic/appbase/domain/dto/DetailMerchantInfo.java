package com.generic.appbase.domain.dto;

import java.io.Serializable;

public class DetailMerchantInfo extends PreviewMerchantInfo implements Serializable {

    public String description;
    public String website;
    public DiscountInfo discountInfo = new DiscountInfo();
    public SpecialOfferInfo specialOfferInfo = new SpecialOfferInfo();
    public OpeningInfo openingInfo = new OpeningInfo();
    public Location location = new Location();

}
