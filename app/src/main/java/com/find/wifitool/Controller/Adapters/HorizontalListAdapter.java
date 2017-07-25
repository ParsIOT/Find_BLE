package com.find.wifitool.Controller.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.find.wifitool.Model.EmkanatRefahi;
import com.find.wifitool.R;
import com.squareup.picasso.Picasso;


import java.util.List;


public class HorizontalListAdapter extends RecyclerView.Adapter<HorizontalListAdapter.MyViewHolder> {
    private static final int FADE_DURATION = 70;
    private ItemClickListener clickListener;
    private ItemClickListener longClickListener;
    private Context context;

    private List<EmkanatRefahi> itemList;

    private int mLastAnimatedPosition = -1;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView title;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.horizontal_title);
            image = (ImageView) view.findViewById(R.id.horizontal_image);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null) longClickListener.onClick(v, getAdapterPosition());
            return false;
        }
    }

    public HorizontalListAdapter(List<EmkanatRefahi> itemList, Context context) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public HorizontalListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_horizontal, parent, false);

        return new HorizontalListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HorizontalListAdapter.MyViewHolder holder, int position) {
        EmkanatRefahi item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.image.setImageResource(item.getImageRes());

        setFadeAnimation(holder.itemView, position);
    }

    private void setFadeAnimation(View itemView, int position) {
        if (position > mLastAnimatedPosition) {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(FADE_DURATION);
            itemView.startAnimation(anim);
            mLastAnimatedPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public void setOnLongItemClickListener(ItemClickListener itemClickListener) {
        this.longClickListener = itemClickListener;
    }

}
