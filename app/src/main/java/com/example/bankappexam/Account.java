package com.example.bankappexam;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
    private String accountHolder;
    private String accountType;
    private int balance;

    protected Account(Parcel in) {
        accountHolder = in.readString();
        accountType = in.readString();
        balance = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountHolder);
        dest.writeString(accountType);
        dest.writeInt(balance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public Account(String accountType) {
        this.accountType = accountType;
        this.balance = 0;
    }

    public Account(String accountHolder, String accountType){
        this.accountHolder = accountHolder;
        this.accountType = accountType;
        this.balance = 0;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
