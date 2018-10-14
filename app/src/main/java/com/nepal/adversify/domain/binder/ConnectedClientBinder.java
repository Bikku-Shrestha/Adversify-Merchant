package com.nepal.adversify.domain.binder;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ahamed.multiviewadapter.ItemBinder;
import com.ahamed.multiviewadapter.ItemViewHolder;
import com.bumptech.glide.Glide;
import com.generic.appbase.domain.event.OnItemClickCallback;
import com.google.android.material.chip.Chip;
import com.nepal.adversify.R;
import com.nepal.adversify.domain.model.ClientModel;

import javax.inject.Inject;

import androidx.appcompat.widget.AppCompatTextView;
import timber.log.Timber;


public class ConnectedClientBinder extends ItemBinder<ClientModel, ConnectedClientBinder.ViewHolder> {


    private OnItemClickCallback<ClientModel> onItemClickCallback;

    @Inject
    public ConnectedClientBinder(OnItemClickCallback<ClientModel> onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public ViewHolder create(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_connected_client, parent, false));
    }

    @Override
    public void bind(ViewHolder holder, ClientModel clientModel) {
        Timber.d("Attempting to bind discover card ");

        if (clientModel == null) {
            Timber.d("Null client model");
            return;
        }

        holder.name.setText(clientModel.name);
        holder.distance.setText(String.format(
                holder.itemView.getContext().getString(R.string.distance_surfix),
                clientModel.distance
        ));

        if (!TextUtils.isEmpty(clientModel.avatar)) {
            Glide.with(holder.itemView.getContext())
                    .load(clientModel.avatar)
                    .into(holder.avatar);
        }

        holder.itemView.setOnClickListener((v) -> {
            if (onItemClickCallback != null) {
                onItemClickCallback.onItemClick(v, holder.getAdapterPosition(), clientModel);
            }
        });
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof ClientModel;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        return 1;
    }

    static class ViewHolder extends ItemViewHolder<ClientModel> {

        AppCompatTextView name;
        Chip distance;
        ImageView avatar;

        ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            distance = itemView.findViewById(R.id.distance);
        }
    }
}
