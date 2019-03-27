package com.blue.zaffico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationConfirmActivity extends AppCompatActivity implements
        View.OnClickListener {

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    String phoneNumber;
    PinEntryEditText pinEntry;
    Button submitBtn;
    TextView resendCodeTextView;
    private static final String TAG = "PhoneAuthActivity";
//    public static boolean phone_verified = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification_confirm);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        submitBtn = findViewById(R.id.submitBtn);
        resendCodeTextView = findViewById(R.id.resendCodeTextView);
        submitBtn.setOnClickListener(this);
        resendCodeTextView.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry1);
        pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                submitBtn.setFocusableInTouchMode(true);
                submitBtn.requestFocus();
//                Toast.makeText(PhoneVerificationConfirmActivity.this,str+"::code enter success!!",Toast.LENGTH_LONG).show();
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    //mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(PhoneVerificationConfirmActivity.this,"code sent success!!",Toast.LENGTH_LONG).show();
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        phoneNumber = getIntent().getStringExtra("phone_number");
        startPhoneNumberVerification(phoneNumber);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(PhoneVerificationConfirmActivity.this,"Phone verification success!!",Toast.LENGTH_LONG).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("verification",true);
                            setResult(RESULT_OK,resultIntent);

                            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(PhoneVerificationConfirmActivity.this);
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putBoolean("phone", true);
                            editor.commit();
                            startActivity(new Intent(PhoneVerificationConfirmActivity.this, ChooseVerificationActivity.class));
                            finish();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                //mVerificationField.setError("Invalid code.");
                            }
                        }
                    }
                });
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.submitBtn:

                //String code = codeEt1.getText().toString();
                if (TextUtils.isEmpty(pinEntry.getText().toString())) {
                    pinEntry.setError("Cannot be empty.");
                    return;
                }

                String code = pinEntry.getText().toString();
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.resendCodeTextView:
                startPhoneNumberVerification(phoneNumber);
                break;
        }
    }

//    private boolean validatePhoneNumber() {
//        String phoneNumber = mPhoneNumberField.getText().toString();
//        if (TextUtils.isEmpty(phoneNumber)) {
//            mPhoneNumberField.setError("Invalid phone number.");
//            return false;
//        }
//        return true;
//    }
}
