package com.nepal.adversify.mapper;

import com.generic.appbase.mapper.Mapper;
import com.nepal.adversify.data.entity.SpecialOfferEntity;
import com.nepal.adversify.domain.model.OfferModel;

import javax.inject.Inject;

public class OfferEntityToOfferModelMapper implements Mapper<SpecialOfferEntity, OfferModel> {

    @Inject
    public OfferEntityToOfferModelMapper() {
    }

    @Override
    public OfferModel from(SpecialOfferEntity offerEntity) {
        OfferModel model = new OfferModel();
        model.id = offerEntity.id;
        model.title = offerEntity.title;
        model.description = offerEntity.description;

        return model;
    }

    @Override
    public SpecialOfferEntity to(OfferModel offerModel) {
        SpecialOfferEntity entity = new SpecialOfferEntity();
        entity.id = 2;
        entity.title = offerModel.title;
        entity.description = offerModel.description;

        return entity;
    }
}
