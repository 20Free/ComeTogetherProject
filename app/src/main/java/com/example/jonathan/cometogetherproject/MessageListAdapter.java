package com.example.jonathan.cometogetherproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jonathan on 10/20/2016.
 */

public class MessageListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> data = new ArrayList<>();
    private Activity activity;
    private LayoutInflater inflater;

    public MessageListAdapter(Activity a, ArrayList<HashMap<String, String>> d){
        data = d;
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(convertView == null){
            view = inflater.inflate(R.layout.message_list_row, null);
        }

        TextView message = (TextView) view.findViewById(R.id.message);
        TextView time = (TextView) view.findViewById(R.id.time);

        HashMap<String, String> messageObject;
        messageObject = data.get(position);

        message.setText(messageObject.get(activity.getString(R.string.key_message_text)));
        time.setText(messageObject.get(activity.getString(R.string.key_message_time)));

        return view;
    }
}
