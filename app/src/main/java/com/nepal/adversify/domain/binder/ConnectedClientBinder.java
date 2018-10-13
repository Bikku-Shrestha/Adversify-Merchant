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
import com.nepal.adversify.data.ConnectedClient;

import javax.inject.Inject;

import androidx.appcompat.widget.AppCompatTextView;
import timber.log.Timber;


public class ConnectedClientBinder extends ItemBinder<ConnectedClient, ConnectedClientBinder.ViewHolder> {


    private OnItemClickCallback<ConnectedClient> onItemClickCallback;

    @Inject
    public ConnectedClientBinder(OnItemClickCallback<ConnectedClient> onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public ViewHolder create(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_connected_client, parent, false));
    }

    @Override
    public void bind(ViewHolder holder, ConnectedClient connectedClient) {
        Timber.d("Attempting to bind discover card ");

        if (connectedClient == null)
            return;

        holder.name.setText(connectedClient.clientInfo.name);
        holder.distance.setText(String.format(
                holder.itemView.getContext().getString(R.string.distance_surfix),
                connectedClient.distance
        ));

        if (!TextUtils.isEmpty(new String(connectedClient.clientInfo.avatar))) {
            Glide.with(holder.itemView.getContext())
                    .load(connectedClient.clientInfo.avatar)
                    .into(holder.avatar);
        }

        holder.itemView.setOnClickListener((v) -> {
            if (onItemClickCallback != null) {
                onItemClickCallback.onItemClick(v, holder.getAdapterPosition(), connectedClient);
            }
        });
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof ConnectedClient;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        return 1;
    }

    static class ViewHolder extends ItemViewHolder<ConnectedClient> {

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
