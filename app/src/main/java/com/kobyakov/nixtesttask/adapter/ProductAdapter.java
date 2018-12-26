package com.kobyakov.nixtesttask.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.kobyakov.nixtesttask.R;
import com.kobyakov.nixtesttask.model.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private List<Product> products;
    private List<Product> productsTempAll;
    private Context context;
    private final String TAG = getClass().getSimpleName();
    private static final int LAYOUT = R.layout.product_list_row;
    private RequestManager glide;

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_title)
        TextView productTitle;
        @BindView(R.id.product_date)
        TextView productDate;
        @BindView(R.id.product_img)
        ImageView productImg;
        @BindView(R.id.product_container)
        LinearLayout productContainer;

        MyViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public ProductAdapter(Context context, RequestManager glide) {
        this.context = context;
        this.glide = glide;
    }

    public List<Product> getProducts() {
        return products;
    }

    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(LAYOUT, parent, false);

        return new ProductAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
        if (products != null) {
            Product currentProduct = products.get(position);
            String path = currentProduct.getImagePath();

            holder.productTitle.setText(currentProduct.getTitle());
            holder.productDate.setText(currentProduct.getDate());

            glide.load(path)
                    .error(glide.load(R.drawable.help))
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.productImg);

            holder.productContainer.setBackgroundColor(currentProduct.isSelected() ? context.getResources().getColor(R.color.colorDrawerIconsChecked) : context.getResources().getColor(R.color.colorWhite));

            holder.itemView.setOnClickListener(view -> {
                holder.itemView.setBackgroundColor(currentProduct.isSelected() ? context.getResources().getColor(R.color.colorDrawerIconsChecked) : context.getResources().getColor(R.color.colorWhite));
            });
        }
    }

    public void setData(List<Product> prosResponse) {
        products = new ArrayList<>(prosResponse);
        productsTempAll = new ArrayList<>(prosResponse);
        notifyDataSetChanged();
        Log.d(TAG, "setData");
    }

    @Override
    public int getItemCount() {
        if (products != null) {
            return products.size();
        } else {
            return 0;
        }
    }

    public void filter(String text) {
        List<Product> proPlayersFiltered = new ArrayList<>();

        if (!text.isEmpty() && productsTempAll != null && productsTempAll.size() > 0) {
            text = text.toLowerCase();
            for (Product product : productsTempAll) {
                if (product.getTitle() != null && product.getTitle().toLowerCase().trim().contains(text)) {
                    proPlayersFiltered.add(product);
                }
            }
        } else {
            proPlayersFiltered = productsTempAll;
        }
        products = proPlayersFiltered;
        notifyDataSetChanged();
    }
}