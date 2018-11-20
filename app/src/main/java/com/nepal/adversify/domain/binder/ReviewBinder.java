package com.nepal.adversify.domain.binder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahamed.multiviewadapter.ItemBinder;
import com.ahamed.multiviewadapter.ItemViewHolder;
import com.nepal.adversify.R;
import com.nepal.adversify.domain.model.ReviewModel;

import javax.inject.Inject;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatTextView;
import timber.log.Timber;


public class ReviewBinder extends ItemBinder<ReviewModel, ReviewBinder.ViewHolder> {


    @Inject
    public ReviewBinder() {
    }

    @Override
    public ViewHolder create(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void bind(ViewHolder holder, ReviewModel reviewInfo) {
        Timber.d("Attempting to bind review item ");

        if (reviewInfo == null)
            return;
        Timber.d("Review:%s, Rating: %d, Client:%s", reviewInfo.review, reviewInfo.star, reviewInfo.clientId);
        holder.name.setText(reviewInfo.clientName);
        holder.content.setText(reviewInfo.review);
        holder.ratingBar.setRating(reviewInfo.star);

    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof ReviewModel;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        return 1;
    }

    static class ViewHolder extends ItemViewHolder<ReviewModel> {

        AppCompatTextView name;
        AppCompatRatingBar ratingBar;
        AppCompatTextView content;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.client_name);
            ratingBar = itemView.findViewById(R.id.ratingbar);
            content = itemView.findViewById(R.id.review);
        }
    }
}
