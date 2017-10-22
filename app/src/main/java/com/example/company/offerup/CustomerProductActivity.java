package com.example.company.offerup;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.company.offerup.utils.Product;
import com.example.company.offerup.utils.SharedPreferenceUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomerProductActivity extends AppCompatActivity {
    private RecyclerView productList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Spinner productSipnner;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("product");
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("category");
        productList = (RecyclerView) findViewById(R.id.productCustomerList);
        productList.setHasFixedSize(true);
        productList.setLayoutManager(new LinearLayoutManager(this));
        productSipnner = (Spinner) findViewById(R.id.productCategory);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSipnner.setAdapter(dataAdapter);
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    list.add(postSnapshot.child("name").getValue(String.class));
                }
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        productSipnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoryName = parent.getItemAtPosition(position).toString();
                Query ref = databaseReference.orderByChild("categoryName").equalTo(categoryName);
                FirebaseRecyclerAdapter<Product, ProductActivity.ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Product, ProductActivity.ProductViewHolder>(
                        Product.class, R.layout.product_items, ProductActivity.ProductViewHolder.class, ref
                ) {
                    @Override
                    protected void populateViewHolder(ProductActivity.ProductViewHolder viewHolder, Product model, int position) {
                        viewHolder.setName(model.getName());
                        viewHolder.setImage(getApplicationContext(), model.getImage());
                        viewHolder.setPrice(model.getPrice());
                        viewHolder.setEndDate(model.getEndDate());
                        viewHolder.setPlaceName(model.getPlaceName());
                        final String productId = getRef(position).getKey().toString();
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent singlProductIntent = new Intent(CustomerProductActivity.this, AddCustomerProductActivity.class);
                                singlProductIntent.putExtra("productId", productId);
                                startActivity(singlProductIntent);
                            }
                        });
                    }
                };
                productList.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView textView = (TextView) mView.findViewById(R.id.productName);
            textView.setText(name);
        }

        public void setPrice(String price) {
            TextView textView = (TextView) mView.findViewById(R.id.productPrice);
            textView.setText(price + " KWD");
        }


        public void setImage(Context ctx, String image) {
            ImageView imageView = (ImageView) mView.findViewById(R.id.productImage);
            Picasso.with(ctx).load(image).into(imageView);
        }

        public void setEndDate(String endDate) {
            TextView nameTV = (TextView) mView.findViewById(R.id.productEndDate);
            nameTV.setText(endDate);
        }

        public void setPlaceName(String placeName) {
            TextView nameTV = (TextView) mView.findViewById(R.id.productPlaceName);
            nameTV.setText(placeName);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        if(!SharedPreferenceUtil.getInstance(CustomerProductActivity.this).isSharedPreferencesExists()){
            menu.findItem(R.id.checkOutItemMenu).setVisible(false);
        }
        MenuBuilder menuBuilder=(MenuBuilder)menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOutItemMenu:
                firebaseAuth.signOut();
                SharedPreferenceUtil.getInstance(CustomerProductActivity.this).clearSharedPreferences();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.checkOutItemMenu:
                startActivity(new Intent(this, ProductCartActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
