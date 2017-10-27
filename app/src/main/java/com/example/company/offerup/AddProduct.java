package com.example.company.offerup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.company.offerup.utils.MyAlertDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddProduct extends AppCompatActivity {
    private Button addBtn;
    private ImageButton imageBtn;
    private EditText priceEditText, descriptionEditText, dateEditText, nameEditText;
    private Uri uri = null;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth firebaseAuth;
    String placeName;
    private Spinner productSipnner;
    List<String> list = new ArrayList<>();
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        String alertTitle = getString(R.string.validation_title_alert);
        alertDialog = MyAlertDialog.createAlertDialog(this, alertTitle);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("product");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("category");
        priceEditText = (EditText) findViewById(R.id.productPriceEditText);
        descriptionEditText = (EditText) findViewById(R.id.productDescriptionEditText);
        dateEditText = (EditText) findViewById(R.id.productDateEditText);
        nameEditText = (EditText) findViewById(R.id.productNameEditText);
        addBtn = (Button) findViewById(R.id.addBtn);
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

    }


    public void addProductClicked(View view) {
        final String description = descriptionEditText.getText().toString().trim();
        final String price = priceEditText.getText().toString().trim();
        final String endDate = dateEditText.getText().toString().trim();
        final String name = nameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            alertDialog.setMessage("Name is required!");
            alertDialog.show();
            return;
        }
        if (TextUtils.isEmpty(price)) {
            alertDialog.setMessage("Price is required!");
            alertDialog.show();
            return;
        }
        if (TextUtils.isEmpty(endDate)) {
            alertDialog.setMessage("End Date is required!");
            alertDialog.show();
            return;
        }
        progressDialog.setMessage("Adding Product ...........");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        StorageReference filePath = storageReference.child(uri.getLastPathSegment());
        final String userUid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference userRef = userDatabaseReference.child(userUid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("placeName")) {
                        final String pName = snapshot.getValue().toString();
                        placeName = pName;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                final DatabaseReference ref = databaseReference.push();
                ref.child("userId").setValue(userUid);
                ref.child("description").setValue(description);
                ref.child("price").setValue(price);
                ref.child("endDate").setValue(endDate);
                ref.child("name").setValue(name);
                ref.child("image").setValue(downloadUrl.toString());
                ref.child("placeName").setValue(placeName);
                ref.child("categoryName").setValue(productSipnner.getSelectedItem().toString());
                progressDialog.dismiss();
                startActivity(new Intent(AddProduct.this, ProductActivity.class));
            }
        });
    }

    public void imageBtnClicked(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            uri = data.getData();
            imageBtn = (ImageButton) findViewById(R.id.productImageBtn);
            imageBtn.setImageURI(uri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        menu.findItem(R.id.checkOutItemMenu).setVisible(false);
        MenuBuilder menuBuilder = (MenuBuilder) menu;
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
