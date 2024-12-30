package com.example.mycart.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycart.R;
import com.example.mycart.models.ProductsAdapter;
import com.example.mycart.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView productsRecyclerView;
    private ProductsAdapter adapter;
    private List<Product> productList;
    private DatabaseReference cartRef;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);


        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        loadUsername(usernameTextView);


        // אתחול רכיבי ה-UI
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productList = new ArrayList<>();
        cartRef = FirebaseDatabase.getInstance().getReference("cart");

        // אתחול Adapter
        adapter = new ProductsAdapter(productList, new ProductsAdapter.OnProductInteractionListener() {
            @Override
            public void onQuantityIncrease(Product product) {
                updateProductQuantity(product, 1);
            }

            @Override
            public void onQuantityDecrease(Product product) {
                updateProductQuantity(product, -1);
            }

            @Override
            public void onDeleteProduct(Product product) {
                deleteProductFromCart(product);
            }
        });
        productsRecyclerView.setAdapter(adapter);

        // טעינת עגלת הקניות
        loadCart();

        // טיפול בכפתור הוספת מוצר
        Button addProductButton = view.findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(v -> openAddProductDialog());

        // טיפול בכפתור מחיקת מוצר
        Button removeProductButton = view.findViewById(R.id.removeProductButton);
        removeProductButton.setOnClickListener(v -> {
            if (!productList.isEmpty()) {
                deleteProductFromCart(productList.get(0)); // מוחק את המוצר הראשון ברשימה
            }
        });

        return view;
    }

    private void loadCart() {
        cartRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                productList.clear();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String name = snapshot.getKey();
                    int quantity = snapshot.child("quantity").getValue(Integer.class);
                    productList.add(new Product(name, quantity));
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Failed to load cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductQuantity(Product product, int delta) {
        DatabaseReference productRef = cartRef.child(product.getName());
        productRef.child("quantity").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Integer currentQuantity = task.getResult().getValue(Integer.class);

                if (currentQuantity != null) {
                    int newQuantity = currentQuantity + delta;

                    if (newQuantity > 0) {
                        // עדכון הכמות ב-Firebase וברשימה המקומית
                        productRef.child("quantity").setValue(newQuantity);
                        product.setQuantity(newQuantity);
                        adapter.notifyDataSetChanged();
                    } else {
                        // מחיקת המוצר אם הכמות יורדת ל-0
                        productRef.removeValue();
                        productList.remove(product);
                        adapter.notifyDataSetChanged();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsername(TextView usernameTextView) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // שליפת אימייל המשתמש
        if (email != null) {
            // המרת האימייל לפורמט תואם מפתח ב-Firebase
            String emailKey = email.replace(".", "_");
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(emailKey).child("username");

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String username = task.getResult().getValue(String.class);
                    usernameTextView.setText("Welcome, " + username);
                } else {
                    usernameTextView.setText("Welcome, Guest");
                }
            });
        } else {
            usernameTextView.setText("Welcome, Guest");
        }
    }


    private void openAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Product");

        // Layout לדיאלוג
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // שדה לשם המוצר
        EditText nameInput = new EditText(getContext());
        nameInput.setHint("Product Name");
        layout.addView(nameInput);

        // שדה לכמות המוצר
        EditText quantityInput = new EditText(getContext());
        quantityInput.setHint("Quantity");
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(quantityInput);

        builder.setView(layout);

        // כפתור הוספה
        builder.setPositiveButton("Add", (dialog, which) -> {
            String productName = nameInput.getText().toString().trim();
            String quantityStr = quantityInput.getText().toString().trim();

            if (!productName.isEmpty() && !quantityStr.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);
                addProductToCart(productName, quantity);
            } else {
                Toast.makeText(getContext(), "Please enter valid product details", Toast.LENGTH_SHORT).show();
            }
        });

        // כפתור ביטול
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    private void deleteProductFromCart(Product product) {
        cartRef.child(product.getName()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.remove(product);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addProductToCart(String productName, int quantity) {
        DatabaseReference productRef = cartRef.child(productName);
        productRef.child("quantity").setValue(quantity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.add(new Product(productName, quantity));
                adapter.notifyDataSetChanged();
            }
        });
    }
}
