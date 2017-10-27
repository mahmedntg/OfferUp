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

import com.example.company.offerup.utils.MyAlertDialog;
import com.example.company.offerup.utils.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signInBtn;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String alertTitle = getString(R.string.validation_title_alert);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        signInBtn = (Button) findViewById(R.id.loginBtn);
        emailEditText = (EditText) findViewById(R.id.emailLoginEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordLoginEditText);
        signInBtn.setOnClickListener(this);
        alertDialog = MyAlertDialog.createAlertDialog(this, alertTitle);
    }

    @Override
    public void onClick(View v) {
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
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
        progressDialog.setMessage("Login, Please wait ...........");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkUserExists();
                } else {
                    alertDialog.setMessage(task.getException().getMessage());
                    alertDialog.show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void checkUserExists() {
        final String userUid = firebaseAuth.getCurrentUser().getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userUid)) {
                    UserType userType = UserType.CUSTOMER;
                    for (DataSnapshot snapshot : dataSnapshot.child(userUid).getChildren()) {
                        if (snapshot.getKey().equals("userType")) {
                            userType = UserType.valueOf(snapshot.getValue(String.class));
                        }
                    }
                    Intent productIntent = null;
                    if (userType.equals(UserType.CUSTOMER)) {
                        productIntent = new Intent(LoginActivity.this, CustomerProductActivity.class);
                    }
                    if (userType.equals(UserType.PRODUCER)) {
                        productIntent = new Intent(LoginActivity.this, ProductActivity.class);
                    }
                    startActivity(productIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
