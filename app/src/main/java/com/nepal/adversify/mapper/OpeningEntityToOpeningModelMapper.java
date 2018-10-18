package com.nepal.adversify.mapper;

import com.generic.appbase.mapper.Mapper;
import com.nepal.adversify.data.entity.OpeningEntity;
import com.nepal.adversify.domain.model.OpeningModel;

import javax.inject.Inject;

public class OpeningEntityToOpeningModelMapper implements Mapper<OpeningEntity, OpeningModel> {

    @Inject
    public OpeningEntityToOpeningModelMapper() {
    }

    @Override
    public OpeningModel from(OpeningEntity openingEntity) {
        OpeningModel model = new OpeningModel();
        model.id = openingEntity.id;
        model.sunday = openingEntity.sunday;
        model.monday = openingEntity.monday;
        model.tuesday = openingEntity.tuesday;
        model.wednesday = openingEntity.wednesday;
        model.thursday = openingEntity.thursday;
        model.friday = openingEntity.friday;
        model.saturday = openingEntity.saturday;

        return model;
    }

    @Override
    public OpeningEntity to(OpeningModel openingModel) {
        OpeningEntity entity = new OpeningEntity();
        entity.id = openingModel.id;
        entity.sunday = openingModel.sunday;
        entity.monday = openingModel.monday;
        entity.tuesday = openingModel.tuesday;
        entity.wednesday = openingModel.wednesday;
        entity.thursday = openingModel.thursday;
        entity.friday = openingModel.friday;
        entity.saturday = openingModel.saturday;

        return entity;
    }
}
