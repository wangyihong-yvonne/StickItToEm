package edu.neu.madcourse.stickittoem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryChatActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String sender;
    private String receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_chat);

        Intent intent = getIntent();
        sender = intent.getStringExtra("sender");
        receiver = intent.getStringExtra("receiver");


        List<String> messageIDs = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        TextView historyWithView = (TextView) findViewById(R.id.history_with);
        historyWithView.setText(historyWithView.getText() + receiver);

        mDatabase.child("chats").child("send").child(sender).child(receiver).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                } else {
                    // show total stickers sent
                    Map<String, String> senderToReceiverMap = (Map<String, String>) snapshot.getValue();
//                    TextView totalView = (TextView) findViewById(R.id.num_total_stickers);
//                    totalView.setText(String.valueOf(senderToReceiverMap.size()));
                    messageIDs.addAll(senderToReceiverMap.values());

                    // get received messages from the current receiver
                    mDatabase.child("chats").child("receive").child(sender).child(receiver).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Map<String, String> receiverToSenderMap = (Map<String, String>) snapshot.getValue();
                                messageIDs.addAll(receiverToSenderMap.values());
                            }
                            // get each messages from db then display
                            for (String messageID : messageIDs) {
                                mDatabase.child("messages").child(messageID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                        } else {
                                            String content = snapshot.child("content").getValue(String.class);
                                            long timestamp = snapshot.child("timestamp").getValue(Long.class);
                                            String curSender = snapshot.child("sender").getValue(String.class);
                                            String curReceiver = snapshot.child("receiver").getValue(String.class);
                                            ChatMessage message = new ChatMessage(curSender, curReceiver, content, timestamp);

                                            String formattedTimestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp);
                                            StringBuilder cur = new StringBuilder();
                                            if (message.getSender().equals(sender)) {
                                                cur.append("You ").append(formattedTimestamp).append("\n");
                                                cur.append(message.getContent());
                                            } else {
                                                cur.append(message.getSender()).append(" ").append(formattedTimestamp).append("\n");
                                                cur.append(message.getContent());
                                            }
                                            cur.append("\n");
                                            LinearLayout layout = (LinearLayout) findViewById(R.id.history_chat_linear_layout);
                                            TextView textView = new TextView(HistoryChatActivity.this);
                                            textView.setText(cur.toString());
                                            layout.addView(textView);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }

                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}