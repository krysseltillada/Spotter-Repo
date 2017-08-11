package com.lmos.spotter.SearchInterface.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmos.spotter.Deals;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by emman on 7/7/2017.
 */

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealsViewHolder> {

    public static OnBookListener onBookListener;
    List<Deals> deals;
    Context context;

    public DealsAdapter(Context context, List<Deals> deals) {
        this.context = context;
        this.deals = deals;
    }

    public void setOnBookListener(OnBookListener onBookListener) {
        DealsAdapter.onBookListener = onBookListener;
    }

    @Override
    public DealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View dealView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deals_list_item, parent, false);

        return new DealsViewHolder(dealView);
    }

    @Override
    public void onBindViewHolder(DealsViewHolder holder, final int position) {

        holder.dealName.setText(deals.get(position).getDealName());
        holder.dealDesc.setText(deals.get(position).getDealDesc());

        Picasso.with(context)
                .load(deals.get(position).getDealImg())
                .placeholder(R.drawable.landscape_placeholder)
                .into(holder.dealImg);

        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contact = deals.get(position).getContact();
                Intent intent =
                        (contact.startsWith("https://") || contact.startsWith("https://")) ?
                        new Intent(Intent.ACTION_VIEW) : new Intent(Intent.ACTION_DIAL);

                if(intent.getAction().equals(Intent.ACTION_DIAL))
                    contact = "tel:" + deals.get(position).getContact();

                intent.setData(Uri.parse(contact));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public interface OnBookListener {
        void OnBookPlaceListener(View v, String link);
    }

    public class DealsViewHolder extends RecyclerView.ViewHolder {

        TextView dealName, dealDesc;
        ImageView dealImg;
        ImageButton expand, contact;

        public DealsViewHolder(View itemView) {
            super(itemView);

            dealName = (TextView) itemView.findViewById(R.id.deal_name);
            dealDesc = (TextView) itemView.findViewById(R.id.deal_desc);
            dealImg = (ImageView) itemView.findViewById(R.id.deal_img);
            expand = (ImageButton) itemView.findViewById(R.id.expand_deal);
            contact = (ImageButton) itemView.findViewById(R.id.deal_contact);

            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dealDesc.getVisibility() == View.GONE)
                        toggleDesc(View.VISIBLE, AnimationUtils.loadAnimation(context, R.anim.slide_up));
                    else
                        toggleDesc(View.GONE, AnimationUtils.loadAnimation(context, R.anim.slide_down));
                }
            });

        }

        private void toggleDesc(int visibility, Animation animation){

            int color = context.getResources().getColor(R.color.white);
            float alpha = 1.0f;

            if(visibility == View.VISIBLE){
                color = context.getResources().getColor(R.color.colorPrimaryDark);
                alpha = 0.5f;
            }

            dealDesc.setVisibility(visibility);
            dealDesc.setAnimation(animation);
            contact.setVisibility(visibility);
            dealImg.setAlpha(alpha);
            expand.setAnimation(
                    (visibility == View.VISIBLE)?
                            AnimationUtils.loadAnimation(context, R.anim.rotate)
                            :
                            AnimationUtils.loadAnimation(context, R.anim.rev_rotate)
            );
            dealName.setTextColor(color);
        }

    }

}