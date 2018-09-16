package com.generic.appbase.domain.dto;

import java.io.Serializable;

public class MerchantInfo extends BaseMerchantInfo implements Serializable {

    public String description;
    public String website;
    public String specialOfferDescription;
    public String discountDescription;
    public byte[] previewImage;

}
