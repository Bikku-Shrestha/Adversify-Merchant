package com.generic.appbase.domain.dto;

import java.io.Serializable;

public class MerchantInfo implements Serializable {

    public String title;
    public String subtitle;
    public String address;
    public String contact;
    public String specialOffer;
    public String discount;
    public String description;
    public String website;
    public String specialOfferDescription;
    public String discountDescription;
    public byte[] previewImage;
    public Location location = new Location();

}
