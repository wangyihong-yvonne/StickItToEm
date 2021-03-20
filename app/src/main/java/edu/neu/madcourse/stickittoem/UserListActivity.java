package edu.neu.madcourse.stickittoem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String sender;
    private String senderToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Intent intent = getIntent();
        sender = intent.getStringExtra("username");
        senderToken = intent.getStringExtra("device");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                } else {
                    List<User> userList = getUserList((Map<String,Object>) snapshot.getValue());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private List<User> getUserList(Map<String,Object> users) {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            String username = entry.getKey();
            HashMap<String, Object> map = (HashMap<String,Object>)entry.getValue();
            String token = (String) map.get("token");
            User user = new User(username, token);
            userList.add(user);

            LinearLayout layout = (LinearLayout) findViewById(R.id.user_list_constraint_layout);
            TextView textView = new TextView(UserListActivity.this);
            textView.setText(user.toString());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
                    intent.putExtra("sender", sender);
                    intent.putExtra("receiver", username);
                    startActivity(intent);
                }
            });
            layout.addView(textView);
        }
        return userList;
    }




}