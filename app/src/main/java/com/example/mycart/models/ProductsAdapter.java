package com.example.mycart.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycart.R;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnProductInteractionListener listener;

    // ממשק לפעולות על מוצרים
    public interface OnProductInteractionListener {
        void onQuantityIncrease(Product product);
        void onQuantityDecrease(Product product);
        void onDeleteProduct(Product product);
    }

    public ProductsAdapter(List<Product> productList, OnProductInteractionListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productQuantity.setText(String.valueOf(product.getQuantity()));

        // פעולות על הכפתורים
        holder.increaseButton.setOnClickListener(v -> listener.onQuantityIncrease(product));
        holder.decreaseButton.setOnClickListener(v -> listener.onQuantityDecrease(product));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteProduct(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder אחראי למיון פריטי ה-RecyclerView
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productQuantity;
        ImageButton increaseButton, decreaseButton, deleteButton;  // השתמש ב-ImageButton במקום Button

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            // חיבור רכיבי ה-UI
            productName = itemView.findViewById(R.id.productName);
            productQuantity = itemView.findViewById(R.id.productQuantity);

            // כאן אנו משתמשים ב-ImageButton ולא ב-Button
            increaseButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseButton = itemView.findViewById(R.id.decreaseQuantityButton);
            deleteButton = itemView.findViewById(R.id.deleteProductButton);
        }
    }
}
