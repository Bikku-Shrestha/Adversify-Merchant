package com.nepal.adversify.mapper;

import android.net.Uri;
import android.text.TextUtils;

import com.generic.appbase.domain.dto.Category;
import com.generic.appbase.mapper.Mapper;
import com.nepal.adversify.data.entity.MerchantEntity;
import com.nepal.adversify.domain.model.MerchantModel;

import javax.inject.Inject;

public class MerchantEntityToMerchantModelMapper implements Mapper<MerchantEntity, MerchantModel> {

    @Inject
    public MerchantEntityToMerchantModelMapper() {
    }

    @Override
    public MerchantModel from(MerchantEntity merchantEntity) {
        MerchantModel model = new MerchantModel();
        model.id = merchantEntity.id;
        model.title = merchantEntity.title;
        model.address = merchantEntity.address;
        model.website = merchantEntity.website;
        model.contact = merchantEntity.contact;
        model.description = merchantEntity.description;
        model.category = Category.valueOf(merchantEntity.category);
        model.image = TextUtils.isEmpty(merchantEntity.image) ? null : Uri.parse(merchantEntity.image);

        return model;
    }

    @Override
    public MerchantEntity to(MerchantModel model) {
        MerchantEntity entity = new MerchantEntity();
        entity.id = 1;
        entity.title = model.title;
        entity.address = model.address;
        entity.website = model.website;
        entity.contact = model.contact;
        entity.description = model.description;
        entity.category = model.category.name();
        entity.image = model.image == null ? "" : model.image.toString();
        return entity;
    }
}
