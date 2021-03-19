package edu.neu.madcourse.stickittoem;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.BuildConfig;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        super.onCreate(savedInstanceState);
        // TODO Login layout
//        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            extractDataFromNotification(extras);
        }

    }

    public void openMessagingActivity(View view) {
        startActivity(new Intent(edu.neu.madcourse.stickittoem.MainActivity.this, MyFirebaseMessagingService.class));
    }


    private void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void extractDataFromNotification(Bundle extras) {
        String dataTitle = extras.getString("title", "Nothing");
        String dataContent = extras.getString("content", "Nothing");
        postToastMessage("Received : " + dataTitle + " " + dataContent);
    }

}
