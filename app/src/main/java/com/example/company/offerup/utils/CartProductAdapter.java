package com.example.company.offerup.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.company.offerup.R;

import java.util.List;

/**
 * Created by Mohamed Sayed on 10/20/2017.
 */

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.MyViewHolder> {
    private List<Product> productList;

    public CartProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_products, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.quantity.setText(product.getQuantity());
        holder.price.setText(product.getPrice()+" KWD");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, quantity, price;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.cartProductName);
            quantity = (TextView) view.findViewById(R.id.cartProductQuantity);
            price = (TextView) view.findViewById(R.id.cartProductPrice);
        }
    }

}
