package com.example.rbenterprise.Splash.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rb.enterprise.R;
import com.example.rbenterprise.Splash.Main.MainActivity;
import com.example.rbenterprise.Splash.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView donthaveAccount;
    private EditText email;
    private EditText password;
    private ImageButton closebtn;
    private Button signInBtn;
    private FirebaseAuth firebaseAuth;
    private String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private ProgressDialog pd;
    private TextView forgotpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        donthaveAccount = findViewById(R.id.registertxt);

        email = findViewById(R.id.signin_email);
        password = findViewById(R.id.signin_password);
        closebtn = findViewById(R.id.signin_closebtn);
        signInBtn = findViewById(R.id.signin_loginbutton);
        forgotpassword = findViewById(R.id.signin_forgotpassword);

        firebaseAuth = FirebaseAuth.getInstance();

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String>(forgotpassword,"emailTransition");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);

                startActivity(intent,options.toBundle());
            }
        });

        donthaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View,String>(donthaveAccount,"emailTransition");
                pairs[1] = new Pair<View,String>(donthaveAccount,"passwordTransition");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);

                startActivity(intent,options.toBundle());
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkEmailandPassword();

            }
        });

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainIntent();
            }
        });
    }
    private void checkEmailandPassword()
    {
        String femail = email.getText().toString();
        String fpassword = password.getText().toString();

        if (TextUtils.isEmpty(femail)) {
            email.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(fpassword)) {
            password.setError("Password is required");
            return;
        }

        if (femail.matches(emailpattern))
        {
            if (fpassword.length() >= 8)
            {
                pd = new ProgressDialog(LoginActivity.this);
                pd.setTitle("Please wait");
                pd.setMessage("Checking your credentials...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                firebaseAuth.signInWithEmailAndPassword(femail,fpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                        else
                        {
                            pd.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this,error,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(LoginActivity.this,"Incorrect email or password",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(LoginActivity.this,"Incorrect email or password",Toast.LENGTH_LONG).show();
        }
    }

    private void mainIntent()
    {
        Intent mainintent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainintent);
        finish();
    }
}