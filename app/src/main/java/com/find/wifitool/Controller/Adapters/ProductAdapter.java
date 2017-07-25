package com.find.wifitool.Controller.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.find.wifitool.Model.Product;
import com.find.wifitool.R;
import com.find.wifitool.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Hadi Shamgholi on 3/11/2017.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    private ItemClickListener clickListener;
    private List<Product> productsList;
    private Context myContext;
    private List<Product> filtered_list;

    @Override
    public Filter getFilter() {
        return new FilterClass(ProductAdapter.this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name, price, status;
        public ImageView image;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.product_name);
            price = (TextView) view.findViewById(R.id.price);
            status = (TextView) view.findViewById(R.id.status);
            image = (ImageView) view.findViewById(R.id.image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    public ProductAdapter(List<Product> boothsList, Context context) {
        this.productsList = boothsList;
        this.myContext = context;
        this.filtered_list = boothsList;
    }

    public void setList(List<Product> list){
        this.filtered_list = list;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_product, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = filtered_list.get(position);
        holder.name.setText(product.getName());
        String s = product.getPrice() + " ریال";
        holder.price.setText(Utils.toPersianNum(s, false));
        holder.status.setText(product.getStatus() ? "موجود" : "ناموجود");
        Picasso.with(myContext).load(product.getImageUrl())
                .error(R.drawable.no_image)
                .into(holder.image);
        setScaleAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return filtered_list.size();
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        view.startAnimation(anim);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(30);
        view.startAnimation(anim);
    }


    private class FilterClass extends Filter {
        private ProductAdapter newAdapter;
        List<Product> new_filtered_list = new ArrayList<>();

        private FilterClass(ProductAdapter mAdapter) {
            super();
            this.newAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                new_filtered_list.addAll(productsList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (final Product product : productsList) {
                    if (product.getName().toLowerCase().contains(filterPattern)) {
                        new_filtered_list.add(product);
                        Log.e("ADD", "item added");
                    } else if (String.valueOf(product.getPrice()).toLowerCase().contains(filterPattern)) {
                        new_filtered_list.add(product);
                        Log.e("ADD", "item added");
                    }/* else if (booth.getDescription().toLowerCase().contains(filterPattern)) {
                        filtered_list.add(booth);
                    }*///TODO add description
                }
            }
            System.out.println("Count Number " + new_filtered_list.size());
            results.values = new_filtered_list;
            results.count = new_filtered_list.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("Count Number 2 " + ((List<Product>) results.values).size());
            this.newAdapter.setList(new_filtered_list);
            this.newAdapter.notifyDataSetChanged();
        }
    }
}