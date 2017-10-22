package com.example.company.offerup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.company.offerup.utils.UserType;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button customerBtn;
    private Button producerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customerBtn = (Button) findViewById(R.id.customerBtn);
        producerBtn = (Button) findViewById(R.id.producerBtn);
        customerBtn.setOnClickListener(this);
        producerBtn.setOnClickListener(this);
    }

    private void customerBtnClicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("userType", UserType.CUSTOMER);
        startActivity(intent);
    }

    private void producerBtnClicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("userType",UserType.PRODUCER);
        startActivity(intent);
         }

    @Override
    public void onClick(View view) {
        if (view == customerBtn) {
            customerBtnClicked();
        }
        if (view == producerBtn) {
            producerBtnClicked();
        }
    }
}
