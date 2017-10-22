package com.example.company.offerup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.company.offerup.utils.CartProductAdapter;
import com.example.company.offerup.utils.DividerItemDecoration;
import com.example.company.offerup.utils.Product;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductCartActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String productKey = "product";
    private final String PRODUCT_SEP = ",";
    private TextView carTotalTextView;
    private List<Product> productList = new ArrayList<Product>();
    private RecyclerView recyclerView;
    private CartProductAdapter mAdapter;
    private double total = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart);
        carTotalTextView = (TextView) findViewById(R.id.cartProductTotal);
        recyclerView = (RecyclerView) findViewById(R.id.cartProductsList);
        mAdapter = new CartProductAdapter(productList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = getApplicationContext().getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        Set<String> productSet = new HashSet<>();
        if (settings.contains(productKey)) {
            productSet = settings.getStringSet(productKey, new HashSet<String>());
        }
        for (String p : productSet) {
            String[] pArray = p.split(PRODUCT_SEP);
            Product product = new Product();
            product.setName(pArray[1]);
            product.setQuantity(pArray[2]);
            product.setPrice(pArray[3]);
            productList.add(product);
            total += Integer.parseInt(product.getQuantity()) * Double.parseDouble(product.getPrice());
        }
        mAdapter.notifyDataSetChanged();
        carTotalTextView.setText("Total: " + total + " KWD");
    }

    public void payBtnClicked(View view) {
        startActivity(new Intent(this, PaymentActivity.class));
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
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
