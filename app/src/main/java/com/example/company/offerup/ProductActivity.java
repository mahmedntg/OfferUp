package com.example.company.offerup;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.example.company.offerup.utils.Product;

public class ProductActivity extends AppCompatActivity {
    private RecyclerView productList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button addProductBtn;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("product");
        productList = (RecyclerView) findViewById(R.id.productList);
        addProductBtn = (Button) findViewById(R.id.addProductBtn);
        productList.setHasFixedSize(true);
        productList.setLayoutManager(new LinearLayoutManager(this));
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent signInIntent = new Intent(ProductActivity.this, LoginActivity.class);
                    signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(signInIntent);
                }
            }
        };
    }

    public void productHomeClicked(View view) {
        Intent addProductIntent = new Intent(ProductActivity.this, AddProduct.class);
        startActivity(addProductIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        Query ref=databaseReference.orderByChild("userId").equalTo(firebaseAuth.getCurrentUser().getUid());
         adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class, R.layout.product_items, ProductViewHolder.class,ref
        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, Product model, final int position) {
                viewHolder.setName(model.getName());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setEndDate(model.getEndDate());
                viewHolder.setPlaceName(model.getPlaceName());
                viewHolder.deleteTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int myPosition=viewHolder.getAdapterPosition();
                        adapter.getRef(myPosition).removeValue();
                        adapter.notifyItemRemoved(myPosition);
                        adapter.notifyItemRangeChanged(myPosition,adapter.getItemCount());
                    }
                });
            }
        };
        productList.setAdapter(adapter);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder  {
        View mView;
        TextView deleteTextView;
        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            deleteTextView= (TextView) mView.findViewById(R.id.deleteProduct);
        }

        public void setName(String name) {
            TextView textView = (TextView) mView.findViewById(R.id.productName);
            textView.setText(name);
        }

        public void setPrice(String price) {
            TextView textView = (TextView) mView.findViewById(R.id.productPrice);
            textView.setText(price+" KWD");
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
        menu.findItem(R.id.checkOutItemMenu).setVisible(false);
        MenuBuilder menuBuilder=(MenuBuilder)menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOutItemMenu:
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
