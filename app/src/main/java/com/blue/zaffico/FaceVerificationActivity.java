package com.blue.zaffico;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class FaceVerificationActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView facePassImageView;
    ImageView cameraImageView;
    public Button faceNextBtn;
    private static final int FACE_VERIFICATION = 111;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private boolean flag = false;
//    public static boolean face_verified = false;

    public TextInputEditText birthdayEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_verification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        birthdayEt = findViewById(R.id.birthdayEt);
        birthdayEt.addTextChangedListener(new TextWatcher() {

            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2300) ? 2300 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    birthdayEt.setText(current);
                    birthdayEt.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        facePassImageView = findViewById(R.id.facePassImageView);
        faceNextBtn = findViewById(R.id.faceNextBtn);
        cameraImageView = findViewById(R.id.cameraImageView);
        cameraImageView.setFocusableInTouchMode(true);
        cameraImageView.requestFocus();
        faceNextBtn.setOnClickListener(this);
        cameraImageView.setOnClickListener(this);
        facePassImageView.setOnClickListener(this);

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
        intent.putExtra("cameraId", "1");
        startActivityForResult(intent, FACE_VERIFICATION);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == FACE_VERIFICATION) {
            if (data.getBooleanExtra("verification", false)) {
                facePassImageView.setBackgroundResource(R.drawable.face_pass);
                Toast.makeText(FaceVerificationActivity.this, "Face Verification Success!!", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(FaceVerificationActivity.this, "Face Verification Failed!!", Toast.LENGTH_SHORT).show();


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
        if(v.getId() == R.id.facePassImageView || v.getId() == R.id.cameraImageView){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkVerify();
            } else {

                takePhotoFromCamera();
            }


            if (ContextCompat.checkSelfPermission(FaceVerificationActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(FaceVerificationActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

        }else if(v.getId() == R.id.faceNextBtn){
            if (flag) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("verification", true);
                setResult(RESULT_OK, resultIntent);
//                face_verified = true;
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("face", true);
                editor.commit();
                finish();
            }
        }
    }



}
