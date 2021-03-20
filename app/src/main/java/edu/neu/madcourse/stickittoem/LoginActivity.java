package edu.neu.madcourse.stickittoem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private String Token;
    private DatabaseReference mDatabase;
    private String username;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.editText);
        button = findViewById(R.id.button);

        // Get device ID
        //FirebaseMessaging.getToken(){} need to check here
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "getToken failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String token = task.getResult().getToken();
                        Token = token;
                    }
                });

        // Retrieve an instance of database using reference the location
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void login(View view) {
        if (Token == null) {
            return;
        }

        username = usernameEditText.getText().toString().trim();
        // Keep the previous username
        usernameEditText.setText(username);
        if (username.equals("")) {
            Toast.makeText(this, "Username can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child("users").child(username).child("token").setValue(Token);

        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot kv : dataSnapshot.getChildren()) {
                    if (kv.child("token").getValue(String.class).equals(Token) && !kv.getKey().equals(username)) {
                        mDatabase.child("users").child((kv.getKey())).child("token").setValue("offline");
                    }
                }

                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("device", Token);

                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
