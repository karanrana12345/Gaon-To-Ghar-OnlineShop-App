package com.example.rbenterprise.Splash.Main.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rbenterprise.Splash.Main.DBQueries;
import com.example.rbenterprise.Splash.Main.UpdateUserInfoActivity;
import com.example.rbenterprise.Splash.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rb.enterprise.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountFragment extends Fragment {

    public MyAccountFragment() {
        // Required empty public constructor
    }

    public static final int MANAGE_ADDRESS = 1;

    private CircleImageView profileView;
    private TextView name;
    private TextView email;
    private TextView phonenumber;
    private TextView userid;
    private Button signoutBtn;
    private Button settingsBtn;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_account, container, false);

        profileView = view.findViewById(R.id.userimage);
        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        signoutBtn = view.findViewById(R.id.sign_out_btn);
        settingsBtn = view.findViewById(R.id.setting_btn);
        phonenumber = view.findViewById(R.id.user_phonenumber);
        userid = view.findViewById(R.id.user_id);

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                DBQueries.clearData();
                Intent registerIntent = new Intent(getContext(), RegisterActivity.class);
                startActivity(registerIntent);
                getActivity().finish();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent updateUserInfo = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfo.putExtra("Name",name.getText());
                updateUserInfo.putExtra("Email",email.getText());
                updateUserInfo.putExtra("Profile",DBQueries.profile);
                startActivity(updateUserInfo);

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String mname = task.getResult().getString("Fullname");
                    String memail = task.getResult().getString("Email");
                    String mphonenumber = task.getResult().getString("Phonenumber");
                    String muserid = currentUser.getUid();
                    String mprofile = task.getResult().getString("Profile");

                    name.setText(mname);
                    email.setText(memail);
                    phonenumber.setText(mphonenumber);
                    userid.setText(muserid);
                    Glide.with(getContext()).load(mprofile).into(profileView);

                }
                else
                {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}