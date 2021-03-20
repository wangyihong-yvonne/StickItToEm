package edu.neu.madcourse.stickittoem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private static final String SERVER_KEY = "key=AAAAvlNaC0Y:APA91bHK2AhlahN1V-TdKSX0K_OYLN0pBNhU7oGg97fbNlnm1lomD4iinPTX-phC_aR19pHWnQig5A38qIxNsA81yrilpkGJoR3VnM8GfG28DpcpSi9KsS9UddOlxT6dOdBg7SDYatVY";

    private String sender;
    private String senderToken;

    private String receiver;
    private String receiverToken;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // TODO  get username of sender from local storage
        Intent intent = getIntent();
        sender = intent.getStringExtra("username");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // get sender token
        mDatabase.child("users").child(sender).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check the existence of the receiver
                if (!snapshot.exists()) {
                    Toast.makeText(ChatActivity.this, "Sender " + sender + " does not exist!", Toast.LENGTH_SHORT).show();
                } else {
                    senderToken = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // TODO Need receiver from intent as this is in a single chat
        // get receiver token
        receiver = "test";

        mDatabase.child("users").child(receiver).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check the existence of the receiver
                if (!snapshot.exists()) {
                    Toast.makeText(ChatActivity.this, "Receiver " + receiver + " does not exist!", Toast.LENGTH_SHORT).show();
                } else {
                    receiverToken = snapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // TODO Load message history?
    }

    public void sendMessageToDevice(View type) {
        // TODO Check content sticker only
        final EditText editMessageText = findViewById(R.id.edit_message);
        final String editMessage = editMessageText.getText().toString().trim();
        sendMessageToDevice(sender, receiverToken, editMessage);
    }


    private void sendMessageToDevice(String sender, String targetToken, String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jPayload = new JSONObject();
                JSONObject jNotification = new JSONObject();
                JSONObject jdata = new JSONObject();
                try {
                    jNotification.put("title", "Message from " + sender);
                    jNotification.put("body", text);
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
                    // TODO save messgage to db
                    saveMessageToDB(text, System.currentTimeMillis());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void saveMessageToDB(String message, long timestamp) {
        // add 2 records to chats: chats-sender-receiver-messageID chats-receiver-sender-messageID
        String messageID = UUID.randomUUID().toString();
        DatabaseReference pushRefForSend = mDatabase.child("chats").child("send").child(sender).child(receiver).child("messages").push();
        pushRefForSend.setValue(messageID);

        if (!receiver.equals(sender)) {
            DatabaseReference pushRefForReceive = mDatabase.child("chats").child("send").child(receiver).child(sender).child("messages").push();
            pushRefForReceive.setValue(messageID);
        }

        // add message
        mDatabase.child("messages").child(messageID).child("content").setValue(message);
        mDatabase.child("messages").child(messageID).child("timestamp").setValue(timestamp);
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

}