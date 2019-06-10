package com.example.bankappexam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

        private EditText editTextEmail, editTextPassword, editTextName, editTextAge;
        private Button btnSignup;

        private FirebaseAuth firebaseAuth;
        private static final String TAG = "TEST";

    @Override
        protected void  onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);

            firebaseAuth = FirebaseAuth.getInstance();

            btnSignup = findViewById(R.id.btn_signUp2);
            editTextEmail = findViewById(R.id.editText_emailSignup);
            editTextPassword = findViewById(R.id.editText_passwordSignup);
            editTextName = findViewById(R.id.editText_nameSignup);
            editTextAge = findViewById(R.id.editText_ageSignup);

            btnSignup.setOnClickListener(this);

        }

        public void signUp(){
            final String email = editTextEmail.getText().toString().trim();
            final String password = editTextPassword.getText().toString().trim();
            final String name = editTextName.getText().toString().trim();
            final String age = editTextAge.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(password)){
                Toast.makeText(this,"Please enter a password", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(name)){
                Toast.makeText(this,"Please enter your name", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(age)){
                Toast.makeText(this,"Please enter your age", Toast.LENGTH_LONG).show();
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Register completed successfully", Toast.LENGTH_SHORT).show();

                                //Saving the user to the DB with his unique auth Uid as key - also putting his uid as key in the account nodes so I can attach specific users to specific accounts
                                FirebaseUser fbUser = firebaseAuth.getCurrentUser();
                                User user = new User(email, password, name, Integer.parseInt(age));
                                FirebaseDatabase.getInstance().getReference("users").child(fbUser.getUid()).setValue(user);

                                //A new customer gets a budget and a default account at creation (with 0 balance)
                                Account defaultAcc = new Account(name, "Default");
                                Account budget = new Account(name, "Budget");
                                Account savings = new Account(name, "Savings");
                                Account pension = new Account(name, "Pension");
                                //Saving the newly created users uid in the specific account nodes
                                FirebaseDatabase.getInstance().getReference("Accounts/Default").child(fbUser.getUid()).setValue(defaultAcc);
                                FirebaseDatabase.getInstance().getReference("Accounts/Budget").child(fbUser.getUid()).setValue(budget);
                                FirebaseDatabase.getInstance().getReference("Accounts/Savings").child(fbUser.getUid()).setValue(savings);
                                FirebaseDatabase.getInstance().getReference("Accounts/Pension").child(fbUser.getUid()).setValue(pension);

                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                            }else{
                                Toast.makeText(getApplicationContext(), "Error, please try again", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        }

        @Override
        public void onClick(View view) {
            if (view == btnSignup){
                signUp();
            }
        }
}
