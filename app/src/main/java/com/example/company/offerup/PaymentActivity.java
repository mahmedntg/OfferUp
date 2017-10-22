package com.example.company.offerup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.company.offerup.utils.CloserDialogTimerTask;
import com.example.company.offerup.utils.MyAlertDialog;
import com.example.company.offerup.utils.SharedPreferenceUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

public class PaymentActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        String alertTitle = getString(R.string.payment_success_alert);
        alertDialog = MyAlertDialog.createAlertDialog(this, alertTitle);
        progressDialog = new ProgressDialog(this);
    }

    public void processPaymentClicked(View view) {
        progressDialog.setMessage("Please wait, your transaction is processing");
        progressDialog.setTitle("Connecting");
        progressDialog.show();
        SharedPreferenceUtil.getInstance(PaymentActivity.this).clearSharedPreferences();
        Timer timer = new Timer();
        timer.schedule(new CloserDialogTimerTask(progressDialog), 5000);
        alertDialog.setMessage("Your payment has been succeeded");
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                alertDialog.show();
            }
        });
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
