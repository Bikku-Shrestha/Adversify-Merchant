package com.nepal.adversify.mapper;

import com.generic.appbase.mapper.Mapper;
import com.nepal.adversify.data.entity.DiscountEntity;
import com.nepal.adversify.domain.model.DiscountModel;

import javax.inject.Inject;

public class DiscountEntityToDiscountModelMapper implements Mapper<DiscountEntity, DiscountModel> {

    @Inject
    public DiscountEntityToDiscountModelMapper() {
    }

    @Override
    public DiscountModel from(DiscountEntity discountEntity) {
        DiscountModel model = new DiscountModel();
        model.id = discountEntity.id;
        model.title = discountEntity.title;
        model.description = discountEntity.description;

        return model;
    }

    @Override
    public DiscountEntity to(DiscountModel discountModel) {
        DiscountEntity entity = new DiscountEntity();
        entity.id = 2;
        entity.title = discountModel.title;
        entity.description = discountModel.description;
        return entity;
    }
}
