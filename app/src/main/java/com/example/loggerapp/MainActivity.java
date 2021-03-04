package com.example.loggerapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;


public class MainActivity extends AppCompatActivity {

    public static final String LOG_FILENAME = "com.example.loggerapp.LOG_FILENAME";
    private static final int CODE_PICK_PHOTO = 100;
    private boolean isOpen = false;
    private AppCompatImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        writeToFile("onCreate()");
        setupFABMenu();
        imageView = findViewById(R.id.image_view_activity_main_show_image);
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
                Toast.makeText(MainActivity.this, "Camera clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fabSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentPickPhoto, CODE_PICK_PHOTO);
            }
        });

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri fileUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        "com.example.loggerapp.fileprovider",
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_PICK_PHOTO && resultCode == RESULT_OK && data != null) {
            Glide.with(MainActivity.this)
                    .load(data.getData())
                    .into(imageView);
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
}