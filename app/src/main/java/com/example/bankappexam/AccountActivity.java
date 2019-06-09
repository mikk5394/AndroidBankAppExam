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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnDeposit, btnWithdraw, btnGoBack, btnSend;
    private EditText ediTextAmount, editTextReciever;
    private TextView textViewAccountInfo, textViewSaldo;

    private FirebaseAuth firebaseAuth;

    private int accountBalance;
    private String accountType;
    private String uid, recipientUid;

    private static final String TAG = "LOG!!!!!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent i = getIntent();
        Account account = i.getParcelableExtra("account obj");
        accountType = account.getAccountType();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();

        //Firebasedatabase calls are asynchronous. I have to call the init in here and not after the db call, or else the db data won't get out in time, and I can't display the correct balance (the default balance, 0, will be shown then).
        FirebaseDatabase.getInstance().getReference("Accounts/" + accountType).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                accountBalance = Integer.parseInt(dataSnapshot.child("balance").getValue().toString());

                init(accountType);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void init(String accountType){

        btnDeposit = findViewById(R.id.btn_deposit);
        btnDeposit.setText("Deposit");
        btnDeposit.setOnClickListener(this);

        btnWithdraw = findViewById(R.id.btn_withdraw);
        btnWithdraw.setText("Withdraw");
        btnWithdraw.setOnClickListener(this);

        btnSend = findViewById(R.id.btn_send);
        btnSend.setText("Send Money");
        btnSend.setOnClickListener(this);

        btnGoBack = findViewById(R.id.btn_goBack);
        btnGoBack.setText("Go back");
        btnGoBack.setOnClickListener(this);

        ediTextAmount = findViewById(R.id.editText_amount);
        editTextReciever = findViewById(R.id.editText_receiver);

        textViewAccountInfo = findViewById(R.id.accountInfo);
        textViewAccountInfo.setText(accountType);

        textViewSaldo = findViewById(R.id.textView_saldo);
        textViewSaldo.setText("Balance: " + accountBalance);

    }

    public void goBack(){
        finish();
        startActivity(new Intent(this, MenuActivity.class));
    }

    public void deposit(){
        int amount = Integer.parseInt(ediTextAmount.getText().toString().trim());

        accountBalance = accountBalance + amount;

        FirebaseDatabase.getInstance().getReference("Accounts/" + accountType).child(uid).child("balance").setValue(accountBalance);
        Toast.makeText(this, "Money deposited", Toast.LENGTH_SHORT).show();
    }

    public void withdraw(){
        int amount = Integer.parseInt(ediTextAmount.getText().toString().trim());

        if(accountBalance - amount >= 0){
            accountBalance = accountBalance - amount;
            FirebaseDatabase.getInstance().getReference("Accounts/" + accountType).child(uid).child("balance").setValue(accountBalance);
            Toast.makeText(this, "Money Withdrawn", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You can't overdaw!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendTo(){

        final String receiverEmail = editTextReciever.getText().toString().trim();
        String amountHolder = ediTextAmount.getText().toString().trim();

        if(TextUtils.isEmpty(receiverEmail)){
            Toast.makeText(getApplicationContext(), "Enter an email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(amountHolder)){
            Toast.makeText(getApplicationContext(), "Enter an amount to send", Toast.LENGTH_LONG).show();
            return;
        }

        //If it's gotten this far, it theres data in the editText (the editText can only contain numbers)
        final int amount = Integer.parseInt(amountHolder);

        //Looping through every user in the DB and checking if the typed email belong to any of the users in the DB
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);
                    //If typed email matches a user in the db (emails are unique due to firebaseAuthentication), continue
                    if(user.getEmail().equals(receiverEmail)){

                        recipientUid = snapshot.getKey();

                        if(accountBalance - amount >= 0 && uid != snapshot.getKey()){
                            //I have to fetch our the balance of the user i'm sending money to so I can add the amount to his balance
                            FirebaseDatabase.getInstance().getReference("Accounts/" + accountType).child(snapshot.getKey()).child("balance").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int balanceOfReceiver = Integer.parseInt(dataSnapshot.getValue().toString());
                                    int updatedBalance = balanceOfReceiver + amount;
                                    //The senders balance is updated
                                    accountBalance = accountBalance - amount;
                                    FirebaseDatabase.getInstance().getReference("Accounts/" + accountType).child(uid).child("balance").setValue(accountBalance);
                                    //And the recipients balance is updated
                                    FirebaseDatabase.getInstance().getReference("Accounts/" + accountType).child(recipientUid).child("balance").setValue(updatedBalance);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;

                        } else {
                            Toast.makeText(getApplicationContext(), "You can't overdraw or send money to yourself", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No account with that email found", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view == btnGoBack){
            goBack();
        }
        if(view == btnDeposit){
            deposit();
        }
        if(view == btnWithdraw){
            withdraw();
        }
        if(view == btnSend){
            sendTo();
        }
    }
}

