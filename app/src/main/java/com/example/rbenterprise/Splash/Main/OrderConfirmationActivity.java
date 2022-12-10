package com.example.rbenterprise.Splash.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rb.enterprise.R;

public class OrderConfirmationActivity extends AppCompatActivity {

    private TextView orderID;
    private TextView continueShoppingTV;
    private ImageView continueShoppingIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        orderID = findViewById(R.id.order_id);
        continueShoppingIV = findViewById(R.id.continue_shopping_IV);
        continueShoppingTV = findViewById(R.id.continue_shopping_TV);

        String orderid = getIntent().getStringExtra("order_id");

        orderID.setText("Your order id is: "+orderid);

        continueShoppingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderConfirmationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        continueShoppingIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderConfirmationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}