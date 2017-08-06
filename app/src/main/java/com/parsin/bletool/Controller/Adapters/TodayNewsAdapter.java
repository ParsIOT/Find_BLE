package com.parsin.bletool.Controller.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.parsin.bletool.Model.TodayNews;
import com.parsin.bletool.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class TodayNewsAdapter  extends RecyclerView.Adapter<TodayNewsAdapter.MyViewHolder> {
    private ItemClickListener clickListener;
    private Context context;
    private List<TodayNews> newsList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, text;
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title_item_news_main_activity);
            text = (TextView) view.findViewById(R.id.text_item_news_main_activity);
            imageView = (ImageView) view.findViewById(R.id.image_item_news_main_activity);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }

    public TodayNewsAdapter(List<TodayNews> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @Override
    public TodayNewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_news_main_activity, parent, false);

        return new TodayNewsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TodayNewsAdapter.MyViewHolder holder, int position) {
        TodayNews news = newsList.get(position);
        holder.title.setText(news.getTitle());
        holder.text.setText(news.getText());
        Picasso.with(context).load(news.getImage_url()).error(R.mipmap.news)
                .into(holder.imageView);
        setScaleAnimation(holder.itemView);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(50);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}