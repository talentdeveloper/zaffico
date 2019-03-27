package com.blue.zaffico;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AddressVerificationActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView cameraImageView;
    ImageView addressPassImageView;
    private static final int ADDRESS_VERIFICATION = 112;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    Button addressNextBtn;
    TextInputEditText addressEt;
    TextInputEditText cityEt;
    private boolean flag = false;
//    public static boolean add_verified = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_verification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        addressEt = findViewById(R.id.addressEt);
        cityEt = findViewById(R.id.cityEt);
        addressNextBtn = findViewById(R.id.addressNextBtn);
        addressNextBtn.setOnClickListener(this);


        addressPassImageView = findViewById(R.id.addressPassImageView);
        cameraImageView = findViewById(R.id.cameraImageView);
        cameraImageView.setFocusableInTouchMode(true);
        cameraImageView.requestFocus();
        cameraImageView.setOnClickListener(this);
        addressPassImageView.setOnClickListener(this);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // ...
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    1);
        } else {

            takePhotoFromCamera();
        }
    }


    private void takePhotoFromCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("cameraId","0");
        startActivityForResult(intent,ADDRESS_VERIFICATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == ADDRESS_VERIFICATION) {
            if(data.getBooleanExtra("verification",false)){
                cameraImageView.setBackgroundResource(R.drawable.doc_pass);
                Toast.makeText(AddressVerificationActivity.this, "Address Verification Success!!", Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(AddressVerificationActivity.this, "Address Verification Failed!!", Toast.LENGTH_SHORT).show();


        }

        flag = true;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                        new AlertDialog.Builder(this).setTitle("Notification").setMessage("You must grant permission to use the app.")
                                .setPositiveButton("close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                }).setNegativeButton("Permission Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();

                        return;
                    }
                }

                takePhotoFromCamera();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addressPassImageView || v.getId() == R.id.cameraImageView){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkVerify();
            } else {

                takePhotoFromCamera();
            }


            if (ContextCompat.checkSelfPermission(AddressVerificationActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddressVerificationActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }else if(v.getId() == R.id.addressNextBtn){
            if (!flag) {
                Toast.makeText(AddressVerificationActivity.this, "Please capture your id", Toast.LENGTH_SHORT).show();
                return;
            } else if (addressEt.getText().toString().trim().isEmpty()) {
                Toast.makeText(AddressVerificationActivity.this, "Please input your Address", Toast.LENGTH_SHORT).show();
                return;
            } else if (cityEt.getText().toString().trim().isEmpty()) {
                Toast.makeText(AddressVerificationActivity.this, "Please input your city", Toast.LENGTH_SHORT).show();
                return;
            } else {

                Intent resultIntent = new Intent();
                resultIntent.putExtra("verification", true);
                setResult(RESULT_OK, resultIntent);
//                add_verified = true;
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("address", true);
                editor.commit();
                finish();


            }
        }
    }
}
