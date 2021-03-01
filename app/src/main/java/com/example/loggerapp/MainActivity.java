package com.example.loggerapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_FILENAME = "com.example.loggerapp.LOG_FILENAME";
    private String fileContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        writeToFile("onCreate()");

        findViewById(R.id.fab_activity_main_share_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri fileUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        "com.example.loggerapp.fileprovider",
                        new File(getFilesDir(), LOG_FILENAME));

                if (fileUri != null) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND, fileUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(shareIntent);
                }
            }
        });
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
        fileContent = fileContent.concat('\n' + body);
        try (FileOutputStream fos = openFileOutput(LOG_FILENAME, Context.MODE_PRIVATE)) {
            fos.write(fileContent.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}