package com.nepal.adversify.mapper;

import com.generic.appbase.mapper.Mapper;
import com.nepal.adversify.data.entity.ReviewEntity;
import com.nepal.adversify.domain.model.ReviewModel;

import javax.inject.Inject;

public class ReviewEntityToReviewModelMapper implements Mapper<ReviewEntity, ReviewModel> {

    @Inject
    public ReviewEntityToReviewModelMapper() {
    }

    @Override
    public ReviewModel from(ReviewEntity reviewEntity) {
        ReviewModel model = new ReviewModel();
        model.clientId = reviewEntity.clientId;
        model.clientName = reviewEntity.clientName;
        model.review = reviewEntity.review;
        model.star = reviewEntity.star;

        return model;
    }

    @Override
    public ReviewEntity to(ReviewModel reviewModel) {
        ReviewEntity entity = new ReviewEntity();
        entity.clientId = reviewModel.clientId;
        entity.clientName = reviewModel.clientName;
        entity.review = reviewModel.review;
        entity.star = reviewModel.star;

        return entity;
    }
}
