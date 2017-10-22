package com.example.company.offerup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.company.offerup.utils.MyAlertDialog;
import com.example.company.offerup.utils.SharedPreferenceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

public class AddCustomerProductActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextView singleProductName, singleProductPrice, singleProductTotal;
    private ImageView singleProductImage;
    private EditText singleProductQuantity;
    private Button addSProductBtn, cancelSProductBtn;
    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String productKey = "product";
    private final String PRODUCT_SEP = ",";
    private String productId;
    private String name;
    private String price;
    private double total = 0.0;
    private FirebaseAuth firebaseAuth;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_product);
        String alertTitle = getString(R.string.validation_title_alert);
        alertDialog = MyAlertDialog.createAlertDialog(this, alertTitle);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("product");
        singleProductImage = (ImageView) findViewById(R.id.singleImageView);
        singleProductName = (TextView) findViewById(R.id.singleProductName);
        singleProductPrice = (TextView) findViewById(R.id.singleProductPrice);
        singleProductTotal = (TextView) findViewById(R.id.singleProductTotal);
        singleProductQuantity = (EditText) findViewById(R.id.singleProductQuantity);
        addSProductBtn = (Button) findViewById(R.id.addSProductBtn);
        cancelSProductBtn = (Button) findViewById(R.id.cancelSProductBtn);
        productId = getIntent().getExtras().getString("productId");
        databaseReference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);
                price = dataSnapshot.child("price").getValue(String.class);
                singleProductName.setText("Name: " + name);
                Picasso.with(AddCustomerProductActivity.this).load(image).into(singleProductImage);
                singleProductPrice.setText("Price: " + price + " KWD");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        singleProductQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    singleProductTotal.setText("");
                } else {
                    Integer quantity = Integer.parseInt(s.toString());
                    total = Double.parseDouble(price) * quantity;
                    singleProductTotal.setText("Total: " + total + " KWD");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void addSProductClicked(View view) {
        String quantity = singleProductQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantity)) {
            alertDialog.setMessage("Quantity is required!");
            alertDialog.show();
            return;
        }
        progressDialog.setMessage("Adding Product ...........");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        saveProduct(name, quantity, total);
        progressDialog.dismiss();
        Intent intent = new Intent(AddCustomerProductActivity.this, CustomerProductActivity.class);
        startActivity(intent);
    }

    public void cancelSProductClicked(View view) {
        Intent intent = new Intent(AddCustomerProductActivity.this, CustomerProductActivity.class);
        startActivity(intent);
    }

    private void saveProduct(String name, String quantity, double total) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = SharedPreferenceUtil.getInstance(AddCustomerProductActivity.this).getSharedPreferences();
        Set<String> productSet = new HashSet<>();
        if (settings.contains(productKey)) {
            productSet = settings.getStringSet(productKey, new HashSet<String>());
        }
        StringBuffer product = new StringBuffer();
        product.append(productId + PRODUCT_SEP + name + PRODUCT_SEP + quantity + PRODUCT_SEP + price);
        for (String p : productSet) {
            if (p.split(PRODUCT_SEP)[0].equals(productId)) {
                productSet.remove(p);
            }
        }
        productSet.add(product.toString());
        editor = settings.edit();
        editor.putStringSet(productKey, productSet);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        if(!SharedPreferenceUtil.getInstance(AddCustomerProductActivity.this).isSharedPreferencesExists()){
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
                SharedPreferenceUtil.getInstance(AddCustomerProductActivity.this).clearSharedPreferences();
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
