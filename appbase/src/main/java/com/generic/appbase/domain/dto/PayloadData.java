package com.generic.appbase.domain.dto;

import java.io.Serializable;

public abstract class PayloadData implements Serializable {

    public static final int MERCHANT_PREVIEW_INFO_WITHOUT_IMAGE = 1;
    public static final int MERCHANT_PREVIEW_INFO_WITH_IMAGE = 2;
    public static final int MERCHANT_DETAIL_INFO = 3;
    public static final int CLIENT_INFO_WITHOUT_IMAGE = 4;
    public static final int CLIENT_INFO_WITH_IMAGE = 5;

    public int dataType;
    public boolean isPartial;

}
