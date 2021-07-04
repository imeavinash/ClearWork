package com.avinash.clearwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    String tag = "LoginActivity";



    private EditText  mCode, mPhoneNumber2, mName;
    private LinearLayout mPhoneNumberLayout, mVerifyLayout, mNameLayout;
    private TextView mCountryCode;

    private Button mGetOtp, mVerify, mAllSet;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public ProgressBar mProgressBar;
    public LinearLayout mProgressBarLayout;



    String mVerificationId;

    Matcher m;

    //String pattern = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$";

    String pattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        userIsLoggedIn();

        mPhoneNumberLayout = findViewById(R.id.phoneNumberLayout);
        mVerifyLayout = findViewById(R.id.verifyLayout);
        mNameLayout = findViewById(R.id.nameLayout);
        mProgressBarLayout = findViewById(R.id.llProgressBar);

        //mPhoneNumber = findViewById(R.id.phoneNumber);
        mPhoneNumber2 = findViewById(R.id.phoneNumber);
        mCode = findViewById(R.id.code);
        mName = findViewById(R.id.name);

        mCountryCode = findViewById(R.id.countryCode);
       // mSend = findViewById(R.id.send);
        mVerify = findViewById(R.id.verify);
        mGetOtp = findViewById(R.id.getOTP);
        mAllSet = findViewById(R.id.allSet);

        //mProgressBar = findViewById(R.id.llProgressBar);

        mAllSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mName.getText().toString().isEmpty()){
                    return;
                }
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){

                    final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                    Map<String, Object> userMap = new HashMap<>();
                    String name = mName.getText().toString();
                    userMap.put("phone",user.getPhoneNumber());
                    userMap.put("name",name);
                    mUserDB.updateChildren(userMap);
                    userIsLoggedIn();
                }
            }
        });
        mGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mPhoneNumber2.getText().toString();
                String countryCode = mCountryCode.getText().toString();
                phoneNumber = countryCode + phoneNumber;
                if(phoneNumber.isEmpty()){
                    Log.d(tag,"Phone number is empty");
                    Toast.makeText(getApplicationContext(),"Please enter number",Toast.LENGTH_SHORT).show();
                }else{

                    Pattern r = Pattern.compile(pattern);
                    m = r.matcher(phoneNumber.trim());
                    if(m.find()){
                        Log.d(tag,"Phone Number matches pattern - "+phoneNumber);
                        //String countryCode = mCountryCode.getText().toString();
                        startPhoneNumberVerification(phoneNumber);
                        mProgressBarLayout.setVisibility(View.VISIBLE);
                    }else{
                        Log.d(tag,"Phone Number does not match pattern - "+phoneNumber);
                        Toast.makeText(LoginActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVerificationId != null ){

                    Log.d(tag,"verification id is not null");
                    mProgressBarLayout.setVisibility(View.VISIBLE);
                    verifyPhoneNumberWithCode();
                }else{
                    Log.d(tag,"verification id is null");
                    mPhoneNumberLayout.setVisibility(View.VISIBLE);
                    mVerifyLayout.setVisibility(View.GONE);
                   // startPhoneNumberVerification(mPhoneNumber.getText().toString());
                }
            }
        });
//        mSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.d(tag,"on click mSend");
//
//                if(mVerificationId != null ){
//
//                    Log.d(tag,"verification id is not null");
//                    verifyPhoneNumberWithCode();
//                }else{
//                    Log.d(tag,"verification id is null");
//
//                    startPhoneNumberVerification(mPhoneNumber.getText().toString());
//                }
//
//            }
//        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Log.d("LoginActivity","onVerificationCompleted");
                mProgressBarLayout.setVisibility(View.GONE);
                String code = phoneAuthCredential.getSmsCode().toString();
                mCode.setText(code);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Log.d(tag,"onVerificationFailed");
                Toast.makeText(LoginActivity.this, "Verification failed - Please enter valid number", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken){

                super.onCodeSent(verificationId,forceResendingToken);

                Log.d(tag,"onCodeSent");
                mVerificationId = verificationId;
                mProgressBarLayout.setVisibility(View.GONE);
                mPhoneNumberLayout.setVisibility(View.GONE);
                mVerifyLayout.setVisibility(View.VISIBLE);
                //mSend.setText("Verify Code");




            }


        };


    }

    private void verifyPhoneNumberWithCode(){

        Log.d("LoginActivity","verifyPhoneNumberWithCode");

        String code = mCode.getText().toString();
        if(code != null && !code.isEmpty() && mVerificationId!=null){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,mCode.getText().toString());
            signInWithPhoneAuthCredential(credential);
        }else{
            Log.d(tag,"code is null or verification id is null");
        }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {

        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d(tag,"signinWithCredential onComplete");
                if(task.isSuccessful()){
                    Log.d(tag,"task is successful");

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null){
                        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(!snapshot.exists()){

                                    mProgressBarLayout.setVisibility(View.GONE);

                                    mVerifyLayout.setVisibility(View.GONE);
                                    mNameLayout.setVisibility(View.VISIBLE);









                                }else{
                                    mProgressBarLayout.setVisibility(View.GONE);
                                    userIsLoggedIn();
                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }

                //userIsLoggedIn();

            }
        });
    }

    private void userIsLoggedIn() {

        Log.d(tag,"userisLoggedIn");
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            Log.d("LoginActivity","user is not null");

            String userID = user.getUid();

            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("userID",userID);
            myEdit.apply();
            myEdit.commit();
            startActivity(new Intent(getApplicationContext(),MainPageActivity.class));
            finish();
            return;
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
