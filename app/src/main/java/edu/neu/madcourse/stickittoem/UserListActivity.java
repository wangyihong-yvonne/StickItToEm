package edu.neu.madcourse.stickittoem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserListActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String sender;
    private String senderToken;

    private List<UserItem> itemList = new LinkedList<>();
    private RecyclerView recyclerView;
    private RviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManger;
    private FloatingActionButton refreshButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Intent intent = getIntent();
        sender = intent.getStringExtra("username");
        senderToken = intent.getStringExtra("device");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        refreshButton =  findViewById(R.id.refresh_button);
        init(true, false);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        itemList.clear();
        // async
        init(false, true);
    }

    private void init(boolean shouldCreateRecycler, boolean shouldNotifyDataChange) {
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                } else {

                    for (Map.Entry<String, Object> entry : ((Map<String, Object>) snapshot.getValue()).entrySet()) {
                        int stickerCount = 0;
                        String username = entry.getKey();
                        HashMap<String, Object> map = (HashMap<String, Object>) entry.getValue();

                        Map<String, Object> stickerMap = (Map<String, Object>) map.get("stickers");
                        if (stickerMap != null) {
                            Map<String, Object> stickerSendMap = (Map<String, Object>) stickerMap.get("send");
                            if (stickerSendMap != null) {
                                stickerCount = stickerSendMap.size();
                            }
                        }

                        UserItem userItem = new UserItem(0, username, "# number of stickers your friend sent: " + stickerCount);
                        itemList.add(userItem);

                        if (shouldCreateRecycler) {
                            createRecycler();
                        }
                        if (shouldNotifyDataChange) {
                            rviewAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void createRecycler() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.user_list_recycler);
        recyclerView.setHasFixedSize(true);

        rviewAdapter = new RviewAdapter(itemList);
        ItemClickListener itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    UserItem userItem = itemList.get(position);
                    Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
                    intent.putExtra("sender", sender);
                    intent.putExtra("receiver", userItem.getUsername());
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(UserListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        };
        rviewAdapter.setOnItemClickListener(itemClickListener);
        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManger);
    }

    public void refreshUserList(View view) {
        itemList.clear();
        init(false, true);
    }

}