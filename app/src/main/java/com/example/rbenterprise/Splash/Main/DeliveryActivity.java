package com.example.rbenterprise.Splash.Main;

import static com.example.rbenterprise.Splash.Main.DBQueries.firebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.rb.enterprise.R;
import com.example.rbenterprise.Splash.Main.Fragments.Adapter.CartAdapter;
import com.example.rbenterprise.Splash.Main.Fragments.Model.CartItemModel;
import com.example.rbenterprise.Splash.Main.Fragments.MyCartFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView deliveryRecyclerView;
    private Button changeOrAddNewAddressBtn;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount;

    private TextView fullname;
    private String name,mobileNo,address,pcode;
    private TextView fullAddress;
    private TextView pincode;

    private Button continueBtn;
    private Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private ImageView cod;

    public static List<CartItemModel> cartItemModelList;

    //
    private String order_id;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        order_id = UUID.randomUUID().toString().substring(0, 28);

        changeOrAddNewAddressBtn = findViewById(R.id.change_or_add_address_btn);
        totalAmount = findViewById(R.id.total_cart_amount);

        fullname = findViewById(R.id.fullname);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        deliveryRecyclerView = findViewById(R.id.delivery_recycler_view);
        continueBtn = findViewById(R.id.cart_continue_btn);

        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        CartAdapter cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddNewAddressBtn.setVisibility(View.VISIBLE);
        changeOrAddNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethodDialog.show();
            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                placeOrderDetails();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        name = DBQueries.addressesModelList.get(DBQueries.selectedAddress).getFullname();
        mobileNo = DBQueries.addressesModelList.get(DBQueries.selectedAddress).getMobileNo();
        address = DBQueries.addressesModelList.get(DBQueries.selectedAddress).getAddress();
        pcode = DBQueries.addressesModelList.get(DBQueries.selectedAddress).getPincode();
        fullname.setText(name + " - " +mobileNo);
        fullAddress.setText(address);
        pincode.setText(pcode);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void placeOrderDetails()
    {
        String userId = FirebaseAuth.getInstance().getUid();
        loadingDialog.show();

        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM)
            {
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                Map<String, Object> orderDetails=new HashMap<>();
                orderDetails.put("ORDER ID",order_id);
                orderDetails.put("Product Id",cartItemModel.getProductID());
                orderDetails.put("Product Image",cartItemModel.getProductImage());
                orderDetails.put("Product Name",cartItemModel.getProductTitle());
                orderDetails.put("User Id",userId);
                orderDetails.put("Product quantity",cartItemModel.getProductQuantity());
                if(cartItemModel.getCuttedPrice() != null)
                {
                    orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                }else
                {
                    orderDetails.put("Cutted Price","");
                }
                orderDetails.put("Product Price",cartItemModel.getProductPrice());
                orderDetails.put("Ordered Date", date);
                orderDetails.put("Order Status","Ordered");
                orderDetails.put("Address",fullAddress.getText().toString());
                orderDetails.put("FullName",fullname.getText().toString());
                orderDetails.put("Pincode",pincode.getText().toString());
                orderDetails.put("Delivery Price","100");

                firebaseFirestore.collection("ORDERS").document(order_id).collection("ORDER_ITEMS").document(cartItemModel.getProductID())
                        .set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Intent intent = new Intent(DeliveryActivity.this,OrderConfirmationActivity.class);
                                    intent.putExtra("order_id",order_id);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    String error=task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }
}
