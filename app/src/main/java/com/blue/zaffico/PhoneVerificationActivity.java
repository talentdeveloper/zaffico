package com.blue.zaffico;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

public class PhoneVerificationActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    TextInputEditText editTextCarrierNumber;
    Button phoneNextBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = (TextInputEditText) findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        phoneNextBtn1 = findViewById(R.id.phoneNextBtn1);
        phoneNextBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ccp.isValidFullNumber()){
                    Toast.makeText(PhoneVerificationActivity.this,ccp.getFullNumber(),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PhoneVerificationActivity.this,PhoneVerificationConfirmActivity.class);
                    intent.putExtra("phone_number", ccp.getFullNumberWithPlus());
                    startActivity(intent);
                    finish();
                }else{
                    editTextCarrierNumber.setError("Invalid phone number.");
                }
            }
        });
    }
}
