package edu.neu.madcourse.stickittoem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText Username;
    private String Token;
    private DatabaseReference mDatabase;
    private String username;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = findViewById(R.id.editText_username);
        button = findViewById(R.id.button_login);
    }
    //reference: https://firebase.google.com/docs/cloud-messaging/android/client
    public void login(View view) {
        username = Username.getText().toString().trim();
        // Keep the previous username
        Username.setText(username);
        if (username.equals("")) {
            Toast.makeText(this, "Username can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            Toast.makeText(LoginActivity.this, "getToken failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Token = task.getResult();

                        mDatabase = FirebaseDatabase.getInstance().getReference();

                        mDatabase.child("users").child(username).child("token").setValue(Token);


                        // TODO local storage
                        Intent intent = new Intent(LoginActivity.this, UserListActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("device", Token);
                        startActivity(intent);
                    }
                });
    }
}
