package com.example.loggerapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_FILENAME = "com.example.loggerapp.LOG_FILENAME";
    private static final int REQUEST_PICK_PHOTO = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 200;
    private static final int PERMISSION_REQUEST_CAMERA = 300;
    private boolean isOpen = false;
    private boolean isCameraAllowed = false;
    private AppCompatImageView imageView;
    private Uri photoURI;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        writeToFile("onCreate()");
        setupFABMenu();
        imageView = findViewById(R.id.image_view_activity_main_show_image);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            isCameraAllowed = true;
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            showRationale();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isCameraAllowed = true;
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showRationale();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_PICK_PHOTO:
                    Glide.with(MainActivity.this)
                            .load(data.getData())
                            .into(imageView);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    Log.i("PHOTO_URI:", photoURI.toString());
                    Glide.with(MainActivity.this)
                            .load(photoURI)
                            .into(imageView);
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date()) + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (intentTakePicture.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(MainActivity.this,
                        getString(R.string.provider_authority),
                        photoFile);
                intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.i("PHOTO_URI:", photoURI.toString());
                startActivityForResult(intentTakePicture, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        writeToFile("onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        writeToFile("onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeToFile("onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeToFile("onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        writeToFile("onDestroy()");
    }

    private void writeToFile(String body) {
        try (FileOutputStream fos = openFileOutput(LOG_FILENAME, Context.MODE_PRIVATE | Context.MODE_APPEND)) {
            fos.write(('\n' + body).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFABMenu() {
        FloatingActionButton fabMenu = findViewById(R.id.fab_activity_main_menu);
        FloatingActionButton fabShare = findViewById(R.id.fab_activity_main_share_file);
        FloatingActionButton fabSelectPicture = findViewById(R.id.fab_activity_main_pick_picture);
        FloatingActionButton fabCamera = findViewById(R.id.fab_activity_main_camera);
        Animation animationFABOpen = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_open);
        Animation animationFABClose = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close);
        Animation animationFABClock = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_rotate_clock);
        Animation animationFABAnticlock = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_rotate_anticlock);

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOpen) {
                    fabCamera.startAnimation(animationFABClose);
                    fabCamera.setClickable(false);
                    fabSelectPicture.startAnimation(animationFABClose);
                    fabSelectPicture.setClickable(false);
                    fabShare.startAnimation(animationFABClose);
                    fabShare.setClickable(false);
                    fabMenu.startAnimation(animationFABAnticlock);
                    isOpen = false;
                } else {
                    fabCamera.startAnimation(animationFABOpen);
                    fabCamera.setClickable(true);
                    fabSelectPicture.startAnimation(animationFABOpen);
                    fabSelectPicture.setClickable(true);
                    fabShare.startAnimation(animationFABOpen);
                    fabShare.setClickable(true);
                    fabMenu.startAnimation(animationFABClock);
                    isOpen = true;
                }
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCameraAllowed) {
                    dispatchTakePictureIntent();
                } else {
                    showRationale();
                }
            }
        });

        fabSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentPickPhoto, REQUEST_PICK_PHOTO);
            }
        });

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri fileUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        getString(R.string.provider_authority),
                        new File(getFilesDir(), LOG_FILENAME));

                if (fileUri != null) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.setType("text/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    startActivity(shareIntent);
                }
            }
        });
    }

    private void showRationale() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.message_rationale)
                .setPositiveButton(getString(R.string.button_title_allow), new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    }
                })
                .setNegativeButton(getString(R.string.button_title_block), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isCameraAllowed = false;
                    }
                })
                .create()
                .show();
    }
}