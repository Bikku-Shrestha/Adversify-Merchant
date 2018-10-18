package com.nepal.adversify.mapper;

import com.generic.appbase.domain.dto.ReviewInfo;
import com.generic.appbase.mapper.Mapper;
import com.nepal.adversify.domain.model.ReviewModel;

import javax.inject.Inject;

public class ReviewInfoToReviewModelMapper implements Mapper<ReviewInfo, ReviewModel> {

    @Inject
    public ReviewInfoToReviewModelMapper() {
    }

    @Override
    public ReviewModel from(ReviewInfo reviewInfo) {
        ReviewModel model = new ReviewModel();
        model.clientId = reviewInfo.clientId;
        model.clientName = reviewInfo.clientName;
        model.star = reviewInfo.star;
        model.review = reviewInfo.content;

        return model;
    }

    @Override
    public ReviewInfo to(ReviewModel reviewModel) {
        return null;
    }
}
