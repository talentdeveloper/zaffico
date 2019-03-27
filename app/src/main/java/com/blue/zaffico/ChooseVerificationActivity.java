package com.blue.zaffico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ChooseVerificationActivity extends AppCompatActivity {
    LinearLayout selectCountyLL;
    LinearLayout selectCountyLL1;
    LinearLayout faceLL;
    LinearLayout docLL;
    LinearLayout addressLL;
    LinearLayout phoneLL;
    Button chooseVerificationNextBtn;
    ImageView facePassImageView2;
    ImageView docPassImageView;
    ImageView addressPassImageView;
    ImageView phonePassImageView;
    public int FACE = 1000;
    public int DOCUMENT = 2000;
    public int ADDRESS = 3000;
    public int PHONE = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_verification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        chooseVerificationNextBtn = findViewById(R.id.chooseVerificationNextBtn);
        chooseVerificationNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseVerificationActivity.this,SignatureActivity.class));
            }
        });
        facePassImageView2 = findViewById(R.id.facePassImageView2);
        docPassImageView = findViewById(R.id.docPassImageView);
        addressPassImageView = findViewById(R.id.addressPassImageView);
        phonePassImageView = findViewById(R.id.phonePassImageView);
        faceLL = findViewById(R.id.faceLL);
        faceLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent face_intent = new Intent(ChooseVerificationActivity.this,FaceVerificationActivity.class);
                //face_intent.putExtra("face_verification",false);
                startActivityForResult(face_intent,FACE);
            }
        });
        docLL = findViewById(R.id.docLL);
        docLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent face_intent = new Intent(ChooseVerificationActivity.this,DocumentVerificationActivity.class);
                //face_intent.putExtra("face_verification",false);
                startActivityForResult(face_intent,DOCUMENT);
            }
        });
        addressLL = findViewById(R.id.addressLL);
        addressLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent face_intent = new Intent(ChooseVerificationActivity.this,AddressVerificationActivity.class);
                //face_intent.putExtra("face_verification",false);
                startActivityForResult(face_intent,ADDRESS);
            }
        });
        phoneLL = findViewById(R.id.phoneLL);
        phoneLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent face_intent = new Intent(ChooseVerificationActivity.this,PhoneVerificationActivity.class);
                //face_intent.putExtra("face_verification",false);
                startActivity(face_intent);
            }
        });
        setVerificationDot(facePassImageView2,"face");
        setVerificationDot(docPassImageView,"document");
        setVerificationDot(addressPassImageView,"address");
        setVerificationDot(phonePassImageView,"phone");

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){

                case 1000:
                    if(data.getBooleanExtra("verification",false))
                        facePassImageView2.setBackgroundResource(R.drawable.pass);
                    break;
                case 2000:
                    if(data.getBooleanExtra("verification",false))
                    docPassImageView.setBackgroundResource(R.drawable.pass);
                    break;
                case 3000:
                    if(data.getBooleanExtra("verification",false))
                    addressPassImageView.setBackgroundResource(R.drawable.pass);
                    break;
            }
        }
    }

    private void setVerificationDot(ImageView img, String key){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPrefs.edit();
        Boolean state = mPrefs.getBoolean(key, false);

        if (state) {
            img.setBackgroundResource(R.drawable.pass);
        } else {
            return;
        }
    }


}
