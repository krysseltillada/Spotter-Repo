package com.lmos.spotter.SearchInterface.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lmos.spotter.R;

/**
 * Created by emman on 7/7/2017.
 */

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealsViewHolder>{

    @Override
    public DealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View dealView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deals_list_item, parent, false);

        return new DealsViewHolder(dealView);
    }

    @Override
    public void onBindViewHolder(DealsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class DealsViewHolder extends RecyclerView.ViewHolder{

        ImageView dealImg;

        public DealsViewHolder(View itemView) {
            super(itemView);
            dealImg = (ImageView) itemView.findViewById(R.id.deal_img_holder);
        }
    }

}
