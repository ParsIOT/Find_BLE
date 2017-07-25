package com.find.wifitool.Controller.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.find.wifitool.R;

import java.util.ArrayList;

/**
 * Created by root on 5/8/17.
 */

public class SliderPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<View> views = new ArrayList<>();

    public SliderPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
//        CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
        //int index = position % views.size();

        View v = views.get(position);

        TextView textView = (TextView) v.findViewById(R.id.slider_text);
        ImageView imageView = (ImageView) v.findViewById(R.id.slider_image);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mContext, PhotoViewActivity.class);
                intent.putExtra("image_url", imageView.getContentDescription());
                intent.putExtra("text", textView.getText().toString());
                mContext.startActivity(intent);*/

            }
        });

        if(v.getParent()!=null)
            ((ViewGroup)v.getParent()).removeView(v); // <- fix
        collection.addView(v);

        //        LayoutInflater inflater = LayoutInflater.from(mContext);
//        ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), collection, false);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
//        return mContext.getString(customPagerEnum.getTitleResId());
        return "title";
    }

    public void addView(View v) {
        views.add(v);
    }

    public int getSize(){
        return views.size();
    }

    public void setViews(ArrayList<View> views) {
        this.views.clear();
        this.views.addAll(views);
    }

/*
    @Override
    public int getCount() {
        return (Integer.MAX_VALUE);
        //artificially large value for infinite scrolling
    }

    public int getRealCount() {
        return views.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int virtualPosition = position % getRealCount();
        return instantiateVirtualItem(container, virtualPosition);
    }

    public Object instantiateVirtualItem(ViewGroup container, final int position) {
        //CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
        View v = views.get(position);

        TextView textView = (TextView) v.findViewById(R.id.slider_text);
        ImageView imageView = (ImageView) v.findViewById(R.id.slider_image);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        //        LayoutInflater inflater = LayoutInflater.from(mContext);
//        ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), collection, false);

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int virtualPosition = position % getRealCount();
        destroyVirtualItem(container, virtualPosition, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    public void destroyVirtualItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    public void addView(View v) {
        views.add(v);
    }

    public void setViews(ArrayList<View> views) {
        this.views.clear();
        this.views.addAll(views);
    }
*/


}