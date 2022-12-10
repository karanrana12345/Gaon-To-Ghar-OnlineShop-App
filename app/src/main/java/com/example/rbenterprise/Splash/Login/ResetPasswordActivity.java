package com.example.rbenterprise.Splash.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rb.enterprise.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText registeredemail;
    private Button resetpasswordBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        registeredemail = findViewById(R.id.forgot_email);
        resetpasswordBtn = findViewById(R.id.reset_password_btn);
        firebaseAuth = FirebaseAuth.getInstance();

        resetpasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(registeredemail.getText().toString())) {
                    registeredemail.setError("Email is required");
                    return;
                }

                pd = new ProgressDialog(ResetPasswordActivity.this);
                pd.setTitle("Please wait");
                pd.setMessage("Checking your email...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                firebaseAuth.sendPasswordResetEmail(registeredemail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ResetPasswordActivity.this,"Email Sent Successfully",Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }
                        else
                        {
                            pd.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ResetPasswordActivity.this,error,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}