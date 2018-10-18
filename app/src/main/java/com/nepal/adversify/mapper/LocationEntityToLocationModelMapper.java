package com.nepal.adversify.mapper;

import com.generic.appbase.mapper.Mapper;
import com.nepal.adversify.data.entity.LocationEntity;
import com.nepal.adversify.domain.model.LocationModel;

import javax.inject.Inject;

public class LocationEntityToLocationModelMapper implements Mapper<LocationEntity, LocationModel> {

    @Inject
    public LocationEntityToLocationModelMapper() {
    }

    @Override
    public LocationModel from(LocationEntity locationEntity) {
        LocationModel model = new LocationModel();
        model.id = locationEntity.id;
        model.lat = locationEntity.lat;
        model.lon = locationEntity.lon;

        return model;
    }

    @Override
    public LocationEntity to(LocationModel locationModel) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.id = 2;
        locationEntity.lat = locationModel.lat;
        locationEntity.lon = locationModel.lon;
        return locationEntity;
    }
}
