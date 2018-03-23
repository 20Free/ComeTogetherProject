package com.example.jonathan.cometogetherproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class NewMessageActivity extends AppCompatActivity {

    private EditText message;
    private ListView messageList;
    private ArrayList<HashMap<String, String>> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_message_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        message = (EditText) findViewById(R.id.message_text2);

        messages = new ArrayList<>();

        messageList = (ListView) findViewById(R.id.message_list2);
    }

    public void sendMessageNew(View view){
        HashMap<String, String> messageObject = new HashMap<>();
        String messageText = message.getText().toString();
        Date current = new Date(System.currentTimeMillis());
        String timeText = current.getHours() + ":" + current.getMinutes();
        messageObject.put(getString(R.string.key_message_text), messageText);
        messageObject.put(getString(R.string.key_message_time), timeText);
        messages.add(messageObject);
        MessageListAdapter messageListAdapter = new MessageListAdapter(this, messages);
        messageList.setAdapter(messageListAdapter);
        message.setText("");
    }
}