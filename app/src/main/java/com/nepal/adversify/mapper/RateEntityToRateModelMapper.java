package com.nepal.adversify.mapper;

import com.generic.appbase.mapper.Mapper;
import com.nepal.adversify.data.entity.RateEntity;
import com.nepal.adversify.domain.model.RateModel;

import javax.inject.Inject;

public class RateEntityToRateModelMapper implements Mapper<RateEntity, RateModel> {

    @Inject
    public RateEntityToRateModelMapper() {
    }

    @Override
    public RateModel from(RateEntity rateEntity) {
        RateModel model = new RateModel();
        model.clientId = rateEntity.clientId;
        model.clientName = rateEntity.clientName;
        model.star = rateEntity.star;

        return model;
    }

    @Override
    public RateEntity to(RateModel rateModel) {
        RateEntity entity = new RateEntity();
        entity.clientId = rateModel.clientId;
        entity.clientName = rateModel.clientName;
        entity.star = rateModel.star;

        return entity;
    }
}
