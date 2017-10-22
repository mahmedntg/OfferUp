package com.example.company.offerup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.company.offerup.utils.MyAlertDialog;
import com.example.company.offerup.utils.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button registerBtn;
    private Button signInBtn, homeBtn;
    private EditText emailEditText, placeNameEditText, phoneEditText, addressEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private UserType userType;
    private boolean customerType;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        String alertTitle = getString(R.string.validation_title_alert);
        alertDialog = MyAlertDialog.createAlertDialog(this, alertTitle);
        userType = (UserType) getIntent().getExtras().get("userType");
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        registerBtn = (Button) findViewById(R.id.registerBtn);
        signInBtn = (Button) findViewById(R.id.signInBtn);
        homeBtn = (Button) findViewById(R.id.homeBtn);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        placeNameEditText = (EditText) findViewById(R.id.placeNameEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        addressEditText = (EditText) findViewById(R.id.addressEditText);
        if (userType.equals(UserType.CUSTOMER)) {
            placeNameEditText.setVisibility(View.GONE);
            customerType = true;
        } else if (userType.equals(UserType.PRODUCER)) {
            phoneEditText.setVisibility(View.GONE);
            addressEditText.setVisibility(View.GONE);
        }
        registerBtn.setOnClickListener(this);
        signInBtn.setOnClickListener(this);
        homeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == registerBtn)
            registerUser();
        if (v == signInBtn) {
            Intent signInIntent = new Intent(this, LoginActivity.class);
            startActivity(signInIntent);
        }
        if (v == homeBtn) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void registerUser() {
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        final String phone = phoneEditText.getText().toString().trim();
        final String address = addressEditText.getText().toString().trim();
        final String restaurantName = placeNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            alertDialog.setMessage("email is required!");
            alertDialog.show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            alertDialog.setMessage("password is required!");
            alertDialog.show();
            return;
        }
        if (!customerType && TextUtils.isEmpty(restaurantName)) {
            alertDialog.setMessage("Restaurant name is required!");
            alertDialog.show();
            return;
        }
        progressDialog.setMessage("Registering User ...........");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userUid = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference ref = databaseReference.child(userUid);
                    ref.child("email").setValue(email);
                    if (!customerType) {
                        ref.child("placeName").setValue(restaurantName);
                    } else {
                        ref.child("phone").setValue(phone);
                        ref.child("address").setValue(address);
                    }
                    ref.child("userType").setValue(userType.toString());
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    alertDialog.setMessage(task.getException().getMessage());
                    alertDialog.show();
                }
                progressDialog.dismiss();

            }
        });
    }
}
