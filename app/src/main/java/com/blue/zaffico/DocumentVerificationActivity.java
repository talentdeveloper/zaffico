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
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class DocumentVerificationActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView cameraImageView;
    ImageView docPassImageView;
    Button docNextBtn;
    TextInputEditText documentIDEt;
    TextView captureIdTextView;
    private static final int DOCUMENT_VERIFICATION = 112;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private boolean flag = false;
    private int currentState = 0;
//    public static boolean doc_verified = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_verification);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        captureIdTextView = findViewById(R.id.captureIdTextView);
        documentIDEt = findViewById(R.id.documentIDEt);
        docNextBtn = findViewById(R.id.docNextBtn);
        docPassImageView = findViewById(R.id.docPassImageView);

        cameraImageView = findViewById(R.id.cameraImageView);
        cameraImageView.setFocusableInTouchMode(true);
        cameraImageView.requestFocus();
        cameraImageView.setOnClickListener(this);
        docPassImageView.setOnClickListener(this);
        docNextBtn.setOnClickListener(this);
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify()
    {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                // ...
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    1);
        }
        else
        {

            takePhotoFromCamera();
        }
    }





    private void takePhotoFromCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("cameraId","0");
        startActivityForResult(intent,DOCUMENT_VERIFICATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == DOCUMENT_VERIFICATION) {
            if(data.getBooleanExtra("verification",false)){
                cameraImageView.setBackgroundResource(R.drawable.doc_pass);
                Toast.makeText(DocumentVerificationActivity.this, "Document Verification Success!!", Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(DocumentVerificationActivity.this, "Document Verification Failed!!", Toast.LENGTH_SHORT).show();


        }

        flag = true;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0)
            {
                for (int i=0; i<grantResults.length; ++i)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {

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
        if(v.getId() == R.id.docPassImageView || v.getId() == R.id.cameraImageView){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                checkVerify();
            }
            else
            {

                takePhotoFromCamera();
            }


            if (ContextCompat.checkSelfPermission(DocumentVerificationActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(DocumentVerificationActivity.this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
            }
        }else if(v.getId() == R.id.docNextBtn){
            if(!flag){
                Toast.makeText(DocumentVerificationActivity.this, "Please capture your id", Toast.LENGTH_SHORT).show();
                return;
            }else if(documentIDEt.getText().toString().trim().isEmpty()){
                Toast.makeText(DocumentVerificationActivity.this, "Please input your document id", Toast.LENGTH_SHORT).show();
                return;
            }else{
                if(currentState == 0){
                    captureIdTextView.setText(R.string.capture_id_back);
                    cameraImageView.setBackgroundResource(R.drawable.camera_icon);
                    flag = false;
                    currentState = 1;
                    Toast.makeText(DocumentVerificationActivity.this, "Success Front Side Document!!", Toast.LENGTH_LONG).show();
                }else{
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("verification",true);
                    setResult(RESULT_OK,resultIntent);
//                    doc_verified = true;
                    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean("document", true);
                    editor.commit();
                    finish();
                }

            }
        }



    }
}
