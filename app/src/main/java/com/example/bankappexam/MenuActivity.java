package com.example.bankappexam;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnDefault, btnBudget, btnPension, btnSavings, btnBusiness, btnLogout, btnGoBusiness;
    private TextView welcome;

    private Account defaultAcc = new Account("Default");
    private Account budget = new Account("Budget");
    private Account pension = new Account("Pension");
    private Account savings = new Account("Savings");
    private Account business = new Account("Business");

    private String name, uid;
    private int age;
    private boolean hasBusiness = false;

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "TEST BUSINESS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        welcome = findViewById(R.id.welcome);

        FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                age = Integer.parseInt(dataSnapshot.child("age").getValue().toString());
                welcome.setText(name + ", " + age);
                defaultAcc.setAccountHolder(name);
                budget.setAccountHolder(name);
                pension.setAccountHolder(name);
                savings.setAccountHolder(name);
                business.setAccountHolder(name);
                checkIfBusiness();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        init();

    }

    public void init() {

        btnDefault = findViewById(R.id.btn_default);
        btnDefault.setText(defaultAcc.getAccountType());
        btnDefault.setOnClickListener(this);

        btnBudget = findViewById(R.id.btn_budget);
        btnBudget.setText(budget.getAccountType());
        btnBudget.setOnClickListener(this);

        btnPension = findViewById(R.id.btn_pension);
        btnPension.setText(pension.getAccountType());
        btnPension.setOnClickListener(this);

        btnSavings = findViewById(R.id.btn_savings);
        btnSavings.setText(savings.getAccountType());
        btnSavings.setOnClickListener(this);

        btnBusiness = findViewById(R.id.btn_business);
        btnBusiness.setText(business.getAccountType());
        btnBusiness.setOnClickListener(this);

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);
        btnGoBusiness = findViewById(R.id.btn_goBus);
        btnGoBusiness.setOnClickListener(this);
    }

    public void logout() {
        Toast.makeText(this, getString(R.string.logout_message), Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void goBusiness() {

        //Checks if the user already has a business account or not - if not, an account is created for him
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("Accounts/Business/" + uid)){
                    Log.d(TAG, "HAR IKKE BUSINESS");
                    business.setAccountHolder(name);
                    FirebaseDatabase.getInstance().getReference("Accounts/Business").child(uid).setValue(business);
                    Toast.makeText(getApplicationContext(), "Business account created", Toast.LENGTH_LONG).show();
                    hasBusiness = true;
                } else {
                    Log.d(TAG, "HAR BUSINESS");
                    Toast.makeText(getApplicationContext(), "You already have a business account", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkIfBusiness() {

        FirebaseDatabase.getInstance().getReference("Accounts/Business/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    hasBusiness = true;
                } else {
                    hasBusiness = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick (View view){
        if (view == btnLogout) {
             logout();
        }
        if (view == btnGoBusiness) {
             goBusiness();
         }
         if (view == btnDefault){
             finish();
             Intent i = new Intent(this, AccountActivity.class);
             i.putExtra("account obj", defaultAcc);
             startActivity(i);
        }
         if (view == btnBudget){
             finish();

             //Passing the object over to the new activiy using parceable (see account class)
             Intent i = new Intent(this, AccountActivity.class);
             i.putExtra("account obj", budget);
             startActivity(i);
         }
        if (view == btnPension){
            finish();
            Intent i = new Intent(this, AccountActivity.class);
            i.putExtra("account obj", pension);
            startActivity(i);
        }
        if (view == btnSavings){
            finish();
            Intent i = new Intent(this, AccountActivity.class);
            i.putExtra("account obj", savings);
            startActivity(i);
        }
        if (view == btnBusiness){
            if(hasBusiness){
                finish();
                Intent i = new Intent(this, AccountActivity.class);
                i.putExtra("account obj", business);
                startActivity(i);
            } else {
                Toast.makeText(this, "You do not have a business account", Toast.LENGTH_LONG).show();
            }
        }
    }
}