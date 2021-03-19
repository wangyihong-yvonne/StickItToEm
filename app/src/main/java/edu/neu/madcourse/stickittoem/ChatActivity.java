package edu.neu.madcourse.stickittoem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private static final String SERVER_KEY = "key=AAAAvlNaC0Y:APA91bHK2AhlahN1V-TdKSX0K_OYLN0pBNhU7oGg97fbNlnm1lomD4iinPTX-phC_aR19pHWnQig5A38qIxNsA81yrilpkGJoR3VnM8GfG28DpcpSi9KsS9UddOlxT6dOdBg7SDYatVY";

    private static String TOKEN1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        TOKEN1 = task.getResult();

                        Toast.makeText(ChatActivity.this, TOKEN1, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void sendMessageToDevice(View type) {
        // TODO find user token

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice(TOKEN1);
            }
        }).start();
    }


    private void sendMessageToDevice(String targetToken) {
        // TODO Check content emoji only
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "Message Title");
            jNotification.put("body", "Message body ");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jdata.put("title", "data title");
            jdata.put("content", "data content");

            // If sending to a single client
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: " + resp);
                    Toast.makeText(ChatActivity.this, resp, Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

}